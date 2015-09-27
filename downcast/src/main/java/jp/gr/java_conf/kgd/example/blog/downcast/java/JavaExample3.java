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

package jp.gr.java_conf.kgd.example.blog.downcast.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by misakura on 2015/09/27.
 */
public class JavaExample3 {

    public static void main(String[] args) {
        Map<String, Number> numberMap = new HashMap<>();
        Map<String, Integer> intMap = new HashMap<>();

        Integer v1 = MapUtil.getAs3(numberMap, "");
        // OK�B�킩��BMap<String, Number>�A�e Number �� �q Integer�̃_�E���L���X�g�Ɖ���

        Double v2 = MapUtil.getAs3(numberMap, "");
        // OK�B�킩��BMap<String, Number>�A�e Number �� �q Double�̃_�E���L���X�g�Ɖ���

        Integer v3 = MapUtil.getAs3(intMap, "");
        // OK�B�킩��BMap<String, Integer>�A�e Integer �� �q Integer�Ɖ��߁B���̂܂܂Ȃ̂�OK

//        Double v4 = MapUtil.getAs3(intMap, "");
        // �G���[�B�킩��BMap<String, Integer>�A�e Integer �� �q Double�Ɖ��߂��邪�A�e�q�֌W�ɖ����̂ŃG���[

        Number v5 = MapUtil.getAs3(intMap, "");
        // OK�B��u���������ǁA�킩��BMap<String, Integer>�A�e Integer �� �q Integer�Ɖ��߂�����ANumber�ɈÖٓI�L���X�g����Ă���
        // ���_���ꂽ�^�̃C���[�W�́� �܂�Av3�Ɠ����悤��Integer���Ԃ��ꂽ��A������Number�ɃA�b�v�L���X�g����Ă邾��
        Number v5b = MapUtil.<String, Integer, Integer>getAs3(intMap, "");


        // �Ӑ}�����Ƃ���̓��������Ă���Ă����x
        // �c�c�Ǝv������

        List<String> list = MapUtil.getAs3(numberMap, "");
        // �R���p�C��OK�B�I�H�I�I�I�H�H�H�Ȃ񂶂Ⴑ���I�H

//        List<String> list3 = MapUtil.<String, Number, List<String>>getAs3(numberMap, "");
        // �^�𖾎�����ƃG���[�B��̂ǂ�Ȍ^�Ƃ��ĉ��߂���Ă�񂾂낤�c�c�H�H

        List<String> list3 = MapUtil.<String, Object, List<String>>getAs3(numberMap, "");
        // �R���p�C��OK�B�����͂����BV��Object�i��������T��V�̋��ʂ̐e�j�Ɖ��߂�����ɗ]�T�̃_�E���L���X�g

//        ArrayList<String> list2 = MapUtil.getAs3(numberMap, "");
        // �Ȃ����������͕��ʂɃG���[�B�Ⴂ�̓C���^�[�t�F�[�X���ǂ������ĂƂ���H�H

        ArrayList<String> list4 = MapUtil.<String, Object, ArrayList<String>>getAs3(numberMap, "");
        // �ڋ��Ȃ����𖾎�����ƃR���p�C��OK�B�Ȃ�ł��낤
    }
}
