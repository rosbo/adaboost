package edu.ntnu.adaboost.utils;

public class Random {
    public static double doubleBetween(double start, double end) {
        double random = Math.random();
        double amplitude = end - start;

        return start + random * amplitude;
    }
}
