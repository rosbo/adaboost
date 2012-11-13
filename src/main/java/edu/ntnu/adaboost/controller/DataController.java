package edu.ntnu.adaboost.controller;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import edu.ntnu.adaboost.model.Instance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DataController {
    public List<Instance> load(String filename, int maxDifferentValuesPerFeature) throws IOException,
            IntegrityException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<Instance> dataset = new ArrayList<Instance>();

        String line = bufferedReader.readLine();
        while (line != null) {
            Instance instance = extractData(line);
            dataset.add(instance);
            line = bufferedReader.readLine();
        }

        validateIntegrity(dataset);
        normalizeData(dataset, maxDifferentValuesPerFeature);

        return dataset;
    }

    private Instance extractData(String line) {
        String[] pieces = line.split(",");

        // Features
        List<Double> features = new ArrayList<Double>();
        for (int i = 0; i < pieces.length - 1; i++) {
            Double feature = Double.parseDouble(pieces[i]);
            features.add(feature);
        }

        // Label
        int label = Integer.parseInt(pieces[pieces.length - 1]);

        return new Instance(features, label);
    }

    // Ensure that we have a maximum of 10 different values per feature
    private void normalizeData(List<Instance> dataset, int maxDifferentValuePerFeacture) {
        int featureCount = dataset.get(0).featureCount();

        for (int feature = 0; feature < featureCount; feature++) {
            Multimap<Double, Instance> instanceByFeatureValue = ArrayListMultimap.create();

            for (Instance instance : dataset) {
                instanceByFeatureValue.put(instance.getFeatures().get(feature), instance);
            }

            List<Double> distinctElements = new ArrayList<Double>(instanceByFeatureValue.keySet());
            if (distinctElements.size() > maxDifferentValuePerFeacture) {
                long groupSize = Math.round(dataset.size() / maxDifferentValuePerFeacture);

                Collections.sort(distinctElements);

                int p = 0;
                for (Double distinctElement : distinctElements) {
                    Collection<Instance> instances = instanceByFeatureValue.get(distinctElement);

                    long group = Math.round(p / (double) groupSize);
                    for (Instance instance : instances) {
                        instance.setFeature(feature, group);
                    }

                    p += instances.size();
                }
            }
        }
    }

    private void validateIntegrity(List<Instance> dataset) throws IntegrityException {
        if (dataset.isEmpty()) {
            throw new IntegrityException("Empty dataset");
        }

        int featureCount = dataset.get(0).featureCount();

        for (Instance instance : dataset) {
            if (instance.getFeatures().size() != featureCount) {
                throw new IntegrityException("All instance should have the same number of features");
            }
        }
    }

}
