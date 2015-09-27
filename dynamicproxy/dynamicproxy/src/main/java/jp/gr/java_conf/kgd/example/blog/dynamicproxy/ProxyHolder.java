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
     * �������ꂽ���I�v���L�V��Ԃ��܂��B
     * <p>
     * ���̃C���X�^���X�̃��\�b�h���Ăяo�����ƂŁA�O��̃C���^�[�Z�v�^�����s����܂��B
     *
     * @return
     */
    T getProxy();

    /**
     * �v���L�V�̃��\�b�h���s�O�ɌĂяo�����C���^�[�Z�v�^�̃��X�g�B
     * <p>
     * �N���X���ŕێ����Ă��郊�X�g�̎Q�Ƃ����̂܂ܕԂ��܂��B
     *
     * @return �~���[�^�u���ȃ��X�g�B
     */
    List<OnPreInvokeListener<? super T>> getOnPreInvokeListeners();

    /**
     * �v���L�V�̃��\�b�h���s��ɌĂяo�����C���^�[�Z�v�^�̃��X�g�B
     * <p>
     * �N���X���ŕێ����Ă��郊�X�g�̎Q�Ƃ����̂܂ܕԂ��܂��B
     *
     * @return �~���[�^�u���ȃ��X�g�B
     */
    List<OnPostInvokeListener<? super T>> getOnPostInvokeListeners();

    /**
     * �v���L�V�̃��\�b�h���s���ɗ�O�������������ɌĂяo�����n���h���̃��X�g�B
     * <p>
     * �N���X���ŕێ����Ă��郊�X�g�̎Q�Ƃ����̂܂ܕԂ��܂��B
     *
     * @return �~���[�^�u���ȃ��X�g�B
     */
    List<ErrorHandler<? super T>> getErrorHandlers();
}
