package util;

/**
 * @author Rich Dionne
 * @project BaMSensorSetup
 * @package util
 * @date 9/26/2018
 */
public enum DeviceType {
    SENDER("Sender"),
    RECEIVER("Receiver"),
    MIXED("Mixed Sender/Receiver");

    private String type;

    DeviceType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }

}
