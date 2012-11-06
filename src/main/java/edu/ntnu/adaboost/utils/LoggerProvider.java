package edu.ntnu.adaboost.utils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class LoggerProvider implements Provider<Logger> {

    private final Boolean quiet;

    @Inject
    public LoggerProvider(final @Named("quietMode") Boolean quiet) {
        this.quiet = quiet;
    }

    public Logger get() {
        if (quiet) {
            return new QuietLogger();
        } else {
            return new DefaultLogger();
        }
    }

}
