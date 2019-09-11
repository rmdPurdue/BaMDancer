package util;

import cues.Cue;
import devices.DancerDevice;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by pujamittal on 4/24/18.
 */
@XmlRootElement(name = "config")
public class ConfigWrapper {


    private List<Cue> cues;
    private List<DancerDevice> devices;

    @XmlElement(name = "cue")
    public List<Cue> getCues() {
        return this.cues;
    }

    public void setCues(List<Cue> cues) {
        this.cues = cues;
    }

    @XmlElement(name = "device")
    public List<DancerDevice> getDevices() {
        return this.devices;
    }

    public void setDevices(List<DancerDevice> devices) {
        this.devices = devices;
    }

}


