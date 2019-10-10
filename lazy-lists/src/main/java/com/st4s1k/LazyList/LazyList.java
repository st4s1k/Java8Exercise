package com.st4s1k;

import java.util.function.Predicate;
import java.util.function.Supplier;

import com.st4s1k.LazyList.ILazyList;
w
public class LazyList<T> implements ILazyList<T> {

    private final T head;
    private final Supplier<ILazyList<T>> tail;

    public LazyList(T head, Supplier<ILazyList<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public ILazyList<T> tail() {
        return tail.get();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ILazyList<T> filter(Predicate<T> p) {
        return isEmpty()
                ? this
                : p.test(head())
                    ? new LazyList<>(head(), () -> tail.get().filter(p))
                    : tail().filter(p);
    }
}