package edu.ntnu.adaboost.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ntnu.adaboost.model.Instance;

public class DecisionTreeClassifierHelper {
    public Node QuinlanDT(List<Instance> instances, List<Integer> features, int depth) {
        if (depth == 0 || (features.size() == 0 && instances.size() != 0) || features.size() <= 0) {
            return new Node(-1, 0, true);
        }

        // test each feature to find f*
        int fStar = chooseFeatureThatMinimizesAvgEntropy(instances, features);
        Node root = new Node(-1, fStar, false);
        // f* partitions instances into subset where k = number of possibile
        // values of f*
        Map<Double, List<Instance>> partitions = new HashMap<Double, List<Instance>>();
        for (Instance instance : instances) {
            double value = instance.getFeatures().get(fStar);
            List<Instance> tmp = partitions.get(value);
            if (tmp == null) {
                tmp = new ArrayList<Instance>();
            }
            tmp.add(instance);
            partitions.put(value, tmp);
        }
        // for each partition
        for (Double partitionValue : partitions.keySet()) {
            // chech if each instance in a partition share the same class
            List<Instance> partition = partitions.get(partitionValue);
            int classValueToBeSHared = partition.get(0).getClazz();
            boolean shareSameClass = true;
            for (Instance instance : partition) {
                if (instance.getClazz() != classValueToBeSHared) {
                    shareSameClass = false;
                    break;
                }
            }
            if (!shareSameClass) {
                // call QuinlanDt(Si, features - f*)
                features.remove(fStar);
                root.addSuccessor(partitionValue, QuinlanDT(partition, features, depth - 1));
                features.add(fStar);
            } else {
                // create a leaf node with the same class as the instances of Si
                Node leaf = new Node(classValueToBeSHared, fStar, true);
                root.addSuccessor(partitionValue, leaf);
            }
        }
        return root;
    }

    public int chooseFeatureThatMinimizesAvgEntropy(List<Instance> instances, List<Integer> features) {
        int featureCount = features.size();
        int featureMin = 0;
        double minAvgEnt = Double.MAX_VALUE;
        for (int i = 0; i < featureCount; i++) {
            double currentAvgEnt = calculateAverageEntropy(instances, features.get(i));
            if (currentAvgEnt <= minAvgEnt) {
                minAvgEnt = currentAvgEnt;
                featureMin = i;
            }
        }
        return featureMin;
    }

    private double log2(double value) {
        if (value == 0) {
            return 0.0;// to avoid NaN (infinite)
        }
        return Math.log(value) / Math.log(2.0);
    }

    public double calculateAverageEntropy(List<Instance> instances, int idFeature) {

        double entropyAverage = 0.0;
        Set<Integer> classValues = new HashSet<Integer>();
        double totalWeight = 0.0;
        for (Instance instance : instances) {
            classValues.add(instance.getClazz());
            totalWeight += instance.getWeight();
        }

        // Ex: Red -> 1,1,1,11,2,3,1,3
        // Blue -> 2,3,4,1,1,1
        Map<Double, List<Integer>> featureValue2classValues = new HashMap<Double, List<Integer>>();
        Map<Double, Double> featureValue2partitionWeight = new HashMap<Double, Double>();
        for (Instance instance : instances) {
            // for calculating feature2class
            Double value = instance.getFeatures().get(idFeature);
            int clazz = instance.getClazz();
            List<Integer> tmp = featureValue2classValues.get(value);
            if (tmp == null) {
                tmp = new ArrayList<Integer>();
            }
            tmp.add(clazz);
            featureValue2classValues.put(value, tmp);
            // for calculating feature2weight
            double instanceWeight = instance.getWeight();
            Double partialWeight = featureValue2partitionWeight.get(value);
            if (partialWeight == null) {
                partialWeight = new Double(0);
            }
            partialWeight += instanceWeight;
            featureValue2partitionWeight.put(value, partialWeight);
        }

        for (Double featureValue : featureValue2classValues.keySet()) {// red,blue...
            double partitionWeight = featureValue2partitionWeight.get(featureValue);
            double featureValueOccurrences = 0.0;
            double featureValueEntropy = 0.0;
            for (Integer classValue : classValues) {
                int classValueOccurrences = 0;
                List<Integer> classValuesList = featureValue2classValues.get(featureValue);
                for (int i = 0; i < classValuesList.size(); i++) {
                    if (classValuesList.get(i).equals(classValue)) {
                        classValueOccurrences++;
                    }
                }
                featureValueOccurrences = classValuesList.size();
                double probability = classValueOccurrences / featureValueOccurrences;
                featureValueEntropy += (-1) * probability * log2(probability);

            }
            entropyAverage += partitionWeight / totalWeight * featureValueEntropy;
        }
        return entropyAverage;
    }
}
