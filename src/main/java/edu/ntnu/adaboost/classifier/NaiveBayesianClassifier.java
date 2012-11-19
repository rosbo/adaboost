package edu.ntnu.adaboost.classifier;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import edu.ntnu.adaboost.model.Instance;
import edu.ntnu.adaboost.utils.FractionalMultiSet;

import java.util.List;

public class NaiveBayesianClassifier implements Classifier {

    private Multiset<Integer> classProbability = HashMultiset.create();
    private Table<Integer, Integer, FractionalMultiSet<Double>> attrGivenClassProbability = HashBasedTable.create();

    public void train(List<Instance> trainingSet) {
        for (Instance instance : trainingSet) {
            int featureNumber = 0;
            for (Double featureValue : instance.getFeatures()) {
                FractionalMultiSet<Double> values = attrGivenClassProbability.get(instance.getClazz(), featureNumber);

                if (values == null) {
                    values = new FractionalMultiSet<Double>();
                    attrGivenClassProbability.put(instance.getClazz(), featureNumber, values);
                }

                values.add(featureValue, instance.getWeight());
                featureNumber++;
            }
            classProbability.add(instance.getClazz());
        }
    }

    public int predict(List<Double> features) {
        int bestClass = -1;
        double bestProb = 0;

        for (Integer clazz : attrGivenClassProbability.rowKeySet()) {
            // P(Class)
            double prob = (double) classProbability.count(clazz) / classProbability.size();

            // Multiplication of all P(attr|class)
            int featureNumber = 0;
            for (Double featureValue : features) {
                FractionalMultiSet<Double> featureCountGivenClass = attrGivenClassProbability.get(clazz, featureNumber);
                prob *= featureCountGivenClass.getValue(featureValue) / featureCountGivenClass.sum();

                featureNumber++;
            }


            if (prob > bestProb) {
                bestClass = clazz;
                bestProb = prob;
            }
        }

        return bestClass;
    }

}
