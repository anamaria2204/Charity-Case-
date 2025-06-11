package Problema_7.fx.controller;

import Problema_7.model.domain.CharityCase;
import Problema_7.model.domain.Donation;
import Problema_7.services.Exceptions;
import Problema_7.services.IObserver;
import Problema_7.services.IServices;
import Problema_7.model.domain.Donor;
import Problema_7.model.domain.Volunteer;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static Problema_7.model.domain.validator.ValidatorStrategy.volunteer;

public class VolunteerController implements IObserver {

    private IServices server;
    private Stage dialogStage;
    private Volunteer volunteer;

    @FXML
    TableView<CharityCase> charity_case_table;
    @FXML
    TableColumn<CharityCase, String> charity_name_column;
    @FXML
    TableColumn<CharityCase, String> amount_column;
    @FXML
    Label case_label;
    @FXML
    TextField name_field;
    @FXML
    TextField surname_field;
    @FXML
    TextField address_field;
    @FXML
    TextField phone_field;
    @FXML
    TextField amount_field;
    @FXML
    TableView<Donor> donor_table ;
    @FXML
    TableColumn<Donor, String> name_column;
    @FXML
    TableColumn<Donor, String> surname_column;
    @FXML
    TableColumn<Donor, String> address_column;
    @FXML
    TableColumn<Donor, String> phone_column;
    @FXML
    TextField search_field;

    public VolunteerController() {
    }

    public void setService(IServices server, Stage stage, Volunteer volunteer) {
        this.server = server;
        this.dialogStage = stage;
        this.volunteer = volunteer;
        initModel();
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    @FXML
    private void initialize() {
        charity_case_table.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    case_label.setText("New donor for: " + newValue.getCase_name());
                } else {
                    case_label.setText("");
                }
            }
        );

        donor_table.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    name_field.setText(newValue.getName());
                    surname_field.setText(newValue.getSurname());
                    address_field.setText(newValue.getAddress());
                    phone_field.setText(newValue.getPhoneNumber());
                } else {
                    clear_text_fields();
                }
            }
        );
    }

    private void initModel() {
    try {
        List<CharityCase> charityCaseList = server.getCharitableCases();

        if (charity_name_column.getCellValueFactory() == null) {
            charity_name_column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCase_name()));
        }

        if (amount_column.getCellValueFactory() == null) {
            amount_column.setCellValueFactory(cellData -> {
                Double total = cellData.getValue().getTotal_amount();
                String value = (total != null) ? String.valueOf(total) : "0.0";
                return new SimpleStringProperty(value);
            });
        }

        charity_case_table.getItems().setAll(charityCaseList);

        List<Donor> donorList = server.searchDonor("");

        if (name_column.getCellValueFactory() == null) {
            name_column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        }

        if (surname_column.getCellValueFactory() == null) {
            surname_column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSurname()));
        }

        if (address_column.getCellValueFactory() == null) {
            address_column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        }

        if (phone_column.getCellValueFactory() == null) {
            phone_column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));
        }

        donor_table.getItems().setAll(donorList);

    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Initialization Error", e.getMessage(), Alert.AlertType.ERROR);
    }
}



    @FXML
    private void handleAddDonorButton() {
        try {
            if (name_field.getText().isEmpty() || surname_field.getText().isEmpty() ||
                address_field.getText().isEmpty() || phone_field.getText().isEmpty() ||
                amount_field.getText().isEmpty()) {
                showAlert("Input Error", "Please fill all fields", Alert.AlertType.WARNING);
                return;
            }
            CharityCase charityCase = charity_case_table.getSelectionModel().getSelectedItem();
            if (charityCase == null) {
                showAlert("Selection Error", "Please select a charity case", Alert.AlertType.WARNING);
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amount_field.getText());
                if (amount <= 0) {
                    showAlert("Invalid Amount", "Amount must be positive", Alert.AlertType.WARNING);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Amount", "Please enter a valid number", Alert.AlertType.WARNING);
                return;
            }

            // Create donor and donation
            Donor d = server.addDonor(
                name_field.getText().trim(),
                surname_field.getText().trim(),
                address_field.getText().trim(),
                phone_field.getText().trim(),
                    this
            );

            Donation donation = new Donation(d, charityCase, amount, LocalDateTime.now());
            server.newDonation(donation, this);

            clear_text_fields();
            showAlert("Success",
                String.format("Added donor %s %s with donation of %.2f to %s",
                    d.getName(), d.getSurname(), amount, charityCase.getCase_name()),
                Alert.AlertType.INFORMATION);

            initModel();

        } catch (Exceptions e) {
            showAlert("Error", "Failed to add donor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void handleLogoutButton(ActionEvent actionEvent) {
        try {
            server.logout(volunteer, this);
        }
        catch (Exceptions e) {
            showAlert("Logout Error", e.getMessage(), Alert.AlertType.ERROR);
        }
        Stage stage = (Stage) charity_case_table.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleSearchButton(ActionEvent actionEvent) {
        String searchTerm = search_field.getText().trim();
        try {
            List<Donor> donors = server.searchDonor(searchTerm);
            donor_table.getItems().setAll(donors);
        } catch (Exceptions e) {
            showAlert("Search Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clear_text_fields(){
        name_field.clear();
        surname_field.clear();
        address_field.clear();
        phone_field.clear();
        amount_field.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void charitableCaseUpdated(CharityCase updatedCase) throws Exceptions {
        Platform.runLater(() -> {
            for (int i = 0; i < charity_case_table.getItems().size(); i++) {
                if (charity_case_table.getItems().get(i).getId() == updatedCase.getId()) {
                    charity_case_table.getItems().set(i, updatedCase);
                    charity_case_table.refresh();
                    break;
                }
            }
        });
    }


    @Override
    public void newDonationAdded(Donation donation) throws Exceptions {
        Platform.runLater(() -> {
            for (int i = 0; i < charity_case_table.getItems().size(); i++) {
                CharityCase cc = charity_case_table.getItems().get(i);
                if (cc.getId() == donation.getCharityCase().getId()) {
                    cc.setTotal_amount(cc.getTotal_amount() + donation.getAmount());
                    charity_case_table.refresh();
                    break;
                }
            }
        });
    }


    @Override
    public void charitableCasesUpdated(List<CharityCase> updatedCases) throws Exceptions {
        Platform.runLater(() -> {
            charity_case_table.getItems().setAll(updatedCases);
        });
    }
}
