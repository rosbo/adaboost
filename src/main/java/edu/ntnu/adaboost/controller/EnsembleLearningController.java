package edu.ntnu.adaboost.controller;

import edu.ntnu.adaboost.classifier.Classifier;
import edu.ntnu.adaboost.model.Data;
import edu.ntnu.adaboost.utils.DatasetSplitter;
import edu.ntnu.adaboost.utils.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EnsembleLearningController {
    private final DataController dataController;
    private final AdaboostController adaboostController;
    private final Logger logger;

    @Inject
    public EnsembleLearningController(final DataController dataController,
                                      final AdaboostController adaboostController, final Logger logger) {
        this.dataController = dataController;
        this.adaboostController = adaboostController;
        this.logger = logger;
    }

    // TODO: Rename and maybe split
    public void learn(String filename) {
        try {
            List<Data> dataset = dataController.load(filename);
            logger.log("Number of instances: " + dataset.size());

            DatasetSplitter<Data> datasetSplitter = new DatasetSplitter<Data>(dataset, 4.0 / 5.0, true);

            Map<Classifier,Double> classifiers = adaboostController.train(1, datasetSplitter.getTrainingSet());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IntegrityException e) {
            e.printStackTrace();
        }
    }
}
