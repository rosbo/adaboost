package edu.ntnu.adaboost.utils;

import java.util.HashMap;
import java.util.Map;

public class FractionalMultiSet<K> {

    private Map<K, Double> map = new HashMap<K, Double>();

    public void clear() {
        map.clear();
    }

    public int numKeys() {
        return map.size();
    }

    public void add(K k, double val) {
        Double v = map.get(k);
        if (v == null)
            map.put(k, val);
        else
            map.put(k, v + val);
    }

    public double getValue(K k) {
        Double v = map.get(k);
        if (v == null)
            return 0;
        return v;
    }

    public Iterable<Map.Entry<K, Double>> entrySet() {
        return map.entrySet();
    }

    public double sum() {
        double sum = 0;
        for (double value : map.values()) {
            sum += value;
        }
        return sum;
    }
}
