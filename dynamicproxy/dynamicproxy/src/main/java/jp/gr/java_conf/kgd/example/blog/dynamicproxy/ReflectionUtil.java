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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ReflectionUtil {

    /**
     * 継承関係にある全てのインターフェースを抽出する。
     *
     * @param clazz
     * @return
     */
    public static Class<?>[] extractInterfaces(Class<?> clazz) {
        if (clazz.isInterface()) {
            return extractInterfacesFromInterface(clazz);
        } else {
            return extractInterfacesFromClass(clazz);
        }
    }

    /*
     * classであれば、親classを辿りながらinterfaceを回収していけば全てのinterfaceが取得できる。
     */
    private static Class<?>[] extractInterfacesFromClass(Class<?> clazz) {
        Set<Class<?>> interfaces = new HashSet<>();
        Class<?> current = clazz;
        while (current != null) {
            interfaces.addAll(Arrays.asList(current.getInterfaces()));
            current = current.getSuperclass();
        }
        return interfaces.toArray(new Class<?>[interfaces.size()]);
    }

    /*
     * interfaceであれば、親interfaceを辿りながら再帰的にinterfaceを回収していけば全てのinterfaceが取得できる。
     */
    private static Class<?>[] extractInterfacesFromInterface(Class<?> interfaze) {
        Set<Class<?>> interfaces = new HashSet<>();
        extractInterfacesFromInterfaceImpl(interfaces, interfaze);
        return interfaces.toArray(new Class<?>[interfaces.size()]);
    }

    private static void extractInterfacesFromInterfaceImpl(Set<Class<?>> mutableSet, Class<?> interfaze) {
        mutableSet.add(interfaze);
        for (Class<?> i : interfaze.getInterfaces()) {
            extractInterfacesFromInterfaceImpl(mutableSet, i);
        }
    }

    public static <T> T getDefaultValue(Class<T> clazz) {
        if (!clazz.isPrimitive()) return null;
        if (clazz == void.class) return null;
        return (T) Array.get(Array.newInstance(clazz, 1), 0);
    }
}
