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

package jp.gr.java_conf.kgd.example.blog.encryptedjar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

public class VirtualClassLoader extends ClassLoader {

    // 中身のフォルダ構成がJARファイルのようなパッケージ階層になっているResourceLoaderである必要がある。
    private ResourceLoader resourceLoader;

    public VirtualClassLoader(ClassLoader parent, ResourceLoader resourceLoader) {
        super(parent);
        this.resourceLoader = resourceLoader;
    }

    public VirtualClassLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // クラス名をリソースへのパスに変換
        String resourcePath = name.replace(".", "/") + ".class";
        InputStream inputStream = getResourceAsStream(resourcePath);
        if (inputStream == null) throw new ClassNotFoundException("リソースが見つかりません。: " + resourcePath);
        // リソースをbyte[]にするためにむにゃむにゃする
        ByteArrayOutputStream byteArrayOutputStream = inputStreamToByteArrayOutputStream(inputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        // byte[]からClassを生成
        Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
        return clazz;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        // 親の実装だとFile経由で取得しようとするのでオーバーライドしておく
        return resourceLoader.getResourceAsStream(name);
    }

    // InputStreamをOutputStreamに流すユーティリティ
    private static void output(OutputStream outputStream, InputStream inputStream) {
        byte[] buffer = new byte[1024];
        try {
            while (true) {
                int len = inputStream.read(buffer);
                if (len < 0) {
                    break;
                }
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // InputStreamをメモリ上に流すユーティリティ
    private static ByteArrayOutputStream inputStreamToByteArrayOutputStream(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        output(byteArrayOutputStream, inputStream);
        return byteArrayOutputStream;
    }
}

