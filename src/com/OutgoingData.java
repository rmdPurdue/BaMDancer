package com;

import util.algorithms.*;

import java.net.InetAddress;

import static java.lang.Math.abs;

/**
 * The <code>OutgoingData</code> class builds the outgoing data packet for the
 * application to broadcast.
 *
 * This class is run when a listener on the incomingQueue is triggered within the Model.
 *
 * @author Puja Mittal (puja@purdue.edu)
 *
 */
public class OutgoingData {

    private InetAddress ipAddress;
    private int portNumber;
    private String URL;
    private float data;

    /**
     * Constructor for <code>OutgoingData</code> assigns parameters URL, alg_name, and rawData passed from
     * startProcessing() in <code>Model</code>. This constructor builds the outgoing data message to be
     * passed to the outgoingQueue.
     *
     * It takes in the URL to package it with, the algorithm name which it uses to find the correct algorithm
     * then runs it on the inputted data.
     *
     * @param URL
     * @param alg_name
     * @param rawData
     */
    public OutgoingData(InetAddress ipAddress, int portNumber, String URL, Algorithm alg_name, int rawData) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.URL = URL;
        switch(alg_name) {
             case PERCENT_LEVELS:
                this.data = new PercentLevels().calculate(rawData);
                break;

            case PERCENT_LEVELS_INVERTED:
                this.data =new PercentLevels().calculate(abs(255 - rawData));
                break;

            case PERCENT_TO_THIRTY:
                this.data = new PercentToThirty().calculate(rawData);
                break;

            case PERCENT_TO_FIFTY:
                this.data = new PercentToFifty().calculate(rawData);
                break;

            case PERCENT_TO_SEVENTY:
                this.data = new PercentToSeventy().calculate(rawData);
                break;

            case PERCENT_FROM_THIRTY:
                this.data = new PercentToThirty().calculate(abs(255 - rawData));
                break;

            case PERCENT_FROM_FIFTY:
                this.data = new PercentToFifty().calculate(abs(255 - rawData));
                break;

            case PERCENT_FROM_SEVENTY:
                this.data = new PercentToSeventy().calculate(abs(255 - rawData));
                break;

            case PERCENT_TWENTY_TO_SIXTY:
                this.data = new PercentTwentyToSixty().calculate(rawData);
                break;

            case PERCENT_SIXTY_TO_TWENTY:
                this.data = new PercentTwentyToSixty().calculate(abs(255 - rawData));
                break;

            case PARTIAL_PERCENT_HIGH:
                this.data = new PartialPercentHigh().calculate(rawData);
                break;

            case PARTIAL_PERCENT_LOW:
                this.data = new PartialPercentLow().calculate(rawData);
                break;

            case ONE_BYTE_LEVELS:
                this.data = new OneByteLevels().calculate(rawData);
                break;

            case ONE_BYTE_LEVELS_INVERTED:
                this.data = new OneByteLevels().calculate(abs(255-rawData));
                break;

            case UNIT_INTERVAL:
                this.data = new UnitInterval().calculate(rawData);
                break;

            case UNIT_INTERVAL_INVERTED:
                this.data = new UnitInterval().calculate(abs(255-rawData));
                break;

            case AUDIO_LEVELS:
                this.data = new AudioLevels().calculate(rawData);
                break;

            case AUDIO_LEVELS_INVERTED:
                this.data = new AudioLevels().calculate(abs(255-rawData));
                break;

            default:
                this.data = new OneByteLevels().calculate(rawData);
                break;
        }
//        System.out.println(this.data);
    }

    public InetAddress getIpAddress () {
        return ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    /**
     * @return String that is the URL the data will be broadcasted to
     */
    public String getURL() {
        return URL;
    }

    /**
     * @return int that is the raw data sent by the dancer
     */
    public float getData() { return data; }
}
