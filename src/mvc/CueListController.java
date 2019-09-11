package mvc;

import cues.Cue;
import cues.OutputMapping;
import devices.AnalogInput;
import devices.OutputAddress;
import devices.RemoteDevice;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.converter.DoubleStringConverter;
import util.DialogType;
import util.algorithms.Algorithm;

import java.net.URL;
import java.util.*;

import static util.DialogType.DELETE_CUE;
import static util.DialogType.DELETE_MAPPING;

/**
 * @author Rich Dionne
 * @project BaMDancer
 * @package mvc
 * @date 7/4/2019
 */
public class CueListController implements Initializable {

    @FXML private TableView<Cue> cueListTableView;
    @FXML private TableColumn<Cue, Double> cueListNumberColumn;
    @FXML private TableColumn<Cue, String> cueListLabelColumn;

    @FXML private Button newCueButton;
    @FXML private Button copyCueButton;
    @FXML private Button deleteCueButton;

    @FXML private Label errorLabel;
    @FXML private Label cueNumberDisplayLabel;
    @FXML private Label cueDescriptionDisplayLabel;
    @FXML private TextField cueNumberTextField;
    @FXML private TextField cueLabelTextField;

    @FXML private TableView<OutputMapping> mappingTableView;
    @FXML private TableColumn<OutputMapping, String> mappingDeviceColumn;
    @FXML private TableColumn<OutputMapping, String> mappingInputColumn;
    @FXML private TableColumn<OutputMapping, String> mappingDestinationColumn;
    @FXML private TableColumn<OutputMapping, String> mappingURLColumn;
    @FXML private TableColumn<OutputMapping, String> mappingAlgorithmColumn;
    @FXML private TableColumn<OutputMapping, String> remoteMacAddressColumn;

    @FXML private Button addMappingButton;
    @FXML private Button removeMappingButton;

    private Model model;
    private Cue cue = new Cue();

