package edu.ntnu.adaboost.dependencyinjection;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import edu.ntnu.adaboost.controller.AdaboostController;
import edu.ntnu.adaboost.controller.DataController;
import edu.ntnu.adaboost.controller.EnsembleLearningController;
import edu.ntnu.adaboost.utils.Logger;
import edu.ntnu.adaboost.utils.LoggerProvider;

import javax.inject.Singleton;

public class AdaboostModule extends AbstractModule {

    private final boolean quietMode;

    public AdaboostModule(boolean quietMode) {
        this.quietMode = quietMode;
    }

    @Override
    protected void configure() {
        bind(EnsembleLearningController.class).in(Singleton.class);
        bind(DataController.class).in(Singleton.class);
        bind(AdaboostController.class).in(Singleton.class);

        bind(Boolean.class).annotatedWith(Names.named("quietMode")).toInstance(quietMode);
        bind(Logger.class).toProvider(LoggerProvider.class).in(Singleton.class);
    }

}
