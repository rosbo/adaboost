package edu.ntnu.adaboost.classifier;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.math.DoubleMath;
import edu.ntnu.adaboost.model.Instance;

import java.util.*;

public class DecisionTreeClassifierHelper {

    public Node QuinlanDT(Collection<Instance> instances, List<Integer> features, int depth) {
        if (depth == 0 || (features.size() == 0 && instances.size() != 0) || features.size() <= 0) {
            return new Node(-1, 0, true);
        }

        // test each feature to find f*
        int fStar = chooseFeatureThatMinimizesAvgEntropy(instances, features);
        Node root = new Node(-1, fStar, false);

        // f* partitions instances into subset where k = number of possibile
        // values of f*
        Multimap<Double, Instance> partitions = ArrayListMultimap.create();
        for (Instance instance : instances) {
            double value = instance.getFeatures().get(fStar);
            partitions.put(value, instance);
        }

        // for each partition
        for (Double partitionValue : partitions.keySet()) {
            // chech if each instance in a partition share the same class
            Collection<Instance> partition = partitions.get(partitionValue);

            Multiset<Integer> classesInPartition = HashMultiset.create();
            for (Instance instance : partition) {
                classesInPartition.add(instance.getClazz());
            }

            boolean shareSameClass = classesInPartition.elementSet().size() == 1;

            if (!shareSameClass) {
                // call QuinlanDt(Si, features - f*)
                features.remove(fStar);
                root.addSuccessor(partitionValue, QuinlanDT(partition, features, depth - 1));
                features.add(fStar);
            } else {
                // create a leaf node with the same class as the instances of Si
                Integer clazz = classesInPartition.elementSet().iterator().next();
                Node leaf = new Node(clazz, fStar, true);
                root.addSuccessor(partitionValue, leaf);
            }
        }
        return root;
    }

    public int chooseFeatureThatMinimizesAvgEntropy(Collection<Instance> instances, List<Integer> features) {
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

    public double calculateAverageEntropy(Collection<Instance> instances, int idFeature) {
        double entropyAverage = 0.0;
        Set<Integer> classValues = new HashSet<Integer>();
        double totalWeight = 0.0;
        for (Instance instance : instances) {
            classValues.add(instance.getClazz());
            totalWeight += instance.getWeight();
        }

        // Ex: Red -> 1,1,1,11,2,3,1,3
        // Blue -> 2,3,4,1,1,1
        Multimap<Double, Integer> featureValue2classValues = ArrayListMultimap.create();
        Map<Double, Double> featureValue2partitionWeight = new HashMap<Double, Double>();
        for (Instance instance : instances) {
            // for calculating feature2class
            Double value = instance.getFeatures().get(idFeature);
            int clazz = instance.getClazz();
            featureValue2classValues.put(value, clazz);
            // for calculating feature2weight
            double instanceWeight = instance.getWeight();
            Double partialWeight = featureValue2partitionWeight.get(value);
            if (partialWeight == null) {
                partialWeight = 0.0;
            }
            partialWeight += instanceWeight;
            featureValue2partitionWeight.put(value, partialWeight);
        }

        for (Double featureValue : featureValue2classValues.keySet()) {// red,blue...
            double partitionWeight = featureValue2partitionWeight.get(featureValue);
            double featureValueOccurrences;
            double featureValueEntropy = 0.0;
            for (Integer classValue : classValues) {
                int classValueOccurrences = 0;
                Collection<Integer> classValuesList = featureValue2classValues.get(featureValue);
                for (Integer aClassValue : classValuesList) {
                    if (aClassValue.equals(classValue)) {
                        classValueOccurrences++;
                    }
                }
                featureValueOccurrences = classValuesList.size();
                double probability = classValueOccurrences / featureValueOccurrences;
                if (probability == 0) {
                    featureValueEntropy = 0;
                } else {
                    featureValueEntropy += (-1) * probability * DoubleMath.log2(probability);
                }

            }
            entropyAverage += partitionWeight / totalWeight * featureValueEntropy;
        }
        return entropyAverage;
    }

}
