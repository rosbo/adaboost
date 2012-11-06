package edu.ntnu.adaboost.classifier;

import java.util.List;

public interface Classifier {

    public int predict(List<Double> features);

}
