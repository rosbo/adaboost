package edu.ntnu.adaboost.model;

import java.util.List;

public class Instance {

    private List<Double> features;
    private int clazz;
    private double weight;

    public Instance(List<Double> features, int clazz) {
        this.clazz = clazz;
        this.features = features;
        weight = 1;
    }

    public List<Double> getFeatures() {
        return features;
    }

    public int getClazz() {
        return clazz;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
