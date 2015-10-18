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

package jp.gr.java_conf.kgd.example.blog.collections.impl;

import jp.gr.java_conf.kgd.example.blog.collections.iterator.variation.forward.ForwardInsertIoIterator;
import jp.gr.java_conf.kgd.example.blog.collections.list.MutableForwardList;

public class SimpleForwardList<T> implements MutableForwardList<T> {

    private Iterator<T> firstIterator;

    @Override
    public ForwardInsertIoIterator<T> firstIterator() {
        return firstIterator;
    }

    @Override
    public void pushFront(T t) {
        firstIterator = new SimpleIterator<>(t, firstIterator);
    }

    @Override
    public T popFront() {
        T t = firstIterator.get();
        firstIterator = firstIterator.getNextIterator();
        return t;
    }

    public interface Iterator<T> extends ForwardInsertIoIterator<T> {

        Iterator<T> getNextIterator();
    }

    public static class SimpleIterator<T> implements Iterator<T> {

        private T element;

        private Iterator<T> nextIterator;

        public SimpleIterator(T element, Iterator<T> iterator) {
            this.element = element;
            this.nextIterator = iterator;
        }

        @Override
        public Iterator<T> getNextIterator() {
            return nextIterator;
        }

        @Override
        public void insertNext(T t) {
            nextIterator = new SimpleIterator<>(t, nextIterator);
        }

        @Override
        public void next() {
            element = nextIterator.get();
            nextIterator = nextIterator.getNextIterator();
        }

        @Override
        public T get() {
            return element;
        }

        @Override
        public void set(T t) {
            this.element = t;
        }
    }

    public static <T> SimpleForwardList<T> of(T... elements) {
        SimpleForwardList<T> list = new SimpleForwardList<>();
        for (int i = elements.length - 1; i >= 0; i--) {
            list.pushFront(elements[i]);
        }
        return list;
    }
}
