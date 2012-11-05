package edu.ntnu.adaboost.utils;

public class ConsoleLogger implements Logger {
    public void log(String message) {
        System.out.println(message);
    }
}
