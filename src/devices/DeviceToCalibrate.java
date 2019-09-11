package devices;

import util.OSCCommand;

/**
 * @author Rich Dionne
 * @project BaMDeviceSetup
 * @package util
 * @date 10/20/2018
 */
public class DeviceToCalibrate {

    private RemoteDevice device;
    private int inputNumber;
    private OSCCommand settingToCalibrate;

    public DeviceToCalibrate(RemoteDevice device, int inputNumber, OSCCommand settingToCalibrate) {
        this.device = device;
        this.inputNumber = inputNumber;
        this.settingToCalibrate = settingToCalibrate;
    }

    public RemoteDevice getDevice() {
        return device;
    }

    public void setDevice(RemoteDevice device) {
        this.device = device;
    }

    public int getInputNumber() {
        return inputNumber;
    }

    public void setInputNumber(int inputNumber) {
        this.inputNumber = inputNumber;
    }

    public OSCCommand getSettingToCalibrate() {
        return settingToCalibrate;
    }

    public void setSettingToCalibrate(OSCCommand settingToCalibrate) {
        this.settingToCalibrate = settingToCalibrate;
    }
}
