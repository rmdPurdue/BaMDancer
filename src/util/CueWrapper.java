package util;

import cues.Cue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "cues")
public class CueWrapper {

    private List<Cue> cues;

    @XmlElement(name = "cue")
    public List<Cue> getCues() {
        return this.cues;
    }

    public void setCues(List<Cue> cues) {
        this.cues = cues;
    }

}