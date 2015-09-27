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

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

import java.util.Map;

/**
 * Created by misakura on 2015/09/27.
 */
public class JavaConfigSlurperExample2 {

    public static void main(String[] args) {
        String script = "foo = 100";
        ConfigObject config = new ConfigSlurper().parse(script);

        // 手動でキャストを書かなくてOK
        int foo = MapUtil.<Integer>getAs2(config, "foo");
        System.out.println(foo);   // 出力: 100

        // キャストを書かなくて済むだけであって、キャストできない型にすると普通に実行時エラーになります
//        String foo2 = MapUtil.<String>getAs2(config, "foo");

        // 型指定は省略したかったけど必要みたい。
        // エディタではエラーにならないものの、ビルド時にエラーになる（おそらくconfigがRaw型のMapだから）
//        Integer foo = MapUtil.getAs2(config, "foo");

        // Raw型じゃなければ省略できる
        Map<Object, Object> config2 = config;
        int foo1 = MapUtil.getAs(config2, "foo");
        int foo2 = MapUtil.getAs2(config2, "foo");
    }
}
