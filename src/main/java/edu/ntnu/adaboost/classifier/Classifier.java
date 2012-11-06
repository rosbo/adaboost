package edu.ntnu.adaboost.classifier;

import edu.ntnu.adaboost.model.Instance;

import java.util.List;

public interface Classifier {

    public void train(List<Instance> trainingSet);

    public int predict(List<Double> features);

}
