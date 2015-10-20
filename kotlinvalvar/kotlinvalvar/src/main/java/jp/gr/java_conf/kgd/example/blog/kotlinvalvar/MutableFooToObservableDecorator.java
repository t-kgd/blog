/*
 * The MIT License
 *
 * Copyright (c) 2015 Misakura.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jp.gr.java_conf.kgd.example.blog.kotlinvalvar;

import java.util.function.Consumer;

// MutableFooを包含して、Observableの機能を付加する
public class MutableFooToObservableDecorator implements ObservableMutableFoo {

    // MutableFooのメソッドを委譲する
    private MutableFoo foo;

    // ListenerRegisterのメソッドを委譲する。内部ではManagerとして利用する
    private ListenerManager<Consumer<Foo>> listenerManager = new SimpleListenerManager<>();

    public MutableFooToObservableDecorator(MutableFoo foo) {
        this.foo = foo;
    }

    // RegisterのメソッドはManagerに委譲して実装する
    @Override
    public void addListener(Consumer<Foo> listener) {
        listenerManager.addListener(listener);
    }

    // 同じく、RegisterのメソッドはManagerに委譲して実装する
    @Override
    public void removeListener(Consumer<Foo> listener) {
        listenerManager.removeListener(listener);
    }

    // fooに委譲
    @Override
    public String getName() {
        return foo.getName();
    }

    // fooに委譲するけど、ちょっと処理を追加
    @Override
    public void setName(String name) {
        foo.setName(name);
        // 変更を通知
        listenerManager.processListeners(listener -> listener.accept(this));
    }
}
