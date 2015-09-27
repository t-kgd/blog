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
        // OK。わかる。Map<String, Number>、親 Number → 子 Integerのダウンキャストと解釈

        Double v2 = MapUtil.getAs3(numberMap, "");
        // OK。わかる。Map<String, Number>、親 Number → 子 Doubleのダウンキャストと解釈

        Integer v3 = MapUtil.getAs3(intMap, "");
        // OK。わかる。Map<String, Integer>、親 Integer → 子 Integerと解釈。そのままなのでOK

//        Double v4 = MapUtil.getAs3(intMap, "");
        // エラー。わかる。Map<String, Integer>、親 Integer → 子 Doubleと解釈するが、親子関係に無いのでエラー

        Number v5 = MapUtil.getAs3(intMap, "");
        // OK。一瞬迷ったけど、わかる。Map<String, Integer>、親 Integer → 子 Integerと解釈した後、Numberに暗黙的キャストされている
        // 推論された型のイメージは↓ つまり、v3と同じようにIntegerが返された後、代入先のNumberにアップキャストされてるだけ
        Number v5b = MapUtil.<String, Integer, Integer>getAs3(intMap, "");


        // 意図したとおりの動きをしてくれてご満悦
        // ……と思いきや

        List<String> list = MapUtil.getAs3(numberMap, "");
        // コンパイルOK。！？！！！？？？なんじゃこりゃ！？

//        List<String> list3 = MapUtil.<String, Number, List<String>>getAs3(numberMap, "");
        // 型を明示するとエラー。一体どんな型として解釈されてるんだろう……？？

        List<String> list3 = MapUtil.<String, Object, List<String>>getAs3(numberMap, "");
        // コンパイルOK。答えはこいつ。VをObject（もしくはTとVの共通の親）と解釈した後に余裕のダウンキャスト

//        ArrayList<String> list2 = MapUtil.getAs3(numberMap, "");
        // なぜかこっちは普通にエラー。違いはインターフェースかどうかってところ？？

        ArrayList<String> list4 = MapUtil.<String, Object, ArrayList<String>>getAs3(numberMap, "");
        // 卑怯なやり方を明示するとコンパイルOK。なんでだろう
    }
}
