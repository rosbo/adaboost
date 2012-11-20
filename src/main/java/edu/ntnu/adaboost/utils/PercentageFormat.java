package edu.ntnu.adaboost.utils;

import java.text.NumberFormat;

public class PercentageFormat {

    private final NumberFormat numberFormat;

    public PercentageFormat() {
        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
    }

    public String format(double d) {
        return numberFormat.format(d * 100) + "%";
    }

}
