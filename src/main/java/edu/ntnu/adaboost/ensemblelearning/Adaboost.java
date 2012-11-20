package edu.ntnu.adaboost.ensemblelearning;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import edu.ntnu.adaboost.classifier.Classifier;
import edu.ntnu.adaboost.classifier.ClassifierStatistics;
import edu.ntnu.adaboost.model.Instance;
import edu.ntnu.adaboost.utils.FractionalMultiSet;
import edu.ntnu.adaboost.utils.Random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adaboost {

    private final Map<Classifier, ClassifierStatistics> weightedClassifiers = new HashMap<Classifier,
            ClassifierStatistics>();
    private final boolean keepLosers;

    public Adaboost(List<Classifier> classifiers, boolean keepLosers) {
        this.keepLosers = keepLosers;
        for (Classifier classifier : classifiers) {
            weightedClassifiers.put(classifier, new ClassifierStatistics());
        }
    }

    public void train(List<Instance> trainingSet) {
        initializeUniformWeights(trainingSet);
        int numberOfDifferentLabels = getNumberOfDifferentLabels(trainingSet);

        for (Map.Entry<Classifier, ClassifierStatistics> entry : weightedClassifiers.entrySet()) {
            Classifier classifier = entry.getKey();
            ClassifierStatistics classifierStatistics = entry.getValue();

            boolean isValid = false;
            while (!isValid) {
                classifier.train(trainingSet);
                isValid = updateWeights(classifier, classifierStatistics, trainingSet, numberOfDifferentLabels);
            }
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
        double bestVote = -Double.MAX_VALUE;
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

    private boolean updateWeights(Classifier classifier,
                                  ClassifierStatistics classifierStatistics, List<Instance> trainingSet,
                                  int L) {
        double error = 0;
        int errorCount = 0;
        double beta = 1.5;

        for (Instance trainingInstance : trainingSet) {
            int predictLabel = classifier.predict(trainingInstance.getFeatures());

            if (predictLabel != trainingInstance.getClazz()) {
                error += trainingInstance.getWeight();
                errorCount++;
            }
        }

        double trainingError = (double) errorCount / trainingSet.size();
        double classifierWeight;

        if (keepLosers || error > 0 && error < (L - 1) / (double) L) {
            for (Instance trainingInstance : trainingSet) {
                int predictLabel = classifier.predict(trainingInstance.getFeatures());

                if (predictLabel != trainingInstance.getClazz()) {
                    trainingInstance.setWeight(trainingInstance.getWeight() * ((1 - error) / error) *
                            (L - 1));
                }
            }

            normalizeWeights(trainingSet);
            classifierWeight = Math.log((1 - error) / error);
        } else if (error >= (L - 1) / (double) L) {
            jiggleWeights(trainingSet, beta);
            return false;
        } else {
            jiggleWeights(trainingSet, beta);
            classifierWeight = 10 + Math.log(L - 1);
        }

        classifierStatistics.setWeight(classifierWeight);
        classifierStatistics.setTrainingError(trainingError);

        return true;
    }

    private void jiggleWeights(List<Instance> trainingSet, double beta) {
        double bound = 1 / Math.pow(trainingSet.size(), beta);
        for (Instance trainingInstance : trainingSet) {
            double weight = trainingInstance.getWeight();
            weight = Math.max(0, weight + Random.doubleBetween(-bound, bound));
            trainingInstance.setWeight(weight);
        }
        normalizeWeights(trainingSet);
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

    private int getNumberOfDifferentLabels(List<Instance> trainingSet) {
        Multiset<Integer> classMultiset = HashMultiset.create();

        for (Instance trainingInstance : trainingSet) {
            classMultiset.add(trainingInstance.getClazz());
        }

        return classMultiset.elementSet().size();
    }

}
