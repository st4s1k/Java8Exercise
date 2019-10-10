package com.st4s1k;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ILazyList<T> {

    class EmptyList<T> implements ILazyList<T> {
    }

    default T head() {
        throw new UnsupportedOperationException();
    }

    default ILazyList<T> tail() {
        throw new UnsupportedOperationException();
    }

    default ILazyList<T> filter(Predicate<T> p) {
        throw new UnsupportedOperationException();
    }

    default boolean isEmpty() {
        return true;
    }

    default Iterator<T> iterator() {
        return new IMyListIterator<>(this);
    }

    default Stream<T> stream() {
        Iterator<T> listIterator = iterator();
        return Stream.generate(listIterator::next);
    }

    class IMyListIterator<T> implements Iterator<T> {

        private ILazyList<T> list;

        public IMyListIterator(ILazyList<T> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return !list.isEmpty();
        }

        @Override
        public T next() {
            T head = list.head();
            list = list.tail();
            return head;
        }

    }
}
