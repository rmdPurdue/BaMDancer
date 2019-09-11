package util;

/**
 * @author Rich Dionne
 * @project BaMSensorSetup
 * @package util
 * @date 9/26/2018
 */
public enum OSCCommand {
    GET_SETTINGS(1),
    MINIMUM(0),
    MAXIMUM(1);

    private int command;

    OSCCommand(int command) {
        this.command = command;
    }

    public int getValue() {
        return command;
    }

}
