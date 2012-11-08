package edu.ntnu.adaboost.ensemblelearning;

import edu.ntnu.adaboost.classifier.Classifier;
import edu.ntnu.adaboost.classifier.ClassifierStatistics;
import edu.ntnu.adaboost.model.Instance;
import edu.ntnu.adaboost.utils.FractionalMultiSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adaboost {

    private final Map<Classifier, ClassifierStatistics> weightedClassifiers = new HashMap<Classifier,
            ClassifierStatistics>();

    public Adaboost(List<Classifier> classifiers) {
        for (Classifier classifier : classifiers) {
            weightedClassifiers.put(classifier, new ClassifierStatistics());
        }
    }

    public void train(List<Instance> trainingSet) {
        initializeUniformWeights(trainingSet);

        for (Map.Entry<Classifier, ClassifierStatistics> entry : weightedClassifiers.entrySet()) {
            Classifier classifier = entry.getKey();
            ClassifierStatistics classifierStatistics = entry.getValue();

            classifier.train(trainingSet);
            updateWeights(classifier, classifierStatistics, trainingSet);
        }
    }

    public int predict(List<Double> features) {
        FractionalMultiSet<Integer> classVoting = new FractionalMultiSet<Integer>();
        for (Map.Entry<Classifier, ClassifierStatistics> entry : weightedClassifiers.entrySet()) {
            Classifier classifier = entry.getKey();
            double weight = entry.getValue().getWeight();
            int predictedClass = classifier.predict(features);
            classVoting.add(predictedClass, weight);
        }

        int predictedClass = -1;
        double bestVote = 0;
        for (Map.Entry<Integer, Double> entry : classVoting.entrySet()) {
            int clazz = entry.getKey();
            Double vote = entry.getValue();

            if (vote > bestVote) {
                bestVote = vote;
                predictedClass = clazz;
            }
        }

        return predictedClass;
    }

    public Map<Classifier, ClassifierStatistics> getWeightedClassifiers() {
        return weightedClassifiers;
    }

    private void updateWeights(Classifier classifier,
                               ClassifierStatistics classifierStatistics, List<Instance> trainingSet) {
        double error = 0;
        int errorCount = 0;

        for (Instance trainingInstance : trainingSet) {
            int predictLabel = classifier.predict(trainingInstance.getFeatures());

            if (predictLabel != trainingInstance.getClazz()) {
                error += trainingInstance.getWeight();
                errorCount++;
            } else {
                trainingInstance.setWeight(trainingInstance.getWeight() * (error / (1 - error)));
            }
        }

        normalizeWeights(trainingSet);

        double classifierWeight = Math.log((1 - error) / error);
        double trainingError = (double) errorCount / trainingSet.size();
        classifierStatistics.setWeight(classifierWeight);
        classifierStatistics.setTrainingError(trainingError);
    }

    private void initializeUniformWeights(List<Instance> trainingSet) {
        double weight = 1.0d / trainingSet.size();
        for (Instance trainingInstance : trainingSet) {
            trainingInstance.setWeight(weight);
        }
    }

    private void normalizeWeights(List<Instance> trainingSet) {
        double totalWeight = 0;
        for (Instance trainingInstance : trainingSet) {
            totalWeight += trainingInstance.getWeight();
        }

        for (Instance trainingInstance : trainingSet) {
            trainingInstance.setWeight(trainingInstance.getWeight() / totalWeight);
        }
    }

}
