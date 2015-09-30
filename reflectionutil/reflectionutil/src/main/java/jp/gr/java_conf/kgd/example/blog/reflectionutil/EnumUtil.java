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

package jp.gr.java_conf.kgd.example.blog.reflectionutil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumUtil {

    public static <T extends Enum<T>> T[] invokeValues(Class<T> enumType) {
        try {
            Method method = enumType.getMethod("values");
            Object result = method.invoke(null);
            // 必ずダウンキャストできる
            return (T[]) result;
        } catch (NoSuchMethodException e) {
            // Enum#valuesは絶対存在するはず
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            // Enum#valuesで例外が発生することはないはず
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            // Enum#valuesにアクセスできないことはないはず
            throw new AssertionError(e);
        }
    }

    // リフレクションのパフォーマンスが気になる場合はキャッシュしたり
    public static class EnumValuesCache {

        private Map<? super Class<? extends Enum<?>>, List<?>> valuesCache = new ConcurrentHashMap<>();

        public <T extends Enum<T>> List<T> getValues(Class<T> enumType) {
            List<?> result = valuesCache.computeIfAbsent(enumType, key -> {
                T[] values = invokeValues(enumType);
                List<T> valueList = Collections.unmodifiableList(Arrays.asList(values));
                return valueList;
            });

            return (List<T>) result;
        }
    }
}
