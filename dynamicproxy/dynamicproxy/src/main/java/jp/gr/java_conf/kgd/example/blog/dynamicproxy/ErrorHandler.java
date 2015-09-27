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
 * メソッド実行中に例外が発生した際に処理を行うハンドラ。
 *
 * @param <T>
 */
@FunctionalInterface
public interface ErrorHandler<T> {

    /**
     * メソッド実行中に例外が発生した際に処理。
     * <p>
     * 処理をした後、代替の結果を返すことができます。
     * この代替の結果は次のハンドラに伝搬されます。
     * もし、伝搬自体を停止したい場合は{@link Result#stopFlooding()}で<@code>true</code>を返すようにしてください。
     * 伝搬は中止され、その時点での{@link Result#getValue()}の値が代替の結果として返されます。
     *
     * @param obj
     * @param method
     * @param args
     * @param throwable
     * @param prevResult 一つ前のハンドラが返した代替の結果。
     * @return 代替の結果を保持するインスタンス。このインスタンス自体をnullにしてはいけません。対象の戻り値がプリミティブの場合、適切な値を返す必要があります。
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
