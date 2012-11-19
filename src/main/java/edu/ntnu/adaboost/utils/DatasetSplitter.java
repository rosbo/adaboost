package edu.ntnu.adaboost.utils;

import java.util.Collections;
import java.util.List;

public class DatasetSplitter<T> {

    private final List<T> trainingSet;
    private final List<T> testSet;

    public DatasetSplitter(List<T> dataset, double ratio, Boolean shuffle) {
        if (shuffle) {
            Collections.shuffle(dataset);
        }

        int splitPosition = (int) Math.round(dataset.size() * ratio);
        trainingSet = dataset.subList(0, splitPosition);
        testSet = dataset.subList(splitPosition, dataset.size());
    }

    public List<T> getTrainingSet() {
        return trainingSet;
    }

    public List<T> getTestSet() {
        return testSet;
    }

}
