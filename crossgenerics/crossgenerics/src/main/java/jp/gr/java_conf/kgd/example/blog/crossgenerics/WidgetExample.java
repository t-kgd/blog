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

package jp.gr.java_conf.kgd.example.blog.crossgenerics;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

public class WidgetExample {

    public static <T extends Actor & Layout> void doSomething1(T widget) {
        // Actorのメソッド。Layoutでは使えない。
        widget.act(0);

        // Layoutのメソッド。Actorでは使えない。
        widget.getPrefWidth();
    }

    // 一応、ジェネリクス無しで実現する場合はこんな風になる。
    // （actorViewとlayoutViewに同じインスタンスを渡して呼び出す）
    public static void doSomething2(Actor actorView, Layout layoutView) {
        // 違うインスタンスが渡せてしまうので、チェックする
        if (actorView != layoutView) {
            throw new IllegalArgumentException("actorViewとlayoutViewは同じインスタンスでなければいけません");
        }

        // 以下、同じ処理
        actorView.act(0);
        layoutView.getPrefWidth();
    }

    public static void main(String[] args) {
        Widget widget = new Widget();

        WidgetGroup widgetGroup = new WidgetGroup();

        // どっちでも呼べる
        doSomething1(widget);
        doSomething1(widgetGroup);

        // 一応、ジェネリクスを使わなくてもこんな感じの呼び出しで実現できる
        doSomething2(widget, widget);
        doSomething2(widgetGroup, widgetGroup);
        // でもさすがにおかしすぎんよ～
    }
}
