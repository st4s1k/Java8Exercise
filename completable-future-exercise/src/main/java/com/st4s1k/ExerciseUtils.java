package com.st4s1k;

import java.util.Random;

class ExerciseUtils {
    
    private static final Random random = new Random();

    public static void randomDelay(int min, int max) {
        int time = min + random.nextInt(max - min);
        delay(time);
    }

    public static void delay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void doSomethingElse() {
        System.out.println("Doing something else...");
        randomDelay(500, 2500);
        System.out.println("Something else done!");
    }
}