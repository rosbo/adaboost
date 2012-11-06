package edu.ntnu.adaboost.utils;

public class QuietLogger implements Logger {

    public void log(String message) {
    }

    public void logImportant(String message) {
        System.out.println(message);
    }
}
