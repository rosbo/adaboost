package edu.ntnu.adaboost.dependencyinjection;

import com.google.inject.AbstractModule;
import edu.ntnu.adaboost.controller.AppController;
import edu.ntnu.adaboost.controller.DataController;
import edu.ntnu.adaboost.utils.ConsoleLogger;
import edu.ntnu.adaboost.utils.Logger;

import javax.inject.Singleton;

public class AdaboostModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AppController.class).in(Singleton.class);
        bind(DataController.class).in(Singleton.class);

        bind(Logger.class).to(ConsoleLogger.class).in(Singleton.class);
    }

}
