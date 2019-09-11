package util.algorithms;

public class PercentLevels implements Algorithms {

    @Override
    public float calculate(int rawData) { return (rawData * 100 / 255);
    }

}