package edu.ntnu.adaboost.classifier;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.ntnu.adaboost.controller.AppController;
import edu.ntnu.adaboost.dependencyinjection.AdaboostModule;
import edu.ntnu.adaboost.model.Instance;

public class DecisionTreeClassifierTest {

    private List<Instance> instances;
    private DecisionTreeClassifier dtc;
    private DecisionTreeClassifierHelper decisionTreeClassifierHelper;

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new AdaboostModule());
        decisionTreeClassifierHelper = injector.getInstance(DecisionTreeClassifierHelper.class);
        int dtcMaxDepth = 3;
        dtc = new DecisionTreeClassifier(dtcMaxDepth,decisionTreeClassifierHelper);
        instances = new ArrayList<Instance>();
        List<Double> features = new ArrayList<Double>();
        features.add(1.0);
        features.add(1.0);
        Instance instance = new Instance(features, 1);
        instance.setWeight(0.05);
        instances.add(instance);

        features = new ArrayList<Double>();
        features.add(2.0);
        features.add(1.0);
        instance = new Instance(features, 1);
        instance.setWeight(0.03);
        instances.add(instance);

        features = new ArrayList<Double>();
        features.add(3.0);
        features.add(1.0);
        instance = new Instance(features, 2);
        instance.setWeight(0.02);
        instances.add(instance);

        features = new ArrayList<Double>();
        features.add(1.0);
        features.add(2.0);
        instance = new Instance(features, 2);
        instance.setWeight(0.1);
        instances.add(instance);

        features = new ArrayList<Double>();
        features.add(3.0);
        features.add(2.0);
        instance = new Instance(features, 1);
        instance.setWeight(0.1);
        instances.add(instance);

        features = new ArrayList<Double>();
        features.add(1.0);
        features.add(3.0);
        instance = new Instance(features, 2);
        instance.setWeight(0.02);
        instances.add(instance);

        features = new ArrayList<Double>();
        features.add(2.0);
        features.add(3.0);
        instance = new Instance(features, 1);
        instance.setWeight(0.02);
        instances.add(instance);

        features = new ArrayList<Double>();
        features.add(3.0);
        features.add(3.0);
        instance = new Instance(features, 1);
        instance.setWeight(0.01);
        instances.add(instance);
    }

    @Test
    public void testCalculateAverageEntropy() {
        assertEquals(0.787, this.decisionTreeClassifierHelper.calculateAverageEntropy(instances, 0), 0.001);
        assertEquals(0.964, this.decisionTreeClassifierHelper.calculateAverageEntropy(instances, 1), 0.001);
    }

    @Test
    public void testChooseFeatureThatMinimizesAvgEntropy() {
        List<Integer> features = new ArrayList<Integer>();
        features.add(0);// color
        features.add(1);// shape
        assertEquals(0, this.decisionTreeClassifierHelper.chooseFeatureThatMinimizesAvgEntropy(instances, features));
    }

    @Test
    public void testTrain() {

        this.dtc.train(instances);
        Node root = this.dtc.getRootNode();
        assertEquals(0, root.getFeatureId());
        assertEquals(3, root.getSuccessors().size());

        // second level
        Node firstChild, secondChild, thirdChild;
        firstChild = root.getSuccessors().get(1.0);
        secondChild = root.getSuccessors().get(2.0);
        thirdChild = root.getSuccessors().get(3.0);

        assertEquals(false, firstChild.isLeaf());
        assertEquals(true, secondChild.isLeaf());
        assertEquals(false, thirdChild.isLeaf());
        assertEquals(1, secondChild.getClazz());
        assertEquals(-1, thirdChild.getClazz());

        assertEquals(1, firstChild.getSuccessors().keySet().size());
        assertEquals(0, secondChild.getSuccessors().size());
        assertEquals(1, thirdChild.getSuccessors().size());
        
        //Feature.size == 0 && istances.size != 0 so the class is Unknown due to the ambiguity 
        assertEquals(-1,firstChild.getSuccessors().get(1.0).getClazz());
        assertEquals(-1,firstChild.getSuccessors().get(1.0).getClazz());
    }
    
    @Test
    public void testPredict(){
        this.dtc.train(instances);
        Node root = this.dtc.getRootNode();
        List<Double> instanceToPredict = new ArrayList<Double>();
        instanceToPredict.add(2.0);
        instanceToPredict.add(1.0);
        assertEquals(1,this.dtc.predict(instanceToPredict));
        
        instanceToPredict = new ArrayList<Double>();
        instanceToPredict.add(2.0);
        instanceToPredict.add(3.0);
        assertEquals(1,this.dtc.predict(instanceToPredict));
        
        //instance with no known values
        instanceToPredict = new ArrayList<Double>();
        instanceToPredict.add(5.0);
        instanceToPredict.add(6.0);
        assertEquals(-1,this.dtc.predict(instanceToPredict));
        
        //unknown because of the ambiguity
        instanceToPredict = new ArrayList<Double>();
        instanceToPredict.add(1.0);
        instanceToPredict.add(1.0);
        assertEquals(-1,this.dtc.predict(instanceToPredict));
    }

}
