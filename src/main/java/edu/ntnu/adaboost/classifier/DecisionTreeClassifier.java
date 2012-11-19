package edu.ntnu.adaboost.classifier;

import edu.ntnu.adaboost.model.Instance;

import java.util.ArrayList;
import java.util.List;

public class DecisionTreeClassifier implements Classifier {

    private final int dtcMaxDepth;
    private Node rootNode;
    private final DecisionTreeClassifierHelper decisionTreeClassifierHelper;

    public DecisionTreeClassifier(int dtcMaxDepth, final DecisionTreeClassifierHelper decisionTreeClassifierHelper) {
        this.dtcMaxDepth = dtcMaxDepth;
        this.rootNode = new Node();
        this.decisionTreeClassifierHelper = decisionTreeClassifierHelper;
    }

    public void train(List<Instance> trainingSet) {
        List<Integer> featureIds = new ArrayList<Integer>();
        for (int i = 0; i < trainingSet.get(0).featureCount(); i++) {
            featureIds.add(i);
        }
        this.rootNode = decisionTreeClassifierHelper.QuinlanDT(trainingSet, featureIds, dtcMaxDepth);
    }

    public int predict(List<Double> features) {
        int currentFeatureId = rootNode.getFeatureId();
        int classPredicted = rootNode.getClazz();
        double currentFeatureValue;
        Node currentNode = rootNode;

        while (currentNode != null && !currentNode.isLeaf()) {
            currentFeatureValue = features.get(currentFeatureId);
            currentNode = currentNode.getSuccessors().get(currentFeatureValue);

            if (currentNode == null) { // unknown feature value
                classPredicted = -1;
            } else {
                classPredicted = currentNode.getClazz();
                currentFeatureId = currentNode.getFeatureId();
            }
        }

        return classPredicted;
    }

    public Node getRootNode() {
        return rootNode;
    }

}
