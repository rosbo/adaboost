package edu.ntnu.adaboost.controller;

import edu.ntnu.adaboost.classifier.*;
import edu.ntnu.adaboost.ensemblelearning.Adaboost;
import edu.ntnu.adaboost.model.Instance;
import edu.ntnu.adaboost.utils.DatasetSplitter;
import edu.ntnu.adaboost.utils.Logger;
import edu.ntnu.adaboost.utils.PercentageFormat;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppController {
    private final DataController dataController;
    private final Logger logger;
    private final DecisionTreeClassifierHelper decisionTreeClassifierHelper;
    private final PercentageFormat percentageFormat;

    @Inject
    public AppController(final DataController dataController, final Logger logger, final DecisionTreeClassifierHelper
            decisionTreeClassifierHelper, final PercentageFormat percentageFormat) {
        this.dataController = dataController;
        this.logger = logger;
        this.decisionTreeClassifierHelper = decisionTreeClassifierHelper;
        this.percentageFormat = percentageFormat;
    }

    public void start(String filename, int maxDifferentValuesPerFeature, double trainTestRatio, int nbcCount,
                      int dtcCount, int dtcMaxDepth) {
        try {
            List<Instance> dataset = dataController.load(filename, maxDifferentValuesPerFeature);
            DatasetSplitter<Instance> datasetSplitter = new DatasetSplitter<Instance>(dataset, trainTestRatio, true);
            List<Instance> trainingSet = datasetSplitter.getTrainingSet();
            List<Instance> testSet = datasetSplitter.getTestSet();

            logger.log("Number of instances: " + dataset.size() + " (train:" + trainingSet.size() + "/test:" +
                    testSet.size() + ")");

            List<Classifier> classifiers = createClassifiers(nbcCount, dtcCount, dtcMaxDepth);
            Adaboost adaboost = new Adaboost(classifiers);
            adaboost.train(trainingSet);

            computeClassifierErrors(testSet, adaboost.getWeightedClassifiers());
            computeAdaboostTestError(testSet, adaboost);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IntegrityException e) {
            e.printStackTrace();
        }
    }

    private void computeClassifierErrors(List<Instance> testSet, Map<Classifier,
            ClassifierStatistics> weightedClassifiers) {
        SummaryStatistics nbcTrainingErrors = new SummaryStatistics();
        SummaryStatistics dtcTrainingErrors = new SummaryStatistics();
        SummaryStatistics nbcTestErrors = new SummaryStatistics();
        SummaryStatistics dtcTestErrors = new SummaryStatistics();

        for (Map.Entry<Classifier, ClassifierStatistics> entry : weightedClassifiers.entrySet()) {
            Classifier classifier = entry.getKey();
            double trainingError = entry.getValue().getTrainingError();

            if (classifier instanceof NaiveBayesianClassifier) {
                nbcTrainingErrors.addValue(trainingError);
                nbcTestErrors.addValue(computeClassifierTestError(classifier, testSet));
            } else if (classifier instanceof DecisionTreeClassifier) {
                dtcTrainingErrors.addValue(trainingError);
                dtcTestErrors.addValue(computeClassifierTestError(classifier, testSet));
            }
        }

        if (nbcTrainingErrors.getN() > 0) {
            logger.log("--- NBCs");
            logger.log("AVG training errors: " + percentageFormat.format(nbcTrainingErrors.getMean()));
            logger.log("SD training errors: " + percentageFormat.format(nbcTrainingErrors.getStandardDeviation()));
            logger.log("AVG test errors: " + percentageFormat.format(nbcTestErrors.getMean()));
            logger.log("SD test errors: " + percentageFormat.format(nbcTestErrors.getStandardDeviation()));
        }
        if (dtcTrainingErrors.getN() > 0) {
            logger.log("--- DTCs");
            logger.log("AVG training errors: " + percentageFormat.format(dtcTrainingErrors.getMean()));
            logger.log("SD training errors: " + percentageFormat.format(dtcTrainingErrors.getStandardDeviation()));
            logger.log("AVG test errors: " + percentageFormat.format(dtcTestErrors.getMean()));
            logger.log("SD training test: " + percentageFormat.format(dtcTestErrors.getStandardDeviation()));
        }
    }

    private void computeAdaboostTestError(List<Instance> testSet, Adaboost adaboost) {
        int numberOfErrors = 0;
        for (Instance testInstance : testSet) {
            int predictedClass = adaboost.predict(testInstance.getFeatures());

            if (predictedClass != testInstance.getClazz()) {
                numberOfErrors++;
            }
        }

        double testAccuracy = (testSet.size() - numberOfErrors) / (double) testSet.size();
        logger.log("--- Adaboost");
        logger.log("Test error: " + percentageFormat.format(numberOfErrors / (double) testSet.size()));
        logger.log("Test accuracy: " + percentageFormat.format(testAccuracy));
    }

    private List<Classifier> createClassifiers(int nbcCount, int dtcCount, int dtcMaxDepth) {
        List<Classifier> classifiers = new ArrayList<Classifier>();

        logger.log("Number of NBCs: " + nbcCount);
        for (int i = 0; i < nbcCount; i++) {
            classifiers.add(new NaiveBayesianClassifier());
        }

        logger.log("Number of DTCs: " + dtcCount);
        for (int i = 0; i < dtcCount; i++) {
            classifiers.add(new DecisionTreeClassifier(dtcMaxDepth, decisionTreeClassifierHelper));
        }

        return classifiers;
    }

    private double computeClassifierTestError(Classifier classifier, List<Instance> testSet) {
        int nbErrors = 0;
        for (Instance testInstance : testSet) {
            int predict = classifier.predict(testInstance.getFeatures());

            if (predict != testInstance.getClazz()) {
                nbErrors++;
            }
        }
        return nbErrors / (double) testSet.size();
    }

}
