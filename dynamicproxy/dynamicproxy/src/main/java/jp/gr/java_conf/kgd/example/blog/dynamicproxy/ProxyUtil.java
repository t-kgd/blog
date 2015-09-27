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

    // [private] リスナを外から渡すパターン
    private static <T> T createProxy(Supplier<? extends T> lazyWrapped, Class<?> clazz,
                                     List<OnPreInvokeListener<? super T>> onPreInvokeListeners,
                                     List<OnPostInvokeListener<? super T>> onPostInvokeListeners,
                                     List<ErrorHandler<? super T>> errorHandlers) {
        Class<?>[] interfaces = ReflectionUtil.extractInterfaces(clazz);
        Handler<T> handler = new Handler<>(lazyWrapped, onPreInvokeListeners, onPostInvokeListeners, errorHandlers);
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, handler);
        return proxy;
    }

    // [private] リスナを後から取り出せるようにするパターン
    private static <T> ProxyHolder<T> createProxyHolder(Supplier<? extends T> lazyWrapped, Class<?> clazz,
                                                        List<OnPreInvokeListener<? super T>> onPreInvokeListeners,
                                                        List<OnPostInvokeListener<? super T>> onPostInvokeListeners,
                                                        List<ErrorHandler<? super T>> errorHandlers) {
        Class<?>[] interfaces = ReflectionUtil.extractInterfaces(clazz);
        Handler<T> handler = new Handler<>(lazyWrapped, onPreInvokeListeners, onPostInvokeListeners, errorHandlers);
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, handler);
        ProxyHolder<T> proxyHolder = new SimpleProxyHolder<>(proxy, handler);
        return proxyHolder;
    }

    /**
     * 動的プロキシを生成する。
     * <p>
     * 任意のインスタンスをラップし、メソッドの前後に処理を挟むことのできるプロキシを生成します。
     * <code>lazyWrapped#get</>は、プロキシのメソッドが呼びだされるたびに呼び出されます。
     * 各リスナーのListインスタンスはそのままプロキシに参照されるので、要素に変更を加えればプロキシのリスナを変更することができます。
     *
     * @param lazyWrapped
     * @param onPreInvokeListeners
     * @param onPostInvokeListeners
     * @param errorHandlers
     * @param dummy                 型情報を受け取るためのダミーの引数。
     * @param <T>                   インターフェースである必要があります。
     * @return 生成したプロキシ。
     */
    public static <T> T createLazyProxy(Supplier<? extends T> lazyWrapped,
                                        List<OnPreInvokeListener<? super T>> onPreInvokeListeners,
                                        List<OnPostInvokeListener<? super T>> onPostInvokeListeners,
                                        List<ErrorHandler<? super T>> errorHandlers,
                                        T... dummy) {
        return createProxy(lazyWrapped, dummy.getClass().getComponentType(), onPreInvokeListeners, onPostInvokeListeners, errorHandlers);
    }

    /**
     * 動的プロキシを生成する。
     * <p>
     * 任意のインスタンスをラップし、メソッドの前後に処理を挟むことのできるプロキシを生成します。
     * <code>lazyWrapped#get</>は、プロキシのメソッドが呼びだされるたびに呼び出されます。
     * 生成したプロキシは{@link ProxyHolder}に包含されて返されます。
     *
     * @param lazyWrapped
     * @param onPreInvokeListeners
     * @param onPostInvokeListeners
     * @param errorHandlers
     * @param dummy                 型情報を受け取るためのダミーの引数。
     * @param <T>                   インターフェースである必要があります。
     * @return 生成したプロキシを包含したインスタンス。
     */
    public static <T> ProxyHolder<T> createLazyProxyHolder(Supplier<? extends T> lazyWrapped,
                                                           List<OnPreInvokeListener<? super T>> onPreInvokeListeners,
                                                           List<OnPostInvokeListener<? super T>> onPostInvokeListeners,
                                                           List<ErrorHandler<? super T>> errorHandlers,
                                                           T... dummy) {
        return createProxyHolder(lazyWrapped, dummy.getClass().getComponentType(), onPreInvokeListeners, onPostInvokeListeners, errorHandlers);
    }


    /**
     * 動的プロキシを生成する。
     * <p>
     * 空のリスナーListを生成して{@link #createLazyProxy(Supplier, List, List, List, Object[])}に委譲します。
     *
     * @param lazyWrapped
     * @param dummy       型情報を受け取るためのダミーの引数。
     * @param <T>         インターフェースである必要があります。
     * @return 生成したプロキシを包含したインスタンス。
     */
    public static <T> T createLazyProxy(Supplier<? extends T> lazyWrapped, T... dummy) {
        return createProxy(lazyWrapped, dummy.getClass().getComponentType(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
    }

    /**
     * 動的プロキシを生成する。
     * <p>
     * 空のリスナーListを生成して{@link #createLazyProxyHolder(Supplier, List, List, List, Object[])}に委譲します。
     *
     * @param lazyWrapped
     * @param dummy       型情報を受け取るためのダミーの引数。
     * @param <T>         インターフェースである必要があります。
     * @return 生成したプロキシを包含したインスタンス。
     */
    public static <T> ProxyHolder<T> createLazyProxyHolder(Supplier<? extends T> lazyWrapped, T... dummy) {
        return createProxyHolder(lazyWrapped, dummy.getClass().getComponentType(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
    }

    public static <T> T createProxy(T wrapped,
                                    List<OnPreInvokeListener<? super T>> onPreInvokeListeners,
                                    List<OnPostInvokeListener<? super T>> onPostInvokeListeners,
                                    List<ErrorHandler<? super T>> errorHandlers) {
        return createProxy(() -> wrapped, wrapped.getClass(), onPreInvokeListeners, onPostInvokeListeners, errorHandlers);
    }

    public static <T> ProxyHolder<T> createProxyHolder(T wrapped,
                                                       List<OnPreInvokeListener<? super T>> onPreInvokeListeners,
                                                       List<OnPostInvokeListener<? super T>> onPostInvokeListeners,
                                                       List<ErrorHandler<? super T>> errorHandlers) {
        return createProxyHolder(() -> wrapped, wrapped.getClass(), onPreInvokeListeners, onPostInvokeListeners, errorHandlers);
    }

    public static <T> T createProxy(T wrapped) {
        return createProxy(() -> wrapped, wrapped.getClass(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
    }

    public static <T> ProxyHolder<T> createProxyHolder(T wrapped) {
        return createProxyHolder(() -> wrapped, wrapped.getClass(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
    }

    /*
     * インターセプタの処理を前後に挟むハンドラ。
     */
    private static class Handler<T> implements InvocationHandler {

        private final Supplier<? extends T> wrapped;

        private List<OnPreInvokeListener<? super T>> onPreInvokeListeners;

        private List<OnPostInvokeListener<? super T>> onPostInvokeListeners;

        private List<ErrorHandler<? super T>> errorHandlers;

        public Handler(Supplier<? extends T> wrapped,
                       List<OnPreInvokeListener<? super T>> onPreInvokeListeners,
                       List<OnPostInvokeListener<? super T>> onPostInvokeListeners,
                       List<ErrorHandler<? super T>> errorHandlers) {
            this.wrapped = wrapped;
            this.onPreInvokeListeners = onPreInvokeListeners;
            this.onPostInvokeListeners = onPostInvokeListeners;
            this.errorHandlers = errorHandlers;
        }

        public Handler(Supplier<? extends T> wrapped) {
            this(wrapped, new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // メソッド前インターセプタ
            onPreInvokeListeners.forEach(listener -> listener.onPreInvoke(wrapped.get(), method, args));

            Object result = ErrorHandler.Result.UNDEFINED;
            Throwable targetException = null;
            try {
                // メソッドの実行
                result = method.invoke(wrapped.get(), args);
            } catch (InvocationTargetException e) {
                // 例外が発生したのでエラーハンドラで処理する。
                targetException = e.getTargetException();

                //エラーハンドラが１つもなかったら例外をそのままスローする
                if (errorHandlers.isEmpty()) {
                    throw targetException;
                }

                for (ErrorHandler<? super T> errorHandler : errorHandlers) {
                    ErrorHandler.Result tempResult = errorHandler.onHandleError(wrapped.get(), method, args, targetException, result);
                    result = tempResult.getValue();
                    if (tempResult.stopFlooding()) break;
                }

            } finally {
                // ラムダ用に実質的finalな変数に代入する
                Object result2 = result;
                Throwable targetException2 = targetException;
                onPostInvokeListeners.forEach(listener -> listener.onPostInvoke(wrapped.get(), method, args, result2, targetException2));

            }

            // 一度も適切な代替戻り値が指定されていない場合、デフォルト値で返す。
            if (result == ErrorHandler.Result.UNDEFINED) {
                return ReflectionUtil.getDefaultValue(method.getReturnType());
            } else {
                return result;
            }
        }

        public List<OnPostInvokeListener<? super T>> getOnPostInvokeListeners() {
            return onPostInvokeListeners;
        }

        public List<OnPreInvokeListener<? super T>> getOnPreInvokeListeners() {
            return onPreInvokeListeners;
        }

        public List<ErrorHandler<? super T>> getErrorHandlers() {
            return errorHandlers;
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
