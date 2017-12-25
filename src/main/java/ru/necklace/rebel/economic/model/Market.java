package ru.necklace.rebel.economic.model;

import java.util.Properties;

public class Market {

    private double coefLargeSales;
    private double coefExtraLargeSales;
    private double coefLargeInput;
    private double coefExtraLargeInput;
    private double coefIncome;

    public void setCoefs(double coefLargeSales, double coefExtraLargeSales, double coefLargeInput, double coefExtraLargeInput, double coefIncome){
        this.coefLargeSales = coefLargeSales;
        this.coefExtraLargeSales = coefExtraLargeSales;
        this.coefLargeInput = coefLargeInput;
        this.coefExtraLargeInput = coefExtraLargeInput;
        this.coefIncome = coefIncome;
    }
}
