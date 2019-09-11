package cues;

import devices.OutputAddress;
import devices.RemoteDevice;
import java.util.UUID;

public class OutputMapping {

    private RemoteDevice device;
    private String deviceName;
    private int input;
    private OutputAddress outputAddress;
    private UUID id;

    OutputMapping() {
        id = UUID.randomUUID();
    }

    OutputMapping(OutputMapping source) {
        id = UUID.randomUUID();
        device = source.device;
        deviceName = source.deviceName;
        input = source.input;
        outputAddress = new OutputAddress(source.outputAddress);
    }

    public OutputMapping(RemoteDevice device, int inputName, OutputAddress outputAddress) {
        id = UUID.randomUUID();
        this.device = device;
        this.deviceName = device.getDeviceName();
        this.input = inputName;
        this.outputAddress = outputAddress;
    }

    public UUID getId() { return id; }

    public RemoteDevice getDevice() { return device; }

    public void setDevice(RemoteDevice device) { this.device = device; }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getInput() {
        return input;
    }

    public void setInput(int inputName) {
        this.input = input;
    }

    public OutputAddress getOutputAddress() {
        return outputAddress;
    }

    public void setOutputAddress(OutputAddress outputAddress) {
        this.outputAddress = outputAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutputMapping that = (OutputMapping) o;

        return id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id.toString();
    }

}
