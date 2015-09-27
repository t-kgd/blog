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

import java.lang.reflect.Method;

/**
 * ���\�b�h���s���ɗ�O�����������ۂɏ������s���n���h���B
 *
 * @param <T>
 */
@FunctionalInterface
public interface ErrorHandler<T> {

    /**
     * ���\�b�h���s���ɗ�O�����������ۂɏ����B
     * <p>
     * ������������A��ւ̌��ʂ�Ԃ����Ƃ��ł��܂��B
     * ���̑�ւ̌��ʂ͎��̃n���h���ɓ`������܂��B
     * �����A�`�����̂��~�������ꍇ��{@link Result#stopFlooding()}��<@code>true</code>��Ԃ��悤�ɂ��Ă��������B
     * �`���͒��~����A���̎��_�ł�{@link Result#getValue()}�̒l����ւ̌��ʂƂ��ĕԂ���܂��B
     *
     * @param obj
     * @param method
     * @param args
     * @param throwable
     * @param prevResult ��O�̃n���h�����Ԃ�����ւ̌��ʁB
     * @return ��ւ̌��ʂ�ێ�����C���X�^���X�B���̃C���X�^���X���̂�null�ɂ��Ă͂����܂���B�Ώۂ̖߂�l���v���~�e�B�u�̏ꍇ�A�K�؂Ȓl��Ԃ��K�v������܂��B
     */
    Result onHandleError(T obj, Method method, Object[] args, Throwable throwable, Object prevResult);

    interface Result {

        Object UNDEFINED = new Object();

        Object getValue();

        boolean stopFlooding();

        static Result of(Object value, boolean stopFlooding) {
            class SimpleResult implements Result {

                Object v;

                boolean f;

                SimpleResult(Object v, boolean f) {
                    this.v = v;
                    this.f = f;
                }

                @Override
                public Object getValue() {
                    return v;
                }

                @Override
                public boolean stopFlooding() {
                    return f;
                }
            }

            return new SimpleResult(value, stopFlooding);
        }

        static Result stop(Object value) {
            return of(value, true);
        }

        static Result stop() {
            return of(UNDEFINED, true);
        }

        static Result through(Object value) {
            return of(value, false);
        }

        static Result through() {
            return of(UNDEFINED, false);
        }
    }
}
