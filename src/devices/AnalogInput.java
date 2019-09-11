package devices;

import javafx.beans.property.SimpleStringProperty;

public class AnalogInput {

    /**
     * A class for storing calibration information about an analog input on a remote device.
     */

    private int inputNumber;
    private int minValue;
    private int maxValue;
    private int filterWeight;
    private SimpleStringProperty filterWeightString;

    /**
     * Default constructor
     */

    public AnalogInput() {
    }

    /**
     * Constructor with parameters.
     * @param inputNumber ordinal number representing this input on the remote device
     * @param minValue lowest raw value the input will read in regular use
     * @param maxValue highest raw value the input will read in regular use
     * @param filterWeight weighting value for input filtering (range of 10 to 50)
     */

    AnalogInput(int inputNumber, int minValue, int maxValue, int filterWeight) {
        this.inputNumber = inputNumber;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.filterWeight = filterWeight;
        this.filterWeightString = new SimpleStringProperty(String.valueOf(filterWeight));
    }

    public int getInputNumber() {
        return inputNumber;
    }

    public void setInputNumber(int inputNumber) {
        this.inputNumber = inputNumber;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getFilterWeight() {
        return filterWeight;
    }

    public void setFilterWeight(int filterWeight) {
        this.filterWeight = filterWeight;
        this.filterWeightString = new SimpleStringProperty(String.valueOf(filterWeight));
    }


    @Override
    public String toString() {
        return Integer.toString(inputNumber);
    }

}
