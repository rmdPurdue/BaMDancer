package mvc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable, PropertyChangeListener {

    @FXML private AnchorPane displayPane;
    @FXML private Button setupButton;
    @FXML private Button cueListButton;
    @FXML private Button playbackButton;

    private Model model;

    private SplitPane devicePane;
    private SplitPane cuePane;
    private SplitPane playbackPane;

    private FXMLLoader deviceLoader;
    private FXMLLoader cueLoader;
    private FXMLLoader playbackLoader;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupButton.setOnAction(event -> {
            try {
                loadDeviceSetup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        cueListButton.setOnAction(event -> {
            try {
                loadCueList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        playbackButton.setOnAction(event -> {
            try {
                loadPlayback();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void setModel(Model model) {
        this.model = model;
    }

    public DeviceSetupController setDeviceLoader(FXMLLoader loader) throws IOException {
        deviceLoader = loader;
        devicePane = deviceLoader.load();
        return deviceLoader.getController();
    }

    public CueListController setCueLoader(FXMLLoader loader) throws IOException {
        cueLoader = loader;
        cuePane = cueLoader.load();
        return cueLoader.getController();
    }

    public PlaybackController setPlaybackLoader(FXMLLoader loader) throws IOException {
        playbackLoader = loader;
        playbackPane = playbackLoader.load();
        return playbackLoader.getController();
    }

    public DeviceSetupController getDeviceController() {
        return deviceLoader.getController();
    }

    private CueListController getCueController() {
        return cueLoader.getController();
    }

    private PlaybackController getPlaybackConttroller() { return playbackLoader.getController(); }

    @FXML
    private void loadDeviceSetup() throws IOException {
        displayPane.getChildren().setAll(devicePane);
    }

    @FXML
    private void loadCueList() throws IOException {
        getCueController().checkForDevices(!model.getSenderDevices().getDevices().isEmpty());
        displayPane.getChildren().setAll(cuePane);
    }

    @FXML
    private void loadPlayback() throws IOException {
        displayPane.getChildren().setAll(playbackPane);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

        System.out.println("heard property change.");
        // Property change listener setup
    }
}
