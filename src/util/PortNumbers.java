package util;

/**
 * @author Rich Dionne
 * @project BaMSensorSetup
 * @package util
 * @date 9/25/2018
 */
public enum PortNumbers {
    UDP_SEND_PORT(9001),
    UDP_RECEIVE_PORT(8000),
    OSC_SEND_PORT(9000),
    OSC_RECEIVE_PORT(8001);

    private int port;

    PortNumbers(int port) {
        this.port = port;
    }

    public int getValue() {
        return port;
    }
}
