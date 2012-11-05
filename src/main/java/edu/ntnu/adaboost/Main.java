package edu.ntnu.adaboost;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.ntnu.adaboost.dependencyinjection.AdaboostModule;
import edu.ntnu.adaboost.utils.Logger;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AdaboostModule());
        Logger logger = injector.getInstance(Logger.class);
        logger.log("Test");
    }
}
