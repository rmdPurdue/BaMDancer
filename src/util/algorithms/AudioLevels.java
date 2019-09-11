package util.algorithms;

/**
 * @author Rich Dionne
 * @project BamDancerHub2.0
 * @package util
 * @date 3/27/2018
 */
public class AudioLevels {

    public int calculate(int rawData) {
        return ((rawData) * (12 - (-60)) / (255) + (-60));
    }

}
