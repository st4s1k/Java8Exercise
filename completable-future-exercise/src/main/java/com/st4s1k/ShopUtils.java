package com.st4s1k;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class ShopUtils {

    private static final int MAX_THREADS = 100;

    public static List<String> findPricesUsingSequentialStream(List<Shop> shops, String product) {
        return shops.stream()
                .map(shop ->
                        String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(toList());
    }

    public static List<String> findPricesUsingParallelStream(List<Shop> shops, String product) {
        return shops.stream()
                .parallel()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(toList());
    }

    public static List<String> findPricesUsingCompletableFeature(List<Shop> shops, String product) {

        final ExecutorService executor = executorFactory(shops.size());

        List<CompletableFuture<String>> futurePrices = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)), executor))
                .collect(toList());

        return futurePrices.stream()
                .map(CompletableFuture::join)
                .collect(toList());
    }

    public static List<String> findPricesUsingDiscountServiceAndSequentialStream(List<Shop> shops, String product) {
        return shops.stream()
                .map(shop -> shop.getFormattedPrice(product))
                .map(Quote::parse)
                .map(Discount::applyDiscount)
                .collect(toList());
    }

    public static List<String> findPricesUsingDiscountServiceAndParallelStream(List<Shop> shops, String product) {
        return shops.stream()
                .parallel()
                .map(shop -> shop.getFormattedPrice(product))
                .map(Quote::parse)
                .map(Discount::applyDiscount)
                .collect(toList());
    }

    public static List<String> findPricesUsingDiscountServiceAndCompletableFuture(List<Shop> shops, String product) {

        final ExecutorService executor = executorFactory(shops.size());

        List<CompletableFuture<String>> futurePrices = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getFormattedPrice(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(
                        quote -> CompletableFuture.supplyAsync(
                                () -> Discount.applyDiscount(quote), executor)))
                .collect(toList());

        return futurePrices.stream()
                .map(CompletableFuture::join)
                .collect(toList());
    }

    public static Stream<CompletableFuture<String>> findPricesStream(List<Shop> shops, String product) {
        final ExecutorService executor = executorFactory(shops.size());
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getFormattedPrice(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(
                        quote -> CompletableFuture.supplyAsync(
                                () -> Discount.applyDiscount(quote), executor)));
    }

    public static List<String> findPricesUsingCompletableFuturePipeline(List<Shop> shops, String product) {
        long start = System.nanoTime();
        CompletableFuture[] futures = ShopUtils.findPricesStream(shops, product)
                .map(f -> f.thenAccept(s -> System.out.println(
                        "\t" + s + " (done in " + ((System.nanoTime() - start) / 1_000_000) + " ms)")))
//              .map(CompletableFuture::join) <-- won't work!
                .toArray(CompletableFuture[]::new);
        /* Explicitly calling join() in a separate function call,
         * because of the nature of streams, which wont process another element
         * until the current element is done, this way forcing sequential processing */
        CompletableFuture.allOf(futures).join();
        return Collections.emptyList();
    }

    public static ExecutorService executorFactory(int collectionSize) {
        return Executors.newFixedThreadPool(Math.min(collectionSize, MAX_THREADS), task -> {
            Thread newThread = new Thread(task);
            newThread.setDaemon(true);
            return newThread;
        });
    }
}