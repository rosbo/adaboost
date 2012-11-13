package edu.ntnu.adaboost.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Instance {

    private Map<Integer, Double> features = new HashMap<Integer, Double>();
    private int clazz;
    private double weight;

    public Instance(List<Double> features, int clazz) {
        this.clazz = clazz;

        for (int i = 0; i < features.size(); i++) {
            this.features.put(i, features.get(i));
        }
        weight = 1;
    }

    public List<Double> getFeatures() {
        return new ArrayList<Double>(features.values());
    }

    public void setFeature(int feature, double value) {
        features.put(feature, value);
    }

    public int featureCount() {
        return features.size();
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
