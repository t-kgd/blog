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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class ProxyUtil {

    /**
     * インターセプタの処理を前後に挟むハンドラ。
     *
     * @param <T>
     */
    private static class Handler<T> implements InvocationHandler {

        private final Supplier<? extends T> lazyWrapped;

        private final List<OnPreInvokeListener<? super T>> onPreInvokeListeners = new LinkedList<>();

        private final List<OnPostInvokeListener<? super T>> onPostInvokeListeners = new LinkedList<>();

        public Handler(Supplier<? extends T> lazyWrapped) {
            this.lazyWrapped = lazyWrapped;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            onPreInvokeListeners.forEach(listener -> listener.onPreInvoke(lazyWrapped.get(), method, args));
            Object result = method.invoke(lazyWrapped.get(), args);
            onPostInvokeListeners.forEach(listener -> listener.onPostInvoke(lazyWrapped.get(), method, args, result));
            return result;
        }

        public List<OnPostInvokeListener<? super T>> getOnPostInvokeListeners() {
            return onPostInvokeListeners;
        }

        public List<OnPreInvokeListener<? super T>> getOnPreInvokeListeners() {
            return onPreInvokeListeners;
        }
    }

    private static class SimpleProxyHolder<T> implements ProxyHolder<T> {

        private final T proxy;

        private final Handler<T> handler;

        private SimpleProxyHolder(T proxy, Handler<T> handler) {
            this.proxy = proxy;
            this.handler = handler;
        }

        @Override
        public T getProxy() {
            return proxy;
        }

        @Override
        public List<OnPostInvokeListener<? super T>> getOnPostInvokeListeners() {
            return handler.getOnPostInvokeListeners();
        }

        @Override
        public List<OnPreInvokeListener<? super T>> getOnPreInvokeListeners() {
            return handler.getOnPreInvokeListeners();
        }
    }

    public static <T> ProxyHolder<T> createProxyHolder(Supplier<? extends T> lazyWrapped, Class<T> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        Handler<T> handler = new Handler<>(lazyWrapped);
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, handler);
        ProxyHolder<T> proxyHolder = new SimpleProxyHolder<>(proxy, handler);
        return proxyHolder;
    }

    public static <T> ProxyHolder<T> createProxyHolder(Supplier<? extends T> lazyWrapped, T... dummy) {
        return createProxyHolder(lazyWrapped, (Class<T>) dummy.getClass().getComponentType());
    }

    public static <T> ProxyHolder<T> createProxyHolder(T wrapped) {
        return createProxyHolder(() -> wrapped, (Class<T>) wrapped.getClass());
    }
}
