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

package jp.gr.java_conf.kgd.example.blog.implicittypeparameter.java;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class JavaExample {

    public static void utilExample() {
        AssetManager assetManager = new AssetManager();
        AssetManagerUtil.<Texture>load(assetManager, "foo.png");
//        assetManager.finishLoading();
        Texture texture = AssetManagerUtil.get(assetManager, "foo.png");
        // use texture
    }

    public static void helperExample() {
        AssetManager assetManager = new AssetManager();
        AssetManagerHelper assetManagerHelper = new AssetManagerHelper(assetManager);
        assetManagerHelper.<Texture>load("foo.png");
//        assetManager.finishLoading();
        Texture texture = assetManagerHelper.get("foo.png");
        // etc
    }

    // ラップして不格好なdummyを隠してみる
    public static <T> void load(AssetManager assetManager, String filename) {
        AssetManagerUtil.<T>load(assetManager, filename);
    }

    public static void badExample() {
        AssetManager assetManager = new AssetManager();
        JavaExample.<Texture>load(assetManager, "foo.png");
        // ラップしてdummyを隠したメソッドに明示的に型を指定しても、TがObject扱いになります。
    }
}
