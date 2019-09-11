package devices;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DeviceType;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

public class RemoteDevice {

    private InetAddress ipAddress;
    private int receivePort;
    private String macAddress;
    private String deviceName;
    private InetAddress addressToSendTo;
    private int portToSendTo;
    private int numberOfAnalogInputs;
    private ObservableList<AnalogInput> analogInputs;
    private DeviceType deviceType;

    public RemoteDevice() {
    }

    public RemoteDevice(InetAddress ipAddress, String macAddress, String deviceName) {
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.deviceName = deviceName;
    }

    public void addAnalogInputs(int size) {
        numberOfAnalogInputs = size;
        analogInputs = FXCollections.observableList(new ArrayList<AnalogInput>(size));
        for(int i = 0; i < size; i++) {
            analogInputs.add(new AnalogInput(i, 0, 100, 10));
        }
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public InetAddress getAddressToSendTo() {
        return addressToSendTo;
    }

    public void setAddressToSendTo(InetAddress addressToSendTo) {
        this.addressToSendTo = addressToSendTo;
    }

    public int getPortToSendTo() {
        return portToSendTo;
    }

    public void setPortToSendTo(int portToSendTo) {
        this.portToSendTo = portToSendTo;
    }

    public ObservableList<AnalogInput> getAnalogInputs() {
        return analogInputs;
    }

    public void setAnalogInputs(ArrayList<AnalogInput> analogInputs) {
        this.analogInputs = FXCollections.observableList(analogInputs);
    }

    public int getNumberOfInputs() {
        return analogInputs.size();
    }

    public void addAnalogInput(int inputNumber, int minValue, int maxValue, int filterWeight) {
        analogInputs.add(new AnalogInput(inputNumber, minValue, maxValue, filterWeight));
    }

    public AnalogInput getAnalogInput(int index) {
        int listIndex = 0;
        for (AnalogInput input : analogInputs) {
            if (input.getInputNumber() == index) {
                listIndex = analogInputs.indexOf(input);
            }
        }
        return analogInputs.get(listIndex);
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RemoteDevice that = (RemoteDevice) o;

        return macAddress.equals(that.macAddress);
    }

    @Override
    public int hashCode() {
        return macAddress.hashCode();
    }

    @Override
    public String toString() {
        return deviceName;
    }
}
