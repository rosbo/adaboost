package edu.ntnu.adaboost.model;

import java.util.List;

public class Data {

    private List<Double> features;
    private int label;
    private double weight;

    public Data(List<Double> features, int label) {
        this.label = label;
        this.features = features;
        weight = 1;
    }

    public List<Double> getFeatures() {
        return features;
    }

    public int getLabel() {
        return label;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
