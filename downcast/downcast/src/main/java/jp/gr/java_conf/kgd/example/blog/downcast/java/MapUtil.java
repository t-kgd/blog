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

import java.util.Map;

/**
 * Created by misakura on 2015/09/27.
 */
public class MapUtil {

    // 引数のmapのVの型指定は、ダウンキャストすることを見越して、あえて非変な型指定にします
    // （※この部分の考えが今回のお題に関係します、後ほど考察します）
    public static <K, T> T getAs(Map<? super K, Object> map, K key) {
        return (T) map.get(key);
    }

    // java.util.Map#getの引数はもともと型がObjectなので、K keyとしなくても良い
    // （むしろK keyにするとGroovyのようにMapがRaw型の場合に明示的が型指定が必要になります
    public static <T> T getAs2(Map<?, Object> map, Object key) {
        return (T) map.get(key);
    }

    // これでもOKなんじゃないの？？と
    public static <K, V, T extends V> T getAs3(Map<? super K, ? extends V> map, K key) {
        return (T) map.get(key);
    }
}
