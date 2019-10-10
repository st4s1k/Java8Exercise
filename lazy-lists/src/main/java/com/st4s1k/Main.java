package com.st4s1k;

import java.time.LocalDateTime;

public class Main {

    private static ILazyList<Integer> from(int n) {
        return new LazyList<>(n, () -> from(n + 1));
    }

    private static ILazyList<Integer> primes(ILazyList<Integer> numbers) {
        return new LazyList<>(numbers.head(), () ->
                primes(numbers.tail().filter(n -> n % numbers.head() != 0)));
    }

    public static void main(String[] args) {
        primes(from(2))
                .stream()
                .limit(100)
                .forEach(System.out::println);
    }
}