    public void setModel(Model model) {
        this.model = model;
        setCueList(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // TODO: make both tableviews traversible with keys.

        newCueButton.setDisable(true);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        enableMappingButtons(false);

        cueNumberTextField.focusedProperty().addListener((obs, oldVal, newVal) -> isCueNumberValid(cueNumberTextField.getText()));

        deleteCueButton.setOnAction(e -> {
            String header = "Confirm Cue Deletion";
            String content = "Are you sure you want to delete Cue " + cueListTableView.getSelectionModel().getSelectedItem().getCueNumber() + "?";
            confirmationDialog(header, content, DELETE_CUE);
        });

        copyCueButton.setOnAction(e -> {
            clearCueEditPane();
            Cue temp = new Cue();
            temp.setCueNumber(cueListTableView.getSelectionModel().getSelectedItem().getCueNumber() + 1.0);
            temp.setCueDescription(cueListTableView.getSelectionModel().getSelectedItem().getCueDescription());
            temp.setOutputMappings(cueListTableView.getSelectionModel().getSelectedItem().getOutputMappings());
            while(model.getCueList().contains(temp)) temp.setCueNumber(temp.getCueNumber() - 0.9);
            // TODO: check if this works for decimal cue numbers.
        });

        addMappingButton.setOnAction(e -> addMappingDialog(null));

        removeMappingButton.setOnAction(e -> {
            String header = "Confirm Mapping Deletion";
            String content = "Are you sure you want to delete this mapping:" +
                    System.lineSeparator() +
                    "Device: " +
                    mappingTableView.getSelectionModel().getSelectedItem().getDeviceName() +
                    System.lineSeparator() +
                    "Input: " +
                    mappingTableView.getSelectionModel().getSelectedItem().getInput() +
                    System.lineSeparator() +
                    "Destination device: " +
                    mappingTableView.getSelectionModel().getSelectedItem().getOutputAddress().getRemoteDevice().getDeviceName() +
                    System.lineSeparator() +
                    "OSC URL: " +
                    mappingTableView.getSelectionModel().getSelectedItem().getOutputAddress().getUrl() +
                    System.lineSeparator() +
                    "Algorithm: " +
                    mappingTableView.getSelectionModel().getSelectedItem().getOutputAddress().getAlgorithm().toString();
            confirmationDialog(header, content, DELETE_MAPPING);
        });

        newCueButton.setOnAction(e -> {
            Cue cue = new Cue(Double.parseDouble(cueNumberTextField.getText()),
                    cueLabelTextField.getText());
            model.getCueList().add(cue);
            setCueList(cue);
            cueNumberTextField.clear();
            cueLabelTextField.clear();
            newCueButton.setDisable(true);
        });

        cueListNumberColumn.setCellValueFactory(new PropertyValueFactory<>("cueNumber"));
        cueListNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        cueListNumberColumn.setOnEditCommit(e -> {
            if(!model.cueExists(e.getNewValue())) {
                isCueNumberValid(String.valueOf(e.getNewValue()));
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setCueNumber(e.getNewValue());
                setCueList(null);
            } else {
                isCueNumberValid(String.valueOf(e.getNewValue()));
                e.getTableView().getItems().get(e.getTablePosition().getRow()).setCueNumber(e.getOldValue());
                // TODO: find out why this isn't opening the editable cell.
                cueListTableView.edit(0, cueListNumberColumn);
            }
        });

        cueListLabelColumn.setCellValueFactory(new PropertyValueFactory<>("cueDescription"));
        cueListLabelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cueListLabelColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setCueDescription(e.getNewValue()));

        cueListTableView.setRowFactory(tv -> {
            TableRow<Cue> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    setCue(row.getItem());
                }
            });
            return row;
        });

        cueListTableView.setPlaceholder(new Label("No cues saved."));
        cueListTableView.getSortOrder().add(cueListNumberColumn);

        mappingDeviceColumn.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        mappingInputColumn.setCellValueFactory(new PropertyValueFactory<>("inputName"));
        mappingURLColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOutputAddress().getUrl()));
        mappingAlgorithmColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOutputAddress().getAlgorithm().getValue()));
        remoteMacAddressColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOutputAddress().getRemoteDevice().getMacAddress()));
        mappingDestinationColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOutputAddress().getRemoteDevice().getDeviceName()));

        mappingTableView.setRowFactory((TableView<OutputMapping> tv) -> {
            TableRow<OutputMapping> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    OutputMapping mapping = row.getItem();
                    addMappingDialog(mapping);
                }
            });
            return row;
        });

        mappingTableView.setPlaceholder(new Label("No mappings recorded."));
    }

    void checkForDevices(boolean exist) {
        if(!exist) {
            errorLabel.setText("No saved devices. Load a show file, or scan for/add new devices before adding or editing cues.");
            errorLabel.setVisible(true);
            copyCueButton.setDisable(true);
            deleteCueButton.setDisable(true);
            enableMappingButtons(false);
            cueNumberTextField.setDisable(true);
            cueLabelTextField.setDisable(true);
        } else {
            errorLabel.setVisible(false);
            copyCueButton.setDisable(false);
            deleteCueButton.setDisable(false);
            enableMappingButtons(false);
            cueNumberTextField.setDisable(false);
            cueLabelTextField.setDisable(false);
        }
    }

    private boolean isCueNumberValid(String newText) {
        if(!(newText == null || newText.length() == 0)) {
            Cue temp = new Cue(Double.parseDouble(newText), cueLabelTextField.getText());
            if(model.getCueList().contains(temp)) {
                errorLabel.setText("Cue number already exists. Choose another number.");
                errorLabel.setVisible(true);
                cueNumberTextField.setStyle("-fx-text-fill: red;");
                newCueButton.setDisable(true);
                enableMappingButtons(false);
            } else {
                errorLabel.setVisible(false);
                cueNumberTextField.setStyle("-fx-text-fill: black;");
                newCueButton.setDisable(false);
                enableMappingButtons(true);
            }
        }
        return false;
    }

    private void enableMappingButtons(boolean enable) {
        if(enable) {
            addMappingButton.setDisable(false);
            if(cue.getOutputMappings().isEmpty()) {
                removeMappingButton.setDisable(true);
            } else {
                removeMappingButton.setDisable(false);
            }
        } else {
            addMappingButton.setDisable(true);
            removeMappingButton.setDisable(true);
        }
    }

    private void setCueList(Cue cue) {
        cueListTableView.getItems().clear();
        cueListTableView.getItems().addAll(model.getCueList());
        cueListTableView.sort();
        cueListTableView.refresh();
        if(cue != null) {
            cueListTableView.getSelectionModel().select(model.getCueList().indexOf(cue));
            cueListTableView.getFocusModel().focus(model.getCueList().indexOf(cue));
            setCue(cue);
        }
    }

    private void setCue(Cue cue) {
        if(cue != null) {
            this.cue = cue;
            cueNumberDisplayLabel.setText(cue.getCueNumber().toString());
            cueDescriptionDisplayLabel.setText(cue.getCueDescription());
            mappingTableView.getItems().clear();
            mappingTableView.getItems().addAll(cue.getOutputMappings());
            mappingTableView.refresh();
            enableMappingButtons(true);
        } else {
            cueNumberDisplayLabel.setText("");
            cueDescriptionDisplayLabel.setText("");
            mappingTableView.getItems().clear();
            mappingTableView.refresh();
            enableMappingButtons(false);
        }
    }

    private void clearCueEditPane() {
        cueNumberTextField.setText("");
        cueLabelTextField.setText("");
        mappingTableView.getItems().clear();
    }

    private void updateMappingTable() {
        mappingTableView.getItems().clear();
        mappingTableView.getItems().addAll(cue.getOutputMappings());
        mappingTableView.refresh();
    }

    private void confirmationDialog(String headerText, String contentText, DialogType type) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent(ButtonType -> {
            if (result.get() == javafx.scene.control.ButtonType.OK) {
                switch (type) {
                    case DELETE_CUE:
                        model.getCueList().remove(cueListTableView.getSelectionModel().getSelectedItem());
                        setCueList(null);
                        setCue(null);
                        break;
                    case DELETE_MAPPING:
                        if (mappingTableView.getSelectionModel().getSelectedItem().getClass().equals(OutputMapping.class) &&
                                mappingTableView.getSelectionModel().getSelectedItem() != null) {
                            cue.getOutputMappings().remove(mappingTableView.getSelectionModel().getSelectedItem());
                            updateMappingTable();
                            enableMappingButtons(true);
                        }
                        break;
                }
            }
        });
    }

    private void addMappingDialog(OutputMapping mapping) {
        // TODO: Validate URL field.
        Dialog<OutputMapping> mappingDialog = new Dialog<>();
        mappingDialog.setTitle("Add Output Mapping");
        mappingDialog.setHeaderText("Add an output mapping.");

        Label deviceToMapLabel = new Label("Device to Map:");
        Label deviceInputLabel = new Label("Input to Map:");
        Label destinationDeviceLabel = new Label("Destination Device:");
        Label oscLabel = new Label("Message URL Address:");
        Label algorithmLabel = new Label("Data Algorithm to Use:");

        TextField oscAddress = new TextField();

        ComboBox<RemoteDevice> devicesToMap = new ComboBox<>(model.getSenderDevices().getDevices());

        ComboBox<AnalogInput> deviceInputs = new ComboBox<>();

        ComboBox<RemoteDevice> destinationDevices = new ComboBox<>(model.getReceiverDevices().getDevices());

        ComboBox<String> algorithms = new ComboBox<>(Algorithm.getValues());

        if(mapping != null) {
            devicesToMap.getSelectionModel().select(model.getSenderDevices().getDevices().indexOf(mapping.getDevice()));
            deviceInputs.setItems(devicesToMap.getSelectionModel().getSelectedItem().getAnalogInputs());
            deviceInputs.getSelectionModel().select(mapping.getInput());
            destinationDevices.getSelectionModel().select(mapping.getOutputAddress().getRemoteDevice());
            oscAddress.setText(mapping.getOutputAddress().getUrl());
            algorithms.getSelectionModel().select(mapping.getOutputAddress().getAlgorithm().getValue());
        } else {
            devicesToMap.getSelectionModel().selectFirst();
            deviceInputs.setItems(devicesToMap.getSelectionModel().getSelectedItem().getAnalogInputs());
            deviceInputs.getSelectionModel().selectFirst();
            destinationDevices.getSelectionModel().selectFirst();
            algorithms.getSelectionModel().selectFirst();
        }

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10,10,10,10));
        pane.setVgap(5);
        pane.setHgap(5);

        pane.add(deviceToMapLabel, 0,0);
        pane.add(devicesToMap, 1, 0);
        pane.add(deviceInputLabel, 0, 1);
        pane.add(deviceInputs, 1, 1);
        pane.add(destinationDeviceLabel, 0, 2);
        pane.add(destinationDevices, 1, 2);
        pane.add(oscLabel, 0, 3);
        pane.add(oscAddress, 1, 3);
        pane.add(algorithmLabel, 0, 4);
        pane.add(algorithms, 1, 4);

        mappingDialog.getDialogPane().setContent(pane);

        mappingDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        mappingDialog.setResultConverter(dialogButton -> {
            if(dialogButton == ButtonType.OK) {
                return new OutputMapping(devicesToMap.getSelectionModel().getSelectedItem(),
                        Integer.valueOf(deviceInputs.getSelectionModel().getSelectedItem().toString()),
                        new OutputAddress(model.getReceiverDevices().getDeviceUsingMac(destinationDevices.getSelectionModel().getSelectedItem().getMacAddress()),
                                oscAddress.getText(),
                                Algorithm.get(algorithms.getSelectionModel().getSelectedItem())));
            }
            return null;
        });

        Optional<OutputMapping> result = mappingDialog.showAndWait();

        result.ifPresent(outputMapping -> {
            if(mapping != null) {
                cue.getOutputMappings().set(cue.getOutputMappings().indexOf(cue.getMappingById(mapping.getId())), outputMapping);
            } else {
                System.out.println("We are adding a new mapping.");
                cue.addOutputMapping(outputMapping);
            }
            updateMappingTable();
            enableMappingButtons(true);
        });
    }
}
