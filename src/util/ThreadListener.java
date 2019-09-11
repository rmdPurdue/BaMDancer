package util;

/**
 * Interface that is used to create a custom thread listener.
 */
public interface ThreadListener {

    /**
     * Method is called in <code>incListener</code> whenever a new message is received
     */
    void incomingDataUpdated();
}
