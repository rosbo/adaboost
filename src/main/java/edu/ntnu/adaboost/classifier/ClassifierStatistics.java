package edu.ntnu.adaboost.classifier;

public class ClassifierStatistics {

    private double weight = 0;
    private double trainingError = 0;

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTrainingError() {
        return trainingError;
    }

    public void setTrainingError(double trainingError) {
        this.trainingError = trainingError;
    }

}
