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

import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public class CrossExample {

    interface Foo {
        void foo();
    }

    interface Bar {
        void bar();
    }

    // 上限境界を指定する際に「&」でつなげます（classは1つまで、interfaceはいくつでも可）
    public static <T extends Foo & Bar> void doFoobar(T foobar) {
        // 上限境界がFooとBarの両方なので、両方のメソッドが使える
        foobar.foo();
        foobar.bar();
    }

    public static class FooImpl implements Foo {
        @Override
        public void foo() {
        }
    }

    public static class FoobarImpl implements Foo, Bar {
        @Override
        public void foo() {
        }

        @Override
        public void bar() {

        }
    }

    public static void main(String[] args) {

        FooImpl foo = new FooImpl();
//        doFoobar(foo);
        // コンパイルエラー！型FooImplはBarが実装されていない

        FoobarImpl foobar = new FoobarImpl();
        doFoobar(foobar);
        // OK！型FoobarImplはFooとBarの両方が実装されている
    }


    // リストからランダムに値を取得したいが……
    public static <E> E getRandom(List<E> list) {
        Random random = new Random();
        int index = random.nextInt(list.size());
        E result = list.get(index);  // ←この部分のオーダーがわからない！
        return result;
        // ちなみに、ランダムアクセスできないのであれば次のようにするのが普通かもしれません。
//        int count = 0;
//        for (E e : list) {
//            if (count++ == index) return e;
//        }
//        throw new AssertionError();
    }

    /**
     * リストからランダムに値を取得する。
     *
     * @param list ランダムアクセス可能なリスト
     * @param <E>  リスト内の要素の型
     * @param <T>  ランダムアクセス可能なリストの型
     * @return ランダムに選ばれた要素
     */
    public static <E, T extends List<E> & RandomAccess> E getRandom2(T list) {
        Random random = new Random();
        int index = random.nextInt(list.size());
        // TはRandomAccessを継承しているListなため、O(1)が確定している！！
        E result = list.get(index);
        return result;
    }
}
