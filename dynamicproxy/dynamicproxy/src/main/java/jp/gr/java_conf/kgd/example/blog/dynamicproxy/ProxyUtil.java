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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class ProxyUtil {

    // [private]
    private static <T> ProxyHolder<T> createProxyHolder(Supplier<? extends T> lazyWrapped, Class<?> clazz) {
        Class<?>[] interfaces = ReflectionUtil.extractInterfaces(clazz);
        Handler<T> handler = new Handler<>(lazyWrapped);
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, handler);
        ProxyHolder<T> proxyHolder = new SimpleProxyHolder<>(proxy, handler);
        return proxyHolder;
    }

    /**
     * 動的プロキシを生成する。
     * <p>
     * 任意のインスタンスをラップし、メソッドの前後に処理を挟むことのできるプロキシを生成します。
     * <code>lazyWrapped#get</>は、プロキシのメソッドが呼びだされるたびに呼び出されます。
     *
     * @param lazyWrapped
     * @param dummy       型情報を受け取るためのダミーの引数。
     * @param <T>         インターフェースである必要があります。
     * @return 生成したプロキシを包含したインスタンス。
     */
    public static <T> ProxyHolder<T> createLazyProxyHolder(Supplier<? extends T> lazyWrapped, T... dummy) {
        return createProxyHolder(lazyWrapped, dummy.getClass().getComponentType());
    }

    /**
     * 動的プロキシを生成する。
     * <p>
     * 任意のインスタンスをラップし、メソッドの前後に処理を挟むことのできるプロキシを生成します。
     *
     * @param wrapped
     * @param <T>
     * @return
     */
    public static <T> ProxyHolder<T> createProxyHolder(T wrapped) {
        return createProxyHolder(() -> wrapped, wrapped.getClass());
    }

    /**
     * 動的プロキシを生成する。
     * <p>
     * {@link #createProxyHolder(Object)}の簡易版です。
     * あらかじめ、メソッドの前後に挟みこむ処理を指定して、プロキシを生成します。
     *
     * @param wrapped
     * @param onPreInvokeListener
     * @param onPostInvokeListener
     * @param <T>
     * @return
     */
    public static <T> T createProxy(T wrapped, OnPreInvokeListener<? super T> onPreInvokeListener, OnPostInvokeListener<? super T> onPostInvokeListener) {
        ProxyHolder<T> proxyHolder = createProxyHolder(wrapped);
        proxyHolder.getOnPreInvokeListeners().add(onPreInvokeListener);
        proxyHolder.getOnPostInvokeListeners().add(onPostInvokeListener);
        return proxyHolder.getProxy();
    }

    /*
     * インターセプタの処理を前後に挟むハンドラ。
     */
    private static class Handler<T> implements InvocationHandler {

        private final Supplier<? extends T> wrapped;

        private Lazy<List<OnPreInvokeListener<? super T>>> onPreInvokeListeners = new AtomicLazy<>(() -> new LinkedList<>());

        private Lazy<List<OnPostInvokeListener<? super T>>> onPostInvokeListeners = new AtomicLazy<>(() -> new LinkedList<>());

        private Lazy<List<ErrorHandler<? super T>>> errorHandlers = new AtomicLazy<>(() -> new LinkedList<>());

        public Handler(Supplier<? extends T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // メソッド前インターセプタ
            if (onPreInvokeListeners.isEvaluated()) {
                onPreInvokeListeners.get().forEach(listener -> listener.onPreInvoke(wrapped.get(), method, args));
            }

            Object result = ErrorHandler.Result.UNDEFINED;
            boolean isErrorOccurred = false;
            try {
                // メソッドの実行
                result = method.invoke(wrapped.get(), args);
            } catch (InvocationTargetException e) {
                // 例外が発生したのでエラーハンドラで処理する。
                isErrorOccurred = true;
                if (errorHandlers.isEvaluated()) {
                    for (ErrorHandler<? super T> errorHandler : errorHandlers.get()) {
                        ErrorHandler.Result tempResult = errorHandler.onHandleError(wrapped.get(), method, args, e.getTargetException(), result);
                        result = tempResult.getValue();
                        if (tempResult.stopFlooding()) break;
                    }
                }
            } finally {
                if (onPostInvokeListeners.isEvaluated()) {
                    // ラムダ用に実質的finalな変数に代入する
                    Object result2 = result;
                    boolean isErrorOccurred2 = isErrorOccurred;
                    onPostInvokeListeners.get().forEach(listener -> listener.onPostInvoke(wrapped.get(), method, args, result2, isErrorOccurred2));
                }
            }

            // 一度も適切な代替戻り値が指定されていない場合、デフォルト値で返す。
            if (result == ErrorHandler.Result.UNDEFINED) {
                return ReflectionUtil.getDefaultValue(method.getReturnType());
            } else {
                return result;
            }
        }

        public List<OnPostInvokeListener<? super T>> getOnPostInvokeListeners() {
            return onPostInvokeListeners.get();
        }

        public List<OnPreInvokeListener<? super T>> getOnPreInvokeListeners() {
            return onPreInvokeListeners.get();
        }

        public List<ErrorHandler<? super T>> getErrorHandlers() {
            return errorHandlers.get();
        }
    }

    /*
     * 動的プロキシに機能（例えばインターセプタの付け替え）を追加したくても、
     * コンパイル時は既存のインターフェースとしてしか振る舞えないので、
     * ラッパーを用意してラッパーに機能を持たせることにする。
     */
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

        @Override
        public List<ErrorHandler<? super T>> getErrorHandlers() {
            return handler.getErrorHandlers();
        }
    }
}
