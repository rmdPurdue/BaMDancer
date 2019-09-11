package mvc;

import com.OutgoingData;
import com.incListener;
import com.outSender;
import cues.Cue;
import osc.OSCMessage;
import com.OSC_Sender;
import devices.DeviceList;
import devices.DeviceToCalibrate;
import devices.RemoteDevice;
import util.DeviceType;
import util.ThreadListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static util.OSCCommand.GET_SETTINGS;
import static util.OSCAddress.*;
import static util.PortNumbers.OSC_SEND_PORT;
import static util.algorithms.Algorithm.PERCENT_LEVELS;

public class Model implements ThreadListener {

    private DeviceList senderDevices = new DeviceList();
    private DeviceList receiverDevices = new DeviceList();
    private ArrayList<Cue> cueList = new ArrayList<>();
    private PropertyChangeSupport modelPropertyChangeSupport = new PropertyChangeSupport(this);

    private BlockingQueue<OSCMessage> incomingQueue = new ArrayBlockingQueue<>(1);
    private BlockingQueue<OutgoingData> outgoingQueue = new ArrayBlockingQueue<>(1);
    private incListener incoming = new incListener(incomingQueue);
    private outSender outgoing = new outSender(outgoingQueue);
    public boolean running = false;
    private boolean paused = false;

    private Cue cue = new Cue();
    private int data = 0;

    public Model() throws UnknownHostException {
        boolean inputCalibrated = false;
        boolean remoteDeviceSaved = false;

        // Add blank cues
        cueList.add(new Cue(1.0, "Test"));
        cueList.add(new Cue(2.0, "Test2"));

        // Add test devices
        senderDevices.getDevices().add(new RemoteDevice(
                InetAddress.getByName("192.168.2.2"),
                "FFFFFFFFFFFF",
                "Test")
        );
        senderDevices.getDeviceUsingMac("FFFFFFFFFFFF").setDeviceType(DeviceType.SENDER);
        senderDevices.getDeviceUsingMac("FFFFFFFFFFFF").setAddressToSendTo(InetAddress.getByName("192.168.2.10"));
        senderDevices.getDeviceUsingMac("FFFFFFFFFFFF").setPortToSendTo(8001);
        senderDevices.getDeviceUsingMac("FFFFFFFFFFFF").addAnalogInputs(6);

        receiverDevices.getDevices().add(new RemoteDevice(
                InetAddress.getByName("192.168.2.10"),
                "FFFFFFFFFFF1",
                "Ion")
        );
        receiverDevices.getDeviceUsingMac("FFFFFFFFFFF1").setDeviceType(DeviceType.RECEIVER);
        receiverDevices.getDeviceUsingMac("FFFFFFFFFFF1").setReceivePort(8001);
    }

    public void start() throws InterruptedException, UnknownHostException, SocketException {
        // Set main thread status properties
        running = true;
        paused = false;

        // Create threads to listen for incoming messages and to handle outgoing messages
        Thread incomingReader = new Thread(incoming);
        Thread outgoingSender = new Thread(outgoing);

        // Set message threads as daemons for easy cleanup
        incomingReader.setDaemon(true);
        outgoingSender.setDaemon(true);

        // Start message threads
        incomingReader.start();
        outgoingSender.start();

        /*
            Code for determining local IP. Not sure if we need it.
         */

        /*
        ipStatus = "Checking local connection...";

        Enumeration<NetworkInterface> iterNetwork;
        Enumeration<InetAddress> iterAddress;
        NetworkInterface network;
        InetAddress address;

        iterNetwork = NetworkInterface.getNetworkInterfaces();

        while(iterNetwork.hasMoreElements()) {
            network = iterNetwork.nextElement();
            if(!network.isUp()) {
                continue;
            }
            if(network.isLoopback()) {
                continue;
            }

            iterAddress = network.getInetAddresses();

            while(iterAddress.hasMoreElements()) {
                address = iterAddress.nextElement();

                if (address.isSiteLocalAddress()) {
                    ipStatus = "Local address: " + address.getHostAddress();
                }
            }
        }
        */

        // Wait a beat, then add Model as a listener for the incoming message thread
        Thread.sleep(500);
        incoming.addListener(this);
    }

    public void stop() {
        //Stop the incoming message thread. Update status variables.
        if(running) {
            incoming.stop();
        }
        paused = true;
    }

    public void resume() {
        //Restart incoming message thread. Update status variables.
        paused = false;
        incoming.resume();
    }

