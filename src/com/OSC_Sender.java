package com;

import osc.OSCBundle;
import osc.OSCMessage;
import osc.OSCPortOut;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * @author Rich Dionne
 * @project BaMSensorSetup
 * @package com
 * @date 9/24/2018
 */
public class OSC_Sender {

    public static void sendMessage(InetAddress ipAddress, int port, String url, List<Object> args ) throws IOException {

        System.out.println("Generating OSC message to " + ipAddress.toString() + ":" + port);
        // Create an OSC sender
        OSCPortOut sender = new OSCPortOut(ipAddress, port);

        // Create an OSC bundle
        OSCBundle bundle = new OSCBundle();

        System.out.println("Message: " + url + " " + args.get(0));
        // Create and OSC message with the passed URL and arguments
        OSCMessage message = new OSCMessage(url, args);

        // Add this message to our bundle
        bundle.addPacket(message);

        // Send the bundle with our sender
        sender.send(bundle);

        // Close our sender
        sender.close();
    }
}
