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

import jp.gr.java_conf.kgd.example.blog.encryptedjar.interfacejar.Foo;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class VirtualClassLoaderTest {

    @Test
    public void zip4jResourceLoaderTest() throws ZipException, ClassNotFoundException, IllegalAccessException, InstantiationException, URISyntaxException {
        // zipファイルを指定する（パスワードは「aaa」）
        URI uri = getClass().getClassLoader().getResource("impljar.zip").toURI();
        File file = new File(uri);
        ZipFile zipFile = new ZipFile(file);
        zipFile.setPassword("aaa");
        ResourceLoader resourceLoader = new Zip4jResourceLoader(zipFile);
        VirtualClassLoader classLoader = new VirtualClassLoader(resourceLoader);
        Class<?> clazz = classLoader.loadClass("jp.gr.java_conf.kgd.example.blog.encryptedjar.impljar.FooImpl");
        Foo foo = (Foo) clazz.newInstance();
        String actual = foo.getName();
        Assert.assertEquals("ふーいんぷる", actual);
    }

    @Test
    public void plainDirectoryResourceLoaderTest() throws ZipException, ClassNotFoundException, IllegalAccessException, InstantiationException, URISyntaxException {
        // フォルダを指定する
        URI uri = getClass().getClassLoader().getResource("impljar").toURI();
        File file = new File(uri);
        ResourceLoader resourceLoader = new PlainDirectoryResourceLoader(file);
        VirtualClassLoader classLoader = new VirtualClassLoader(resourceLoader);
        Class<?> clazz = classLoader.loadClass("jp.gr.java_conf.kgd.example.blog.encryptedjar.impljar.FooImpl");
        Foo foo = (Foo) clazz.newInstance();
        String actual = foo.getName();
        Assert.assertEquals("ふーいんぷる", actual);
    }
}
