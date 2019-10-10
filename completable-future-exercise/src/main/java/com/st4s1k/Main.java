package com.st4s1k;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

class Main {

    private static List<Shop> shops = new ArrayList<>();

    private static List<CompletableFuture<Void>> examples = new ArrayList<>();

    private static final ExecutorService executor = ShopUtils
            .executorFactory(Runtime.getRuntime().availableProcessors());

    static {
        shops.add(new Shop("BestPrice"));
        shops.add(new Shop("LetsSaveBig"));
        shops.add(new Shop("MyFavoriteShop"));
        shops.add(new Shop("BuyItAll0"));
        shops.add(new Shop("BuyItAll1"));
        shops.add(new Shop("BuyItAll2"));
        shops.add(new Shop("BuyItAll3"));
        shops.add(new Shop("BuyItAll4"));
        shops.add(new Shop("BuyItAll5"));
    }

    private static void findPrices(BiFunction<List<Shop>, String, List<String>> findPrices, String operation) {
        examples.add(CompletableFuture.runAsync(() -> {
            long start, duration;
            List<String> prices;
            start = System.nanoTime();
            prices = findPrices.apply(shops, "myPhone27S");
            duration = (System.nanoTime() - start) / 1_000_000;
            System.out.println(operation + ": Done in " + duration + " ms");
            // System.out.println(prices);
        }, executor));
    }

    public static void main(String[] args) {

        findPrices(ShopUtils::findPricesUsingSequentialStream,
                "stream()");
        findPrices(ShopUtils::findPricesUsingParallelStream,
                "stream().parallel()");
        findPrices(ShopUtils::findPricesUsingCompletableFeature,
                "Completable Feature");
        findPrices(ShopUtils::findPricesUsingDiscountServiceAndSequentialStream,
                "Discount service & stream()");
        findPrices(ShopUtils::findPricesUsingDiscountServiceAndParallelStream,
                "Discount service & stream().parallel()");
        findPrices(ShopUtils::findPricesUsingDiscountServiceAndCompletableFuture,
                "Discount service & Completable Future");
        findPrices(ShopUtils::findPricesUsingCompletableFuturePipeline,
                "All shops");

        examples.forEach(CompletableFuture::join);

        /*
         * OUTPUT (example):
         *
         * Completable Feature: Done in 1776 ms
         * Discount service & Completable Future: Done in 3220 ms
         *     LetsSaveBig price is 141.138000 (done in 1803 ms)
         *     MyFavoriteShop price is 127.219500 (done in 1830 ms)
         * stream().parallel(): Done in 3943 ms
         *     BuyItAll1 price is 184.734000 (done in 2226 ms)
         *     BuyItAll2 price is 105.664000 (done in 2374 ms)
         *     BestPrice price is 156.042000 (done in 2529 ms)
         *     BuyItAll5 price is 129.599500 (done in 2711 ms)
         *     BuyItAll0 price is 126.848000 (done in 3077 ms)
         *     BuyItAll4 price is 139.750000 (done in 3300 ms)
         *     BuyItAll3 price is 113.864000 (done in 3487 ms)
         * All shops: Done in 3487 ms
         * Discount service & stream().parallel(): Done in 6214 ms
         * stream(): Done in 17874 ms
         * Discount service & stream(): Done in 22446 ms
         *
         * Process finished with exit code 0
         *
         * */
    }
}