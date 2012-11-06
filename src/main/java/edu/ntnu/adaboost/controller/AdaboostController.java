package edu.ntnu.adaboost.controller;

import edu.ntnu.adaboost.classifier.Classifier;
import edu.ntnu.adaboost.model.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdaboostController {

    public Map<Classifier, Double> train(int numberOfHypothesis, List<Data> trainingSet) {
        initializeUniformWeights(trainingSet);

        Map<Classifier, Double> classifiers = new HashMap<Classifier, Double>();
        for (int i = 0; i < numberOfHypothesis; i++) {
            // TODO: Generate based on the parameters types
            Classifier classifier = generateHypothesis(trainingSet);
            updateWeights(classifiers, classifier, trainingSet);
        }

        return classifiers;
    }

    private Classifier generateHypothesis(List<Data> trainingSet) {
        // TODO:
        return null;
    }

    private void updateWeights(Map<Classifier, Double> classifiers, Classifier classifier, List<Data> trainingSet) {
        double error = 0;

        for (Data trainingData : trainingSet) {
            int predictLabel = classifier.predict(trainingData.getFeatures());

            if (predictLabel != trainingData.getLabel()) {
                error += trainingData.getWeight();
            } else {
                trainingData.setWeight(trainingData.getWeight() * (error / (1 - error)));
            }
        }

        normalizeWeights(trainingSet);

        double classifierWeight = Math.log((1 - error) / error);
        classifiers.put(classifier, classifierWeight);
    }

    private void initializeUniformWeights(List<Data> trainingSet) {
        double weight = 1.0d / trainingSet.size();
        for (Data trainingData : trainingSet) {
            trainingData.setWeight(weight);
        }
    }

    private void normalizeWeights(List<Data> trainingSet) {
        double totalWeight = 0;
        for (Data trainingData : trainingSet) {
            totalWeight += trainingData.getWeight();
        }

        for (Data trainingData : trainingSet) {
            trainingData.setWeight(trainingData.getWeight() / totalWeight);
        }
    }

}
