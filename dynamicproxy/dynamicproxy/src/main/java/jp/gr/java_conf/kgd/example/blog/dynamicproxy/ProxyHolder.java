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

import java.util.List;

public interface ProxyHolder<T> {

    /**
     * 生成された動的プロキシを返します。
     * <p>
     * このインスタンスのメソッドを呼び出すことで、前後のインターセプタが実行されます。
     *
     * @return
     */
    T getProxy();

    /**
     * プロキシのメソッド実行前に呼び出されるインターセプタのリスト。
     * <p>
     * クラス内で保持しているリストの参照をそのまま返します。
     *
     * @return ミュータブルなリスト。
     */
    List<OnPreInvokeListener<? super T>> getOnPreInvokeListeners();

    /**
     * プロキシのメソッド実行後に呼び出されるインターセプタのリスト。
     * <p>
     * クラス内で保持しているリストの参照をそのまま返します。
     *
     * @return ミュータブルなリスト。
     */
    List<OnPostInvokeListener<? super T>> getOnPostInvokeListeners();

    /**
     * プロキシのメソッド実行中に例外が発生した時に呼び出されるハンドラのリスト。
     * <p>
     * クラス内で保持しているリストの参照をそのまま返します。
     *
     * @return ミュータブルなリスト。
     */
    List<ErrorHandler<? super T>> getErrorHandlers();
}
