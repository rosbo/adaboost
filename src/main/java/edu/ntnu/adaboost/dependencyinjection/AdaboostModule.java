package edu.ntnu.adaboost.dependencyinjection;

import com.google.inject.AbstractModule;
import edu.ntnu.adaboost.utils.ConsoleLogger;
import edu.ntnu.adaboost.utils.Logger;

import javax.inject.Singleton;

public class AdaboostModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Logger.class).to(ConsoleLogger.class).in(Singleton.class);
    }
}
