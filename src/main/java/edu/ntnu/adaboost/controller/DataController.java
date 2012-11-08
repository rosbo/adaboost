package edu.ntnu.adaboost.controller;

import edu.ntnu.adaboost.model.Instance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataController {

    public List<Instance> load(String filename) throws IOException, IntegrityException {
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

    private void validateIntegrity(List<Instance> dataset) throws IntegrityException {
        if(dataset.isEmpty()){
            throw new IntegrityException("Empty dataset");
        }

        int featureCount = dataset.get(0).getFeatures().size();

        for(Instance instance : dataset){
            if(instance.getFeatures().size() != featureCount){
                throw new IntegrityException("All instance should have the same number of features");
            }
        }
    }

}
