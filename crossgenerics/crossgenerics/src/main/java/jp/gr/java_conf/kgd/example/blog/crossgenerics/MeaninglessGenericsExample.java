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

public class MeaninglessGenericsExample {

    // 引数の型をTにする意味がない
    public static <T> void printObject(T obj) {
        System.out.println(obj.toString());
    }

    // これと同じこと
    public static void printObject2(Object obj) {
        System.out.println(obj.toString());
    }

    // 上限境界があっても同じ
    public static <T extends Number> void printNumberAsInt(T number) {
        System.out.println(number.intValue());
    }

    // これも同じこと
    public static void printNumberAsInt2(Number number) {
        System.out.println(number.intValue());
    }

    // 戻り値を受け取る側の型で型推論させる場合はそれなりに意味があります。
    // ただこれは本質的な型安全ではないです。
    // JavaFXのFXMLLoader#loadはめちゃくちゃ危ないと思います。
    public static <T> T newInstance(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (T) Class.forName(className).newInstance();
    }
}
