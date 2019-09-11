package devices;

import util.algorithms.Algorithm;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class OutputAddress {

    private InetAddress IpAddress;
    private String IpAddressString;
    private int portNumber;
    private RemoteDevice remoteDevice;
    private String url;
    private Algorithm algorithm;

    public OutputAddress() {
    }

    public OutputAddress(OutputAddress source) {
        IpAddress = source.IpAddress;
        IpAddressString = source.IpAddressString;
        portNumber = source.portNumber;
        remoteDevice = source.remoteDevice;
        url = source.url;
        algorithm = source.algorithm;
    }

    public OutputAddress(RemoteDevice remoteDevice, String url, Algorithm algorithm) {
        this.remoteDevice = remoteDevice;
        this.url = url;
        this.algorithm = algorithm;
    }

    public OutputAddress(InetAddress IpAddress, int portNumber) {
        this.IpAddress = IpAddress;
        this.IpAddressString = IpAddress.toString();
        this.portNumber = portNumber;
    }

    public OutputAddress(InetAddress IpAddress, int portNumber, String url, Algorithm algorithm) {
        this.IpAddress = IpAddress;
        this.IpAddressString = IpAddress.toString();
        this.portNumber = portNumber;
        this.url = url;
        this.algorithm = algorithm;
    }

    public OutputAddress(String IpAddressString, int portNumber, String url, Algorithm algorithm) throws UnknownHostException {
        this.IpAddress = InetAddress.getByName(IpAddressString);
        this.IpAddressString = IpAddressString;
        this.portNumber = portNumber;
        this.url = url;
        this.algorithm = algorithm;
    }

    @XmlTransient
    public InetAddress getIPaddress() {
        return IpAddress;
    }

    public void setIPaddress(InetAddress IPaddress) {
        this.IpAddress = IPaddress;
//        this.IpAddressString = IpAddress.toString();
    }

    @XmlElement
    public String getIpAddressString() { return IpAddressString; }

    public void setIpAddressString(String IpAddressString) throws UnknownHostException {
        this.IpAddressString = IpAddressString;
        this.IpAddress = InetAddress.getByName(IpAddressString.replace("/", ""));
    }

    @XmlElement
    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    @XmlElement
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElement
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setRemoteDevice(RemoteDevice remoteDevice) {
        this.remoteDevice = remoteDevice;
    }

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }
}
