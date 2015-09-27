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
     * ���I�v���L�V�𐶐�����B
     * <p>
     * �C�ӂ̃C���X�^���X�����b�v���A���\�b�h�̑O��ɏ��������ނ��Ƃ̂ł���v���L�V�𐶐����܂��B
     * <code>lazyWrapped#get</>�́A�v���L�V�̃��\�b�h���Ăт�����邽�тɌĂяo����܂��B
     *
     * @param lazyWrapped
     * @param dummy       �^�����󂯎�邽�߂̃_�~�[�̈����B
     * @param <T>         �C���^�[�t�F�[�X�ł���K�v������܂��B
     * @return ���������v���L�V���܂����C���X�^���X�B
     */
    public static <T> ProxyHolder<T> createLazyProxyHolder(Supplier<? extends T> lazyWrapped, T... dummy) {
        return createProxyHolder(lazyWrapped, dummy.getClass().getComponentType());
    }

    /**
     * ���I�v���L�V�𐶐�����B
     * <p>
     * �C�ӂ̃C���X�^���X�����b�v���A���\�b�h�̑O��ɏ��������ނ��Ƃ̂ł���v���L�V�𐶐����܂��B
     *
     * @param wrapped
     * @param <T>
     * @return
     */
    public static <T> ProxyHolder<T> createProxyHolder(T wrapped) {
        return createProxyHolder(() -> wrapped, wrapped.getClass());
    }

    /**
     * ���I�v���L�V�𐶐�����B
     * <p>
     * {@link #createProxyHolder(Object)}�̊ȈՔłł��B
     * ���炩���߁A���\�b�h�̑O��ɋ��݂��ޏ������w�肵�āA�v���L�V�𐶐����܂��B
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
     * �C���^�[�Z�v�^�̏�����O��ɋ��ރn���h���B
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
            // ���\�b�h�O�C���^�[�Z�v�^
            if (onPreInvokeListeners.isEvaluated()) {
                onPreInvokeListeners.get().forEach(listener -> listener.onPreInvoke(wrapped.get(), method, args));
            }

            Object result = ErrorHandler.Result.UNDEFINED;
            boolean isErrorOccurred = false;
            try {
                // ���\�b�h�̎��s
                result = method.invoke(wrapped.get(), args);
            } catch (InvocationTargetException e) {
                // ��O�����������̂ŃG���[�n���h���ŏ�������B
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
                    // �����_�p�Ɏ����Ifinal�ȕϐ��ɑ������
                    Object result2 = result;
                    boolean isErrorOccurred2 = isErrorOccurred;
                    onPostInvokeListeners.get().forEach(listener -> listener.onPostInvoke(wrapped.get(), method, args, result2, isErrorOccurred2));
                }
            }

            // ��x���K�؂ȑ�֖߂�l���w�肳��Ă��Ȃ��ꍇ�A�f�t�H���g�l�ŕԂ��B
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
     * ���I�v���L�V�ɋ@�\�i�Ⴆ�΃C���^�[�Z�v�^�̕t���ւ��j��ǉ��������Ă��A
     * �R���p�C�����͊����̃C���^�[�t�F�[�X�Ƃ��Ă����U�镑���Ȃ��̂ŁA
     * ���b�p�[��p�ӂ��ă��b�p�[�ɋ@�\���������邱�Ƃɂ���B
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
