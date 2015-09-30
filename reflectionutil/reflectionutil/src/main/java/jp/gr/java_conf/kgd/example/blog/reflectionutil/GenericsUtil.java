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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
     * @return ミュータブルなリスト
     */
    public static <T> List<Class<?>> getClassHierarchy(Class<? extends T> child, Class<T> parent) {
        List<Class<?>> hierarchy = new ArrayList<>();
        if (parent.isInterface()) {
            // interfaceは複数回実装されている可能性があるので、走査済みのinterfaceはSetに登録して2度以上走査しないようにする。。
            Set<Class<?>> visited = new HashSet<>();
            // 目指す親がinterfaceの場合
            traceToInterfaceHierarchy(hierarchy, visited, child, parent);
        } else {
            // 目指す親がclassの場合
            traceToClassHierarchy(hierarchy, child, parent);
        }
        return hierarchy;
    }

    // 目指す親がclassであれば、順に辿るだけで良い
    private static void traceToClassHierarchy(List<Class<?>> hierarchy, Class<?> child, Class<?> parentClass) {
        hierarchy.add(child);
        if (child.equals(parentClass)) {
            return;
        }
        traceToClassHierarchy(hierarchy, child.getSuperclass(), parentClass);
    }

    // 目指す親がinterfaceであれば、全通りを走査し、初めに親子関係が確定した階層を登録する。
    private static void traceToInterfaceHierarchy(List<Class<?>> hierarchy, Set<Class<?>> visited, Class<?> child, Class<?> parentInterface) {
        hierarchy.add(child);
        if (child.equals(parentInterface)) {
            return;
        }

        // どこから親に繋がるかわからないので先にinterfaceをチェック
        for (Class<?> interfaze : child.getInterfaces()) {
            if (visited.contains(interfaze)) continue;
            visited.add(interfaze);
            if (!parentInterface.isAssignableFrom(interfaze)) continue;
            traceToInterfaceHierarchy(hierarchy, visited, interfaze, parentInterface);
            return;
        }

        // なければ親クラスへ
        traceToInterfaceHierarchy(hierarchy, visited, child.getSuperclass(), parentInterface);
    }

    /**
     * あるクラスの、子クラスで束縛された型パラメータを取り出す。
     * <p>
     * 対象の型パラメータが子クラスにおいてまだ束縛されていない場合、<code>null</code>を返します。
     * 指定した親クラスが型パラメータを持たない場合や、指定した名前の型パラメータが存在しない場合はnullを返します。
     *
     * @param child             型パラメータが束縛されてある可能性のある子クラス
     * @param parent            走査したい型パラメータを持つ親クラス
     * @param typeParameterName 親クラスでの型パラメータの名前
     * @param <T>
     * @return 束縛されてない場合は<code>null</code>を返します。
     */
    public static <T> Type getTypeParameterType(Class<? extends T> child, Class<T> parent, String typeParameterName) {
        TypeVariable<Class<T>>[] typeParameters = parent.getTypeParameters();
        if (typeParameters.length == 0) return null;
        if (find(typeParameters, t -> t.getName().equals(typeParameterName)) == null) return null;
        return searchTypeParameterType(getClassHierarchy(child, parent), typeParameterName);
    }

    /**
     * あるクラスの、子クラスで束縛された型パラメータを取り出す。
     * <p>
     * 対象の型パラメータが子クラスにおいてまだ束縛されていない場合、<code>null</code>を返します。
     * {@link Class#getTypeParameters()}から<code>index</code>番目にある型パラメータの名前を取得し、
     * {@link #getTypeParameterType(Class, Class, String)}に委譲します。
     *
     * @param child  型パラメータが束縛されてある可能性のある子クラス
     * @param parent 走査したい型パラメータを持つ親クラス
     * @param index  　親クラスでの型パラメータのインデックス
     * @param <T>
     * @return
     */
    public static <T> Type getTypeParameterType(Class<? extends T> child, Class<T> parent, int index) {
        return getTypeParameterType(child, parent, parent.getTypeParameters()[index].getName());
    }

    /**
     * あるクラスの、子クラスで束縛された型パラメータを取り出す。
     * <p>
     * 対象の型パラメータが子クラスにおいてまだ束縛されていない場合、<code>null</code>を返します。
     * <code>index</code>を0として{@link #getTypeParameterType(Class, Class, int)}に委譲します。
     *
     * @param child  型パラメータが束縛されてある可能性のある子クラス
     * @param parent 走査したい型パラメータを持つ親クラス
     * @param <T>
     * @return
     */
    public static <T> Type getTypeParameterType(Class<? extends T> child, Class<T> parent) {
        return getTypeParameterType(child, parent, 0);
    }

    // エントリ
    private static Type searchTypeParameterType(List<Class<?>> hierarchy, String targetTypeParameterName) {
        Class<?> parent = hierarchy.remove(hierarchy.size() - 1);
        // ここから再帰
        return searchTypeParameterTypeImpl(hierarchy, parent, targetTypeParameterName);
    }

    private static Type searchTypeParameterTypeImpl(List<Class<?>> hierarchy, Class<?> current, String targetTypeParameterName) {
        if (hierarchy.isEmpty()) {
            return null;
        }

        // 親子階層を記録したリストから、currentの1階層下の子Classを取り出す。
        Class<?> child = hierarchy.remove(hierarchy.size() - 1);

        // 子Classから型パラメータがバインドされているかもしれない情報を取り出す。
        // currentがinterfaceならcurrentの適切な位置から取り出す。
        Type currentType = current.isInterface() ? child.getGenericInterfaces()[indexOf(child.getInterfaces(), t -> t.equals(current))] : child.getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) currentType;

        // （型パラメータが複数あるかもしれないので）探したい型パラメータのindexを取得する。
        int typeParameterIndex = indexOf(current.getTypeParameters(), t -> t.getName().equals(targetTypeParameterName));
        Type resultType = parameterizedType.getActualTypeArguments()[typeParameterIndex];

        // まだバインドされていなければ、階層を1つ降りて再帰で走査する
        if (resultType instanceof TypeVariable) {
            TypeVariable<? extends Class<?>> typeVariable = (TypeVariable<? extends Class<?>>) resultType;
            return searchTypeParameterTypeImpl(hierarchy, child, typeVariable.getName());
        } else {
            return resultType;
        }
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

        // 普通のClass
        if (type instanceof Class) {
            return (Class<?>) type;
        }

        // バインドされた型パラメータ情報を持つが、Classで返す（Raw型）
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }

        // 型パラメータ（Tとかそのままの名前で返るはず）
        if (type instanceof TypeVariable) {
            return type.getClass();
        }

        // ワイルドカード
        if (type instanceof WildcardType) {
            return type.getClass();
        }

        // Tの配列？
        if (type instanceof GenericArrayType) {
            return type.getClass();
        }

        throw new RuntimeException("予期せぬ型です。");
    }

    // 配列操作ユーティリティ
    private static <T> int indexOf(T[] values, Predicate<? super T> predicate) {
        for (int i = 0; i < values.length; i++) {
            if (predicate.test(values[i])) return i;
        }
        return -1;
    }

    // 配列操作ユーティリティ
    private static <T> T find(T[] values, Predicate<? super T> predicate) {
        int i = indexOf(values, predicate);
        if (i < 0) return null;
        return values[i];
    }

    public static class TypeParameterCache {

        // (parent, child)の組み合わせで継承階層をキャッシュする
        private Map<Class<?>, Map<Class<?>, List<Class<?>>>> hierarchyCache = new ConcurrentHashMap<>();

        // (parent, child, 型パラメータ名)の組み合わせでTypeをキャッシュする
        private Map<Class<?>, Map<Class<?>, Map<String, Type>>> resultCache = new ConcurrentHashMap<>();

        // Typeから得られるClassをキャッシュする
        private Map<Type, Class<?>> typeToClassCache = new ConcurrentHashMap<>();

        public <T> List<Class<?>> getClassHierarchy(Class<? extends T> child, Class<T> parent) {
            // まだparentに結びつくMapがなければ生成してキャッシュする
            Map<Class<?>, List<Class<?>>> hierarchyMap = hierarchyCache.computeIfAbsent(parent, p -> new ConcurrentHashMap<>());
            // まだ(parent, child)に結びつく継承階層がなければ取得してキャッシュする
            List<Class<?>> hierarchy = hierarchyMap.computeIfAbsent(child, c -> {
                List<Class<?>> h = getClassHierarchy(child, parent);
                return Collections.unmodifiableList(h);
            });
            return hierarchy;
        }

        public <T> Type getTypeParameterType(Class<? extends T> child, Class<T> parent, String typeParameterName) {
            // まだparentに結びつくMapがなければ生成してキャッシュする
            Map<Class<?>, Map<String, Type>> resultMap = resultCache.computeIfAbsent(parent, p -> new ConcurrentHashMap<>());
            // まだ(parent, child)に結びつくMapがなければ生成してキャッシュする
            Map<String, Type> typeMap = resultMap.computeIfAbsent(child, c -> new ConcurrentHashMap<>());
            // まだ(parent, child, 型パラメータ名)に結びつくTypeがなければ取得してキャッシュする
            Type type = typeMap.computeIfAbsent(typeParameterName, n -> {
                List<Class<?>> hierarchy = new ArrayList(getClassHierarchy(child, parent));
                return searchTypeParameterType(hierarchy, typeParameterName);
            });
            return type;
        }

        public Class<?> typeToClass(Type type) {
            return typeToClassCache.computeIfAbsent(type, t -> typeToClass(t));
        }
    }
}
