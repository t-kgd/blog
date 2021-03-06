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

package jp.gr.java_conf.kgd.example.blog.dynamicproxy;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProxyUtilTest {

    @Test
    public void interceptorTest() {
        List<String> reactions = new ArrayList<>();
        List<String> target = Arrays.asList("foo", "bar");

        ProxyHolder<List<String>> sut = ProxyUtil.createLazyProxyHolder(() -> target);
        sut.getOnPreInvokeListeners().add((w, m, a) -> reactions.add("preInvoke: " + m.getName() + "(" + a[0] + ")"));
        sut.getOnPostInvokeListeners().add((w, m, a, r, e) -> reactions.add("postInvoke: " + "result" + " -> " + r));

        List<String> proxy = sut.getProxy();

        // Fire interceptor.
        proxy.get(0);
        Assert.assertEquals("preInvoke: get(0)", reactions.get(0));
        Assert.assertEquals("postInvoke: result -> foo", reactions.get(1));

        // Fire interceptor.
        proxy.get(1);
        Assert.assertEquals("preInvoke: get(1)", reactions.get(2));
        Assert.assertEquals("postInvoke: result -> bar", reactions.get(3));
    }

    @Test
    public void errorHandleTest() {
        List<String> reactions = new ArrayList<>();
        List<String> target = Arrays.asList("foo", "bar");

        ProxyHolder<List<String>> sut = ProxyUtil.createLazyProxyHolder(() -> target);
        sut.getErrorHandlers().add((w, m, a, t, r) -> {
            reactions.add(t.getClass().getSimpleName());
            return ErrorHandler.Result.stop(r);
        });
        sut.getErrorHandlers().add((w, m, a, t, r) -> {
            // Unreachable
            reactions.add(t.getClass().getSimpleName());
            return ErrorHandler.Result.through(r);
        });

        List<String> proxy = sut.getProxy();

        // Fire error handler.
        proxy.add("baz");
        Assert.assertEquals("UnsupportedOperationException", reactions.get(0));
        Assert.assertEquals(1, reactions.size());

        // Fire error handler.
        proxy.get(10);
        Assert.assertEquals("ArrayIndexOutOfBoundsException", reactions.get(1));
    }
}
