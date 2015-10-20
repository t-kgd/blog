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

package jp.gr.java_conf.kgd.example.blog.kotlinvalvar.kotlin.etc

import jp.gr.java_conf.kgd.example.blog.kotlinvalvar.Foo
import jp.gr.java_conf.kgd.example.blog.kotlinvalvar.ListenerManager
import jp.gr.java_conf.kgd.example.blog.kotlinvalvar.ListenerRegister
import jp.gr.java_conf.kgd.example.blog.kotlinvalvar.MutableFoo
import jp.gr.java_conf.kgd.example.blog.kotlinvalvar.ObservableMutableFoo
import jp.gr.java_conf.kgd.example.blog.kotlinvalvar.SimpleListenerManager
import java.util.function.Consumer

class MutableFooToObservableDecorator(
        private val foo: MutableFoo,
        private val listenerManager: ListenerManager<Consumer<Foo>> = SimpleListenerManager()
) :
        ObservableMutableFoo,
        MutableFoo by foo,
        ListenerRegister<Consumer<Foo>> by listenerManager {

    override fun setName(name: String?) {
        foo.name = name
        listenerManager.processListeners { it.accept(this) }
    }
}