package cues;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Rich Dionne
 * @project BamDancerHub2.0
 * @package cues
 * @date 3/28/2018
 */
public class Cue {

    private Double cueNumber;
    private String cueDescription;
    private ObservableList<OutputMapping> outputMappings = FXCollections.observableArrayList();

    public Cue() {
    }

    public Cue(Cue source) {
        cueNumber = source.cueNumber;
        cueDescription = source.cueDescription;
        for(OutputMapping mapping : source.outputMappings) {
            outputMappings.add(new OutputMapping(mapping));
        }
    }

    public Cue(Double cueNumber, String cueDescription) {
        this.cueNumber = cueNumber;
        this.cueDescription = cueDescription;
    }

    public Cue(Double cueNumber, String cueDescription, OutputMapping outputMapping) {
        this.cueNumber = cueNumber;
        this.cueDescription = cueDescription;
        outputMappings.add(outputMapping);
    }

    public Cue(Double cueNumber, String cueDescription, ObservableList<OutputMapping> mappings) {
        this.cueNumber = cueNumber;
        this.cueDescription = cueDescription;
        this.outputMappings = mappings;
    }

    public Double getCueNumber() {
        return cueNumber;
    }

    public void setCueNumber(Double cueNumber) {
        this.cueNumber = cueNumber;
    }

    public String getCueDescription() {
        return cueDescription;
    }

    public void setCueDescription(String cueDescription) {
        this.cueDescription = cueDescription;
    }

    public void addOutputMapping(OutputMapping outputMapping) {
        outputMappings.add(outputMapping);
    }

    public ObservableList<OutputMapping> getOutputMappings() {
        return outputMappings;
    }

    public OutputMapping getMappingById(UUID id) {
        for (OutputMapping mapping : outputMappings) {
            if (mapping.getId().equals(id)) {
                return mapping;
            }
        }
        return null;
    }

    public void setOutputMappings(ObservableList<OutputMapping> mappings) {
        this.outputMappings = mappings;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;

        if(getClass() != o.getClass()) return false;

        if(this == o) return true;

        Cue cue = (Cue) o;
        return Objects.equals(cueNumber, cue.cueNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cueNumber);
    }
}
