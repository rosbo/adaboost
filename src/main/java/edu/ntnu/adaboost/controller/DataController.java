package edu.ntnu.adaboost.controller;

import edu.ntnu.adaboost.model.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataController {

    public List<Data> load(String filename) throws IOException, IntegrityException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<Data> dataset = new ArrayList<Data>();

        String line = bufferedReader.readLine();
        while (line != null) {
            Data data = extractData(line);
            dataset.add(data);
            line = bufferedReader.readLine();
        }

        validateIntegrity(dataset);

        return dataset;
    }

    private Data extractData(String line) {
        String[] pieces = line.split(",");

        // Features
        List<Double> features = new ArrayList<Double>();
        for (int i = 0; i < pieces.length - 1; i++) {
            Double feature = Double.parseDouble(pieces[i]);
            features.add(feature);
        }

        // Label
        int label = Integer.parseInt(pieces[pieces.length - 1]);

        return new Data(features, label);
    }

    private void validateIntegrity(List<Data> dataset) throws IntegrityException {
        if(dataset.isEmpty()){
            throw new IntegrityException("Empty dataset");
        }

        int featureCount = dataset.get(0).getFeatures().size();

        for(Data data : dataset){
            if(data.getFeatures().size() != featureCount){
                throw new IntegrityException("All data should have the same number of features");
            }
        }
    }

}
