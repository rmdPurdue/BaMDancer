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
import java.util.Optional;
import java.util.ResourceBundle;

import static util.DialogType.DELETE_CUE;
import static util.DialogType.DELETE_MAPPING;

/**
 * @author Rich Dionne
 * @project BaMDancer
 * @package mvc
 * @date 7/4/2019
 */
public class PlaybackController implements Initializable {

    @FXML public TableView<Cue> cueListTableView;
    @FXML private TableColumn<Cue, Double> cueListNumberColumn;
    @FXML private TableColumn<Cue, String> cueListLabelColumn;

    @FXML private Button newCueButton;
    @FXML private Button copyCueButton;
    @FXML private Button deleteCueButton;

    @FXML public Button goButton;
    @FXML public Button stopButton;

    @FXML private Label cueNumberDisplayLabel;
    @FXML private Label cueDescriptionDisplayLabel;
    @FXML private TextField cueNumberTextField;
    @FXML private TextField cueLabelTextField;

    private Model model;
    private Cue cue = new Cue();

    public void setModel(Model model) {
        this.model = model;
        setCueList(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
                    //
                }
            });
            return row;
        });

        cueListTableView.setPlaceholder(new Label("No cues saved."));
        cueListTableView.getSortOrder().add(cueListNumberColumn);


    }

    private boolean isCueNumberValid(String newText) {
        if(!(newText == null || newText.length() == 0)) {
            Cue temp = new Cue(Double.parseDouble(newText), cueLabelTextField.getText());
            if(model.getCueList().contains(temp)) {
                //errorLabel.setText("Cue number already exists. Choose another number.");
                //errorLabel.setVisible(true);
                cueNumberTextField.setStyle("-fx-text-fill: red;");
                newCueButton.setDisable(true);
            } else {
                //errorLabel.setVisible(false);
                cueNumberTextField.setStyle("-fx-text-fill: black;");
                newCueButton.setDisable(false);
            }
        }
        return false;
    }

    private void setCueList(Cue cue) {
        cueListTableView.getItems().clear();
        cueListTableView.getItems().addAll(model.getCueList());
        cueListTableView.sort();
        cueListTableView.refresh();
        if(cue != null) {
            cueListTableView.getSelectionModel().select(model.getCueList().indexOf(cue));
            cueListTableView.getFocusModel().focus(model.getCueList().indexOf(cue));
        }
    }

}
