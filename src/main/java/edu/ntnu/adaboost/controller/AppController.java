package edu.ntnu.adaboost.controller;

import edu.ntnu.adaboost.classifier.Classifier;
import edu.ntnu.adaboost.classifier.ClassifierStatistics;
import edu.ntnu.adaboost.classifier.DecisionTreeClassifier;
import edu.ntnu.adaboost.classifier.NaiveBayesianClassifier;
import edu.ntnu.adaboost.ensemblelearning.Adaboost;
import edu.ntnu.adaboost.model.Instance;
import edu.ntnu.adaboost.utils.DatasetSplitter;
import edu.ntnu.adaboost.utils.Logger;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppController {
    private final DataController dataController;
    private final Logger logger;

    @Inject
    public AppController(final DataController dataController, final Logger logger) {
        this.dataController = dataController;
        this.logger = logger;
    }

    public void start(String filename, double trainTestRatio, int nbcCount, int dtcCount) {
        try {
            List<Instance> dataset = dataController.load(filename);
            logger.log("Number of instances: " + dataset.size());

            DatasetSplitter<Instance> datasetSplitter = new DatasetSplitter<Instance>(dataset, trainTestRatio, true);
            List<Instance> trainingSet = datasetSplitter.getTrainingSet();
            List<Instance> testSet = datasetSplitter.getTestSet();
            logger.log("Number of training instances: " + trainingSet.size());
            logger.log("Number of test instances: " + testSet.size());

            List<Classifier> classifiers = createClassifiers(nbcCount, dtcCount);

            Adaboost adaboost = new Adaboost(classifiers);
            adaboost.train(trainingSet);

            printTrainingErrors(adaboost.getWeightedClassifiers());
            computeTestError(testSet, adaboost);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IntegrityException e) {
            e.printStackTrace();
        }
    }

    private void printTrainingErrors(Map<Classifier, ClassifierStatistics> weightedClassifiers) {
        SummaryStatistics nbcTrainingErrors = new SummaryStatistics();
        SummaryStatistics dtcTrainingErrors = new SummaryStatistics();

        for (Map.Entry<Classifier, ClassifierStatistics> entry : weightedClassifiers.entrySet()) {
            Classifier classifier = entry.getKey();
            double trainingError = entry.getValue().getTrainingError();

            if (classifier instanceof NaiveBayesianClassifier) {
                nbcTrainingErrors.addValue(trainingError);
            } else if (classifier instanceof DecisionTreeClassifier) {
                dtcTrainingErrors.addValue(trainingError);
            }
        }

        if (nbcTrainingErrors.getN() > 0) {
            logger.log("Average of the training errors for NBCs: " + nbcTrainingErrors.getMean());
            logger.log("Standard deviation of the training errors for NBCs: " + nbcTrainingErrors
                    .getStandardDeviation());
        }
        if (dtcTrainingErrors.getN() > 0) {
            logger.log("Average of the training errors for DTCs: " + dtcTrainingErrors.getMean());
            logger.log("Standard deviation of the training errors for DTCs: " + dtcTrainingErrors
                    .getStandardDeviation());
        }
    }

    private void computeTestError(List<Instance> testSet, Adaboost adaboost) {
        double testError = 0;
        for (Instance testInstance : testSet) {
            int predictedClass = adaboost.predict(testInstance.getFeatures());

            if (predictedClass != testInstance.getClazz()) {
                testError++;
            }
        }
        testError = testError / (double) testSet.size();
        logger.log("Test error: " + testError);
    }

    private List<Classifier> createClassifiers(int nbcCount, int dtcCount) {
        List<Classifier> classifiers = new ArrayList<Classifier>();

        logger.log("Number of NBCs: " + nbcCount);
        for (int i = 0; i < nbcCount; i++) {
            classifiers.add(new NaiveBayesianClassifier());
        }

        logger.log("Number of DTCs: " + dtcCount);
        for (int i = 0; i < dtcCount; i++) {
            classifiers.add(new DecisionTreeClassifier());
        }

        return classifiers;
    }

}