    @Override
    public void incomingDataUpdated() {
        System.out.println("Got a message.");
        // Create a new OSC Message

        OSCMessage message = new OSCMessage();

        // If there's data on the incoming message queue, grab it.
        if (incomingQueue.peek() != null) {
            try {
                message = incomingQueue.take();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(message.getAddress() + " " + message.getArguments());

            // If we are running and not paused:
            if (running && !paused) {
                OSCMessage finalMessage = message;

                // get the mappings for the current cue.
                this.cue.getOutputMappings().forEach(mapping -> {

                    // For each mapping, get the data argument based on the input number
                    // Create an instance of OutgoingData based on the mapping information
                    // for destination IP, destination port, OSC address, algorithm, and data.
                    // Then, add the outgoing data to the outgoing message queue.
                    if (finalMessage.getAddress().equals("/hub/" + mapping.getDeviceName())) {
                        this.data = (Integer) finalMessage.getArguments().get(mapping.getInput());
                        OutgoingData out = new OutgoingData(mapping.getOutputAddress().getIPaddress(),
                                mapping.getOutputAddress().getPortNumber(),
                                mapping.getOutputAddress().getUrl(),
                                mapping.getOutputAddress().getAlgorithm(),
                                data);
                        try {
                            this.outgoingQueue.put(out);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    void addPropertyChangeListener(PropertyChangeListener listener) {
        modelPropertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        modelPropertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setSenderDevices(ArrayList<RemoteDevice> devices) throws IOException {
        senderDevices.setDevices(devices);
        updateSenderDeviceData();
    }

    DeviceList getSenderDevices() { return senderDevices; }

    public void setReceiverDevices(ArrayList<RemoteDevice> devices) throws IOException {
        receiverDevices.setDevices(devices);
    }

    DeviceList getReceiverDevices() { return receiverDevices; }

    ArrayList<Cue> getCueList() {
        return cueList;
    }

    boolean cueExists(Double cueNumber) {
        for (Object aCueList : cueList) {
            Cue next = (Cue) aCueList;
            if (next.getCueNumber().equals(cueNumber)) {
                return true;
            }
        }
        return false;
    }

    public void goCue(Cue cue) throws CloneNotSupportedException {
        outgoingQueue.clear();
        incoming.addListener(this);
        this.cue = cue;
    }

    public boolean resetLevels() throws InterruptedException {
        System.out.println("Resetting Levels.");
        if(this.cue!=null) {
            System.out.println("Cue not null.");
            if(incoming.size() > 0 ) incoming.removeListener(this);
            System.out.println("Listener removed.");
            incomingQueue.clear();
            System.out.println("Incoming queue cleared.");
            outgoingQueue.clear();
            System.out.println("Outgoing queue cleared.");
            if(cue.getOutputMappings().size() > 0) {
                System.out.println("Resetting mapping levels.");
                this.cue.getOutputMappings().forEach(mapping -> {
                    System.out.println(mapping.getOutputAddress().getUrl());
                    OutgoingData out = new OutgoingData(
                            mapping.getOutputAddress().getIPaddress(),
                            mapping.getOutputAddress().getPortNumber(),
                            mapping.getOutputAddress().getUrl(),
                            PERCENT_LEVELS,
                            0
                    );

                    try {
                        outgoingQueue.put(out);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                });
            }
        }
        return true;
    }

    private void updateSenderDeviceData() throws IOException {
        for(RemoteDevice device : senderDevices.getDevices()) {
            List<Object> args = new ArrayList<>();
            args.add(GET_SETTINGS.getValue());
            OSC_Sender.sendMessage(device.getIpAddress(), OSC_SEND_PORT.getValue(), SETUP.toString(), args);
        }
    }

    public void parseIncomingOSCMessage(OSCMessage message) throws UnknownHostException {
        if(message.matches("/device_setup")) {
            System.out.println("Got message addressed to: /device_setup");
            String senderMACAddress = (String) message.getArguments().get(0);

            RemoteDevice temp = new RemoteDevice();

            temp.setMacAddress(senderMACAddress);

            temp.setDeviceName((String)message.getArguments().get(1));

            String address = message.getArguments().get(2) + "." +
                    message.getArguments().get(3) + "." +
                    message.getArguments().get(4) + "." +
                    message.getArguments().get(5);

            temp.setAddressToSendTo(InetAddress.getByName(address));

            temp.setPortToSendTo((int) message.getArguments().get(6));

            switch ((int)message.getArguments().get(7)) {
                case 1:
                    temp.setDeviceType(DeviceType.SENDER);
                    System.out.println("Device is a SENDER.");
                    break;
                case 2:
                    temp.setDeviceType(DeviceType.RECEIVER);
                    System.out.println("Device is a RECEIVER.");
                    break;
                case 3:
                    temp.setDeviceType(DeviceType.MIXED);
                    System.out.println("Device type is MIXED.");
                    break;
                default:
                    temp.setDeviceType(DeviceType.SENDER);
                    System.out.println("Device defaults to SENDER.");
                    break;
            }

            temp.addAnalogInputs((int) message.getArguments().get(8));

            for(int i = 0; i < temp.getNumberOfInputs(); i++) {

                temp.getAnalogInputs().get(i).setMinValue((int) message.getArguments().get((i*3) + 9));
                temp.getAnalogInputs().get(i).setMaxValue((int) message.getArguments().get((i*3) + 10));
                temp.getAnalogInputs().get(i).setFilterWeight((int) message.getArguments().get((i*3) + 11));
            }

            if (senderDevices.getDevices().contains(temp)) {
                int index = senderDevices.getDevices().indexOf(temp);
                temp.setIpAddress(senderDevices.getDevices().get(index).getIpAddress());
                senderDevices.getDevices().set(index, temp);
                System.out.println("Updated device (" +  senderDevices.getDevices().get(index).getMacAddress() + ") with new data.");
                System.out.println(senderDevices.getDevices().get(index).getNumberOfInputs() + " inputs.");
            }
            modelPropertyChangeSupport.firePropertyChange("updatedDeviceData", false, true);
        }

        if(message.matches("/calibrate")) {
            String senderMACAddress = (String)message.getArguments().get(0);
            System.out.println("Got calibration message from: " + senderMACAddress);
            System.out.println("Input number: " + message.getArguments().get(1));
            System.out.println("Calibration Type: " + message.getArguments().get(2));
            System.out.println("Calibrated value: " + message.getArguments().get(3));

            RemoteDevice temp = new RemoteDevice();

            temp.setMacAddress(senderMACAddress);

            if(senderDevices.getDevices().contains(temp)) {
                int index = senderDevices.getDevices().indexOf(temp);
                switch((int) message.getArguments().get(2)) {
                    case 0:
                        System.out.println("Updating minimum value.");
                        senderDevices.getDevices().get(index).getAnalogInput((Integer) message.getArguments().get(1)).setMinValue((int)message.getArguments().get(3));
                        break;
                    case 1:
                        System.out.println("Updating maximum value.");
                        senderDevices.getDevices().get(index).getAnalogInput((Integer) message.getArguments().get(1)).setMaxValue((int)message.getArguments().get(3));
                        break;
                }
                modelPropertyChangeSupport.firePropertyChange("updatedInputData", false, true);
            }
        }

        if(message.matches("/calibrate/high")) {
            String senderMACAddress = (String)message.getArguments().get(0);
            for (RemoteDevice dev : senderDevices.getDevices()) {
                if (dev.getMacAddress().equals(senderMACAddress)) {
                    dev.getAnalogInput((Integer) message.getArguments().get(1)).setMaxValue((Integer) message.getArguments().get(2));
                    modelPropertyChangeSupport.firePropertyChange("inputCalibrated", false, true);
                }
            }
        }

        if(message.matches("/saved")) {
            System.out.println("Got a saved confirmation.");
            if((boolean)message.getArguments().get(0)) {
                modelPropertyChangeSupport.firePropertyChange("remoteDeviceSaved", false, true);
            }
        }
    }

    public void sendCalibrationCommand(DeviceToCalibrate deviceToCalibrate) throws IOException {
        System.out.println("Sending command.");
        List<Object>args = new ArrayList<>();
        args.add(deviceToCalibrate.getInputNumber());
        args.add(deviceToCalibrate.getSettingToCalibrate().getValue());
        OSC_Sender.sendMessage(deviceToCalibrate.getDevice().getIpAddress(), OSC_SEND_PORT.getValue(), CALIBRATE.toString(), args);
    }

    public void sendUpdateFirmwareCommand(RemoteDevice device) throws IOException {
        List<Object> args = new ArrayList<>();
        args.add(device.getDeviceName());
        args.add(device.getDeviceName().length());
        byte[] bytes = device.getAddressToSendTo().getAddress();
        for (byte aByte : bytes) {
            args.add((int) aByte);
        }
        args.add(device.getPortToSendTo());
        for(int i = 0; i < 6; i++) {
            args.add(device.getAnalogInputs().get(i).getMinValue());
            args.add(device.getAnalogInputs().get(i).getMaxValue());
            args.add(device.getAnalogInputs().get(i).getFilterWeight());
        }
        OSC_Sender.sendMessage(device.getIpAddress(),OSC_SEND_PORT.getValue(), SAVE_SETTINGS.toString(), args);
    }

}
