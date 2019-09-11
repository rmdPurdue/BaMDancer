package util.algorithms;

import static java.lang.Math.round;

public class UnitInterval implements Algorithms {
    @Override
    public float calculate(int rawData) {
        return (float)(round(((float)rawData/255)*100.0)/100.0);
    }
}