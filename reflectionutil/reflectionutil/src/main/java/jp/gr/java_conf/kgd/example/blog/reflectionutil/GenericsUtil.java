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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class GenericsUtil {

    /**
     * あるクラスから、そのクラスのある親クラスに至るまでの継承階層を取得する。
     * <p>
     * 対象とする親クラスがインターフェースの場合、{@link Class#getInterfaces()}で返される順序通りに走査して、
     * 最初に継承関係にあることが確認できた継承階層を返します。
     * 返されるリストは、引数に指定した子クラスと親クラスを含み、要素は子クラスから親クラスの順で格納されています。
     *
     * @param child  階層の最下部となる子クラス。
     * @param parent 階層の最上部となる親クラス。
     * @param <T>
     * @return
     */
    public static <T> List<Class<?>> getClassHierarchy(Class<? extends T> child, Class<T> parent) {
        List<Class<?>> hierarchy = new ArrayList<>();
        if (parent.isInterface()) {
            Set<Class<?>> visited = new HashSet<>();
            traceToInterfaceHierarchy(hierarchy, visited, child, parent);
        } else {
            traceToClassHierarchy(hierarchy, child, parent);
        }
        return hierarchy;
    }

    private static void traceToClassHierarchy(List<Class<?>> hierarchy, Class<?> child, Class<?> parentClass) {
        hierarchy.add(child);
        if (child.equals(parentClass)) {
            return;
        }
        traceToClassHierarchy(hierarchy, child.getSuperclass(), parentClass);
    }

    private static void traceToInterfaceHierarchy(List<Class<?>> hierarchy, Set<Class<?>> visited, Class<?> child, Class<?> parentInterface) {
        hierarchy.add(child);
        if (child.equals(parentInterface)) {
            return;
        }

        for (Class<?> interfaze : child.getInterfaces()) {
            if (visited.contains(interfaze)) continue;
            visited.add(interfaze);
            if (!parentInterface.isAssignableFrom(interfaze)) continue;
            traceToInterfaceHierarchy(hierarchy, visited, interfaze, parentInterface);
            return;
        }

        traceToInterfaceHierarchy(hierarchy, visited, child.getSuperclass(), parentInterface);
    }

    /**
     * あるクラスの、子クラスで束縛された型パラメータを取り出す。
     * <p>
     * 対象の型パラメータが子クラスにおいてまだ束縛されていない場合、<code>null</code>を返します。
     *
     * @param child             型パラメータが束縛されてある可能性のある子クラス
     * @param parent            走査したい型パラメータを持つ親クラス
     * @param typeParameterName 親クラスでの型パラメータの名前
     * @param <T>
     * @return 束縛されてない場合は<code>null</code>を返します。
     */
    public static <T> Type getTypeParameterType(Class<? extends T> child, Class<T> parent, String typeParameterName) {
        return searchTypeParameterType(getClassHierarchy(child, parent), typeParameterName);
    }

    public static <T> Type getTypeParameterType(Class<? extends T> child, Class<T> parent, int index) {
        return searchTypeParameterType(getClassHierarchy(child, parent), "");
    }

    private static Type searchTypeParameterType(List<Class<?>> hierarchy, String targetTypeParameterName) {
        Class<?> parent = hierarchy.remove(hierarchy.size() - 1);
        return searchTypeParameterTypeImpl(hierarchy, parent, targetTypeParameterName);
    }

    private static Type searchTypeParameterTypeImpl(List<Class<?>> hierarchy, Class<?> current, String targetTypeParameterName) {
        if (hierarchy.isEmpty()) {
            return null;
        }

        Class<?> child = hierarchy.remove(hierarchy.size() - 1);

        Type currentType = current.isInterface() ? child.getGenericInterfaces()[indexOf(child.getInterfaces(), t -> t.equals(current))] : child.getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) currentType;

        int typeParameterIndex = indexOf(current.getTypeParameters(), t -> t.getName().equals(targetTypeParameterName));
        Type resultType = parameterizedType.getActualTypeArguments()[typeParameterIndex];

        if (resultType instanceof TypeVariable) {
            TypeVariable<? extends Class<?>> typeVariable = (TypeVariable<? extends Class<?>>) resultType;
            return searchTypeParameterTypeImpl(hierarchy, child, typeVariable.getName());
        } else {
            return resultType;
        }
    }

    private static <T> int indexOf(T[] values, Predicate<? super T> predicate) {
        for (int i = 0; i < values.length; i++) {
            if (predicate.test(values[i])) return i;
        }
        return -1;
    }

    private static <T> T match(T[] values, Predicate<? super T> predicate) {
        return values[indexOf(values, predicate)];
    }

    /**
     * {@link Type}を{@link Class}に変換する。
     * <p>
     * 型パラメータの情報を持っていてもRaw型で返します。
     * <code>null</code>が渡された場合は<code>null</code>を返します。
     *
     * @param type
     * @return
     */
    public static Class<?> typeToClass(Type type) {
        if (type == null) return null;

        if (type instanceof Class) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }

        if (type instanceof TypeVariable) {
            return type.getClass();
        }

        if (type instanceof WildcardType) {
            return type.getClass();
        }

        if (type instanceof GenericArrayType) {
            return type.getClass();
        }

        throw new RuntimeException("予期せぬ型です。");
    }
}
