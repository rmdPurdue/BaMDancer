import com.DeviceDiscoveryQuery;
import com.DiscoveryQueryListener;
import cues.Cue;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mvc.*;
import osc.OSCListener;
import osc.OSCPortIn;
import util.CountdownTimer;
import devices.DeviceToCalibrate;
import devices.RemoteDevice;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application implements PropertyChangeListener {

    private Model model;
    private DeviceSetupController deviceSetupController;
    private Controller controller;
    private ExecutorService executor;
    private DiscoveryQueryListener discoveryQueryListener;
    private DeviceDiscoveryQuery deviceDiscoveryQuery;
    private CountdownTimer countdownTimer;
    private ProcessingService service;
    private StopService stopService;
    private MessageWrapper messageWrapper;

    @Override
    public void start(Stage primaryStage) throws Exception{
        /*
            Establish stage and primary controller
         */

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mvc/main.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();

        /*
           Create a new data model.
         */

        model = new Model();

        /*
           Establish background threads for application services
         */

        executor = Executors.newFixedThreadPool(5);
        deviceDiscoveryQuery = new DeviceDiscoveryQuery(5);
        discoveryQueryListener = new DiscoveryQueryListener();
        countdownTimer = new CountdownTimer(5);

        /*
            Hook up controllers to model.
        */
        hookupConnections();

        /*
           Start listening for OSC messages here.
         */

        startListeningOSC();

        service = new ProcessingService();
        stopService = new StopService();

        BooleanBinding serviceRunning = service.runningProperty().or(stopService.runningProperty());
        messageWrapper = new MessageWrapper("Not Started.", "Not Connected.", false);
        //view.messagesLabel.textProperty().bind(messageWrapper.runStatusProperty());
        //view.ipConnectedLabel.textProperty().bind(service.valueProperty());
        //view.cancelButton.disableProperty().bind(messageWrapper.runningProperty().not().or(serviceRunning));

        service.messageProperty().addListener((ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> messageWrapper.runStatus.set(newValue));

        stopService.messageProperty().addListener((ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> messageWrapper.runStatus.set(newValue));

        service.setOnSucceeded(event -> System.out.println("Succeeded"));

        // Add close application handler to kill all threads
    }

    private void hookupConnections() throws IOException {

        /*
            Get controllers for different views.
         */

        deviceSetupController = controller.setDeviceLoader(new FXMLLoader(getClass().getResource("/mvc/deviceSetup.fxml")));
        CueListController cueListController = controller.setCueLoader(new FXMLLoader(getClass().getResource("/mvc/cueList.fxml")));
        PlaybackController playbackController = controller.setPlaybackLoader(new FXMLLoader(getClass().getResource("/mvc/playback.fxml")));

        /*
           Connect controllers to the model.
         */

        controller.setModel(model);
        deviceSetupController.setModel(model);
        cueListController.setModel(model);
        playbackController.setModel(model);

        /*
            Add property change listeners for talk back from controllers and discovery query.
         */

        deviceSetupController.addPropertyChangeListener(this);
        deviceDiscoveryQuery.addPropertyChangeListener(this);

        /*
            Bind scan network dialog box progress bar to query timeout progress.
         */

        deviceSetupController.getProgressBar().progressProperty().bind(deviceDiscoveryQuery.getPercentTimeElapsed().divide(10));

        /*
            Connect playback go button to model.
         */

        playbackController.goButton.setOnAction(actionEvent -> {
            if (playbackController.cueListTableView.getSelectionModel().getSelectedItems().size() != 0) {
                try {
                    if(model.resetLevels()) model.goCue(new Cue(playbackController.cueListTableView.getSelectionModel().getSelectedItem()));
                } catch (CloneNotSupportedException | InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Sending Cue.");
                if (playbackController.cueListTableView.getSelectionModel().getTableView().getItems().size() > playbackController.cueListTableView.getSelectionModel().getSelectedIndex() + 1) {
                    playbackController.cueListTableView.getSelectionModel().select(playbackController.cueListTableView.getSelectionModel().getSelectedIndex() + 1);
                } else {
                    playbackController.cueListTableView.getSelectionModel().clearSelection();
                }
            }
        });

    }

    private void startListeningOSC() throws SocketException {
        // Create an OSC receiver object on port 8001
        OSCPortIn receiver = new OSCPortIn(8001);

        // Create an OSC listener, connect to model method for parsing the message
        OSCListener listener = (time, message) -> {
            System.out.println("Received message addressed to: " + message.getAddress());
            System.out.println("Message length: " + message.getArguments().size());
            model.parseIncomingOSCMessage(message);
        };

        // Add listener for "/device_setup" messages
        receiver.addListener("/device_setup", listener);

        // Add listener for "/calibrate/low" messages
        receiver.addListener("/calibrate", listener);

        // Add listener for "/saved" messages
        receiver.addListener("/saved", listener);

        // Start listener thread
        receiver.startListening();
    }

    private void startNetworkDiscoveryScan() {
        System.out.println("Got here.");

        // Start the discovery listener thread
        executor.submit(discoveryQueryListener);

        // Start the network discovery scan thread
        executor.submit(deviceDiscoveryQuery);
    }

    private void stopNetworkDiscoveryScan() {
        // Stop the discovery scan thread
        deviceDiscoveryQuery.stopDiscovery();

        // Stop the discovery listener thread
        discoveryQueryListener.stopDiscoveryListening();
    }

    private void cleanUpAfterNetworkScan() throws IOException {

        System.out.println("Discovery Query timeout received.");

        // Stop listener for discovery responses
        discoveryQueryListener.stopDiscoveryListening();

        // Update model with found devices
        model.setSenderDevices(discoveryQueryListener.getDiscoveredDevices());

        // refresh main display
        deviceSetupController.updateDeviceTable();
    }

    private void sendSettingsToDevice(RemoteDevice device) {
        System.out.println("Remote Device to send changes to: " + device.getDeviceName());
        System.out.println("MAC Address of remote device to send changes to: " + device.getMacAddress());
        try {
            model.sendUpdateFirmwareCommand(device);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void calibrate(DeviceToCalibrate deviceToCalibrate) {

        // Start a timer
        executor.submit(countdownTimer);
        try {

            // send calibration command to remote device
            model.sendCalibrationCommand(deviceToCalibrate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

        System.out.println("heard property change.");
        // Property change listener setup

        String property = propertyChangeEvent.getPropertyName();
        Object value = propertyChangeEvent.getNewValue();

        if(property.equals("startScanning")) startNetworkDiscoveryScan();

        if(property.equals("stopScanning")) stopNetworkDiscoveryScan();

        if(property.equals("scanComplete")) try {
            cleanUpAfterNetworkScan();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(property.equals("saveDeviceSettings")) sendSettingsToDevice((RemoteDevice) value);

        if(property.equals("calibrate")) {
            calibrate((DeviceToCalibrate)value);
        }

    }

    private class ProcessingService extends Service<String> {
        @Override
        protected void succeeded() {
            messageWrapper.setRunStatus("Running.");
            messageWrapper.setRunningProperty(true);
        }

        @Override
        protected void failed() {
            messageWrapper.setRunStatus("Could not start.");
            messageWrapper.setRunningProperty(false);
        }

        @Override
        protected void cancelled() {
            messageWrapper.setRunStatus("Cancelled");
            messageWrapper.setRunningProperty(false);
        }

        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                @Override
                protected String call() throws Exception {
                    if(!model.running) {
                        model.start();
                    } else {
                        model.resume();
                        Thread.sleep(1000);
                    }
                    //return model.ipStatus;
                    return null;
                }
            };
        }

    }

    public class StopService extends Service<String> {
        @Override
        protected void succeeded() {
            messageWrapper.setRunStatus("Stopped.");
            messageWrapper.setRunningProperty(false);
        }

        @Override
        protected void cancelled() {
            messageWrapper.setRunStatus("Stopping cancelled.");
            messageWrapper.setRunningProperty(true);
        }

        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                @Override
                protected String call() throws Exception {
                    //updateMessage("Stopping...");
                    model.stop();
                    Thread.sleep(1000);
                    //updateMessage("Stopped.");
                    //return model.ipStatus;
                    return null;
                }
            };
        }
    }

    private class MessageWrapper {
        StringProperty runStatus = new SimpleStringProperty();
        StringProperty ipStatus = new SimpleStringProperty();
        BooleanProperty runningProperty = new SimpleBooleanProperty(Boolean.FALSE);

        public MessageWrapper(String runStatusString, String ipStatusString, Boolean running) {
            this.runStatus.setValue(runStatusString);
            this.ipStatus.setValue(ipStatusString);
            this.runningProperty.setValue(running);
        }

        public String getRunStatus() {
            return runStatus.get();
        }

        public StringProperty runStatusProperty() {
            return runStatus;
        }

        public void setRunStatus(String runStatus) {
            this.runStatus.set(runStatus);
        }

        public String getIpStatus() {
            return ipStatus.get();
        }

        public StringProperty ipStatusProperty() {
            return ipStatus;
        }

        public void setIpStatus(String ipStatus) {
            this.ipStatus.set(ipStatus);
        }

        public boolean isRunningProperty() {
            return runningProperty.get();
        }

        public BooleanProperty runningProperty() {
            return runningProperty;
        }

        public void setRunningProperty(boolean runningProperty) {
            this.runningProperty.set(runningProperty);
        }
    }


}
