package util;

import devices.DancerDevice;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement (name = "devices")
public class DeviceWrapper {

    private List<DancerDevice> devices;

    @XmlElement(name = "device")
    public List<DancerDevice> getDevices() {
        return this.devices;
    }

    public void setDevices(List<DancerDevice> devices) {
        this.devices = devices;
    }
}
