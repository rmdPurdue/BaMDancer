package com;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static util.PortNumbers.UDP_SEND_PORT;

public class DeviceDiscoveryQuery implements Runnable {

    private InetAddress multicastIP = InetAddress.getByName("239.0.0.57");
    private DatagramSocket socket = null;
    private boolean newDiscovery = true;
    private int timeoutInSeconds = 0;
    private DoubleProperty percentTimeElapsed = new SimpleDoubleProperty(0.00);
    private PropertyChangeSupport discoveryCompletePropertyChange = new PropertyChangeSupport(this);
    private boolean discoveryComplete = false;

    public DeviceDiscoveryQuery(int timeoutInSeconds) throws UnknownHostException {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        discoveryCompletePropertyChange.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        discoveryCompletePropertyChange.removePropertyChangeListener(listener);
    }
    public DoubleProperty getPercentTimeElapsed() {
        return percentTimeElapsed;
    }

    private void unsetNewDiscovery() {
        newDiscovery = false;
    }

    public void stopDiscovery() {
        discoveryComplete = true;
        Thread.currentThread().interrupt();
        percentTimeElapsed.set(0);
        discoveryCompletePropertyChange.firePropertyChange("scanComplete", false, true);
    }

    @Override
    public void run() {
        // Set start time to current time
        long startTimeInMillis = System.currentTimeMillis();
        discoveryComplete = false;

        while(!discoveryComplete) {

            // Calculate time elapsed
            double timeElapsed = System.currentTimeMillis() - startTimeInMillis;

            // Calculate percent of time elapsed for progress indicator
            percentTimeElapsed.set(((timeElapsed / 1000) / timeoutInSeconds) * 10);

            // if time left is larger than timeout, stop process
            if((timeElapsed/1000) >= timeoutInSeconds) {
                System.out.println("Discovery timeout.");
                percentTimeElapsed.set(0);
                stopDiscovery();
            }

            try {
                // Open a UDP socket
                socket = new DatagramSocket(UDP_SEND_PORT.getValue());

                // Set our discovery query message
                String message = "Hello?";

                // Convert our message to a byte array
                byte[] multicastMsg = message.getBytes();

                // Create a UDP datagram packet with our message, addressed to the Multicast IP group and the UDP port set previously
                DatagramPacket packet = new DatagramPacket(multicastMsg, multicastMsg.length, multicastIP, UDP_SEND_PORT.getValue());

                // Send our outgoing message
                socket.send(packet);

                // Update properties
                if (newDiscovery) unsetNewDiscovery();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Close our UDP socket
            socket.close();

            // Wait 250 millis before doing it again
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                // don't worry about it.
            }
        }
    }

}
