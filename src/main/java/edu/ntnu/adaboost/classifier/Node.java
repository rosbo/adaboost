package edu.ntnu.adaboost.classifier;

import java.util.HashMap;
import java.util.Map;

public class Node {
    private int clazz;
    private int featureId;
    private Map<Double, Node> successors;
    private boolean isLeaf;

    public Node() {

    }

    public Node(int clazz, int featureId, boolean isLeaf) {
        this.clazz = clazz;
        this.featureId = featureId;
        this.successors = new HashMap<Double, Node>();
        this.isLeaf = isLeaf;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getClazz() {
        return clazz;
    }

    public void setClazz(int clazz) {
        this.clazz = clazz;
    }

    public void addSuccessor(double featureValue, Node node) {
        this.successors.put(featureValue, node);
    }

    public int getFeatureId() {
        return this.featureId;
    }

    public Map<Double, Node> getSuccessors() {
        return successors;
    }

    public void setSuccessors(Map<Double, Node> successors) {
        this.successors = successors;
    }

    @Override
    public String toString() {
        return "Node [clazz=" + clazz + ", featureId=" + featureId + ", successors=" + successors + ", isLeaf="
                + isLeaf + "]";
    }

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }
}
