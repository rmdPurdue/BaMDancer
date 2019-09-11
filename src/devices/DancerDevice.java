package devices;

import java.util.ArrayList;

/**
 * @author Rich Dionne
 * @project BamDancerHub2.0
 * @package devices
 * @date 3/28/2018
 */

public class DancerDevice {

    private String name;
    private int numberOfInputs;
    public ArrayList<String> inputs = new ArrayList<>();

    public DancerDevice() {
    }

    public DancerDevice(String name) {
        this.name = name;
    }

    public DancerDevice(String name, int numberOfInputs) {
        this.name = name;
        this.numberOfInputs = numberOfInputs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfInputs(int number) {
        this.numberOfInputs = number;
        for(int i = 1; i <= this.numberOfInputs; i++) {
            this.inputs.add("input" + Integer.toString(i));
        }
    }

    public String getName() {
        return this.name;
    }

    public int getNumberOfInputs() { return this.numberOfInputs; }

    public ArrayList<String> getInputs() {
        return inputs;
    }

}
