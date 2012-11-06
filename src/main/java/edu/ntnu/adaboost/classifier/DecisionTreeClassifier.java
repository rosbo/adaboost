package edu.ntnu.adaboost.classifier;

import edu.ntnu.adaboost.model.Instance;

import java.util.List;

public class DecisionTreeClassifier implements Classifier {

    private final int dtcMaxDepth;

    public DecisionTreeClassifier(int dtcMaxDepth) {
        this.dtcMaxDepth = dtcMaxDepth;
    }

    @Override
    public void train(List<Instance> trainingSet) {
        // TODO: Implement
    }

    @Override
    public int predict(List<Double> features) {
        // TODO: Implement
        return 0;
    }

}
