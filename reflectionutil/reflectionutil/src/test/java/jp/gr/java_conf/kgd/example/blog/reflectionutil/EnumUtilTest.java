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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EnumUtilTest {

    enum Foo {
        A, B, C
    }

    @Test
    public void invokeValuesTest() {
        {
            Foo[] actual = EnumUtil.invokeValues(Foo.class);
            assertThat(Arrays.asList(actual), is(contains(Foo.A, Foo.B, Foo.C)));
        }

        {
            // 型推論版
            Foo[] actual = EnumUtil.invokeValues();
            assertThat(Arrays.asList(actual), is(contains(Foo.A, Foo.B, Foo.C)));
        }
    }

    @Test
    public void enumValueCacheTest() {
        EnumUtil.EnumValuesCache sut = new EnumUtil.EnumValuesCache();
        List<Foo> a = sut.getValues(Foo.class);
        List<Foo> b = sut.getValues(Foo.class);

        // キャッシュしてるので同一インスタンスが返るはず
        assertThat(a, is(sameInstance(b)));
    }

    @Test
    public void createEnumMapTest() {
        Map<Foo, String> actual = EnumUtil.createEnumMap(k -> k.name().toLowerCase());
        assertThat(actual.keySet(), is(contains(Foo.A, Foo.B, Foo.C)));
        assertThat(actual.values(), is(contains("a", "b", "c")));
    }
}





















