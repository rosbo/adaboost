package edu.ntnu.adaboost;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.ntnu.adaboost.controller.EnsembleLearningController;
import edu.ntnu.adaboost.dependencyinjection.AdaboostModule;
import org.apache.commons.cli.*;

public class Main {

    public static void main(String[] args) {
        Options options = createArgsOptions();

        PosixParser parser = new PosixParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            boolean quietMode = cmd.hasOption("q");
            String filename = cmd.getOptionValue("f");

            Injector injector = Guice.createInjector(new AdaboostModule(quietMode));
            EnsembleLearningController learningController = injector.getInstance(EnsembleLearningController.class);
            learningController.learn(filename);
        } catch (ParseException e) {
            e.printStackTrace();
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("Adaboost", options);
        }
    }

    private static Options createArgsOptions() {
        Options options = new Options();
        Option filename = OptionBuilder.withArgName("FILENAME").hasArg().withDescription("The name of the data file")
                .isRequired().create("f");
        options.addOption(filename);
        options.addOption("q", false, "Quiet mode");

        return options;
    }

}
