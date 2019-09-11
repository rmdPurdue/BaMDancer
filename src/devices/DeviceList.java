package devices;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;
/**
 * @author Rich Dionne
 * @project BaMSensorSetup
 * @package util
 * @date 9/24/2018
 */
public class DeviceList {

    private ArrayList<RemoteDevice> devices = new ArrayList<>();

    public ObservableList<RemoteDevice> getDevices() {
        return FXCollections.observableList(devices);
    }

    public void setDevices(ArrayList<RemoteDevice> devices) {
        this.devices = devices;
    }

    public RemoteDevice getDeviceUsingMac(String macAddress) {
        for (RemoteDevice device : devices) {
            System.out.println("Search string: " + macAddress);
            System.out.println("Test String: " + device.getMacAddress());
            if (macAddress.equals(device.getMacAddress())) {
                System.out.println("Matches.");
                return device;
            }
        }
        return null;
    }
}
