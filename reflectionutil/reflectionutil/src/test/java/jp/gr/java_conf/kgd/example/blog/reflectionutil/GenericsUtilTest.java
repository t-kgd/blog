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

package jp.gr.java_conf.kgd.example.blog.reflectionutil;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class GenericsUtilTest {

    interface Foo<F0> {
    }

    interface Bar<B0, B1> {
    }

    class Hoge<H0> {
    }

    class SimpleFoo implements Foo<String> {
    }

    class SimpleBar implements Bar<Integer, Double> {
    }

    class SimpleHogeEx extends Hoge<Float> {
    }

    interface ChainExampleInterface<C> extends Foo<C> {
    }

    class ChainExample0<C0> extends Hoge<C0> implements ChainExampleInterface<Byte> {
    }

    class ChainExample1<C1> extends ChainExample0<Boolean> implements Bar<C1, Short> {
    }

    class ChainExample2 extends ChainExample1<Character> {
    }

    // Fooは冗長な実装だけど、Class#getInterfaces()で直接取れるようになる
    class ReimplementsExample extends ChainExample2 implements Foo<Byte> {
    }

    class ListFoo implements Foo<List<String>> {
    }

    @Test
    public void hierarchyTest() {
        {
            List<Class<?>> actual = GenericsUtil.getClassHierarchy(SimpleFoo.class, Foo.class);
            assertThat(actual, is(contains(SimpleFoo.class, Foo.class)));
        }

        {
            List<Class<?>> actual = GenericsUtil.getClassHierarchy(SimpleHogeEx.class, Hoge.class);
            assertThat(actual, is(contains(SimpleHogeEx.class, Hoge.class)));
        }

        {
            List<Class<?>> actual = GenericsUtil.getClassHierarchy(ChainExample1.class, Foo.class);
            assertThat(actual, is(contains(ChainExample1.class, ChainExample0.class, ChainExampleInterface.class, Foo.class)));
        }

        {
            List<Class<?>> actual = GenericsUtil.getClassHierarchy(ReimplementsExample.class, Foo.class);
            // 再度、直接実装してるので、直接繋がる
            assertThat(actual, is(contains(ReimplementsExample.class, Foo.class)));
        }
    }

    @Test
    public void getTypeParameterTypeTest() {
        {
            Type actual = GenericsUtil.getTypeParameterType(SimpleFoo.class, Foo.class, "F0");
            String actualName = actual.getTypeName();
            assertThat(actualName, is("java.lang.String"));
        }

        {
            Type actual = GenericsUtil.getTypeParameterType(SimpleBar.class, Bar.class, "B0");
            String actualName = actual.getTypeName();
            assertThat(actualName, is("java.lang.Integer"));
        }

        {
            Type actual = GenericsUtil.getTypeParameterType(SimpleBar.class, Bar.class, "B1");
            String actualName = actual.getTypeName();
            assertThat(actualName, is("java.lang.Double"));
        }

        {
            Type actual = GenericsUtil.getTypeParameterType(ChainExample2.class, Foo.class, "F0");
            String actualName = actual.getTypeName();
            assertThat(actualName, is("java.lang.Byte"));
        }

        {
            // Foo<F0>のF0をChainExampleInterface<C>のCで再束縛している時
            Type actual = GenericsUtil.getTypeParameterType(ChainExample2.class, ChainExampleInterface.class, "C");
            String actualName = actual.getTypeName();
            assertThat(actualName, is("java.lang.Byte"));
        }

        {
            Type actual = GenericsUtil.getTypeParameterType(ChainExample2.class, Bar.class, "B0");
            String actualName = actual.getTypeName();
            assertThat(actualName, is("java.lang.Character"));
        }

        {
            // Bar<B0, B1>のB0をChainExample1<C1>のC1で再束縛している時
            Type actual = GenericsUtil.getTypeParameterType(ChainExample2.class, ChainExample1.class, "C1");
            String actualName = actual.getTypeName();
            assertThat(actualName, is("java.lang.Character"));
        }

        {
            Type actual = GenericsUtil.getTypeParameterType(ChainExample2.class, Bar.class, "B1");
            String actualName = actual.getTypeName();
            assertThat(actualName, is("java.lang.Short"));
        }

        {
            Type actual = GenericsUtil.getTypeParameterType(ListFoo.class, Foo.class, "F0");
            String actualName = actual.getTypeName();
            // 束縛されていれば、型パラメータの中身の情報も持っている
            assertThat(actualName, is("java.util.List<java.lang.String>"));
        }
    }
}




