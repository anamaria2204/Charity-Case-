package controller;

import domain.CharityCase;
import domain.Donation;
import domain.Donor;
import domain.Volunteer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import repository.database.VolunteerDBRepository;
import service.Service_Main;

import java.util.ArrayList;
import java.util.List;

public class VolunteerController {

    private Service_Main service;
    private Stage dialogStage;
    private Volunteer volunteer;

    @FXML
    TableView<CharityCase> charity_case_table;
    @FXML
    TableColumn<CharityCase, String> charity_name_column;
    @FXML
    TableColumn<CharityCase, Double> amount_column;
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

    public void setService(Service_Main service, Volunteer volunteer){
        this.service = service;
        this.volunteer = volunteer;
        initModel();
    }

    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
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
        search_field.setOnKeyReleased(event -> {
        refreshDonorTable();
        });
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


    private void initModel(){
        List<CharityCase> charityCaseList = new ArrayList<>();
        service.getAllCharityCases().forEach(charityCaseList::add);

        charity_name_column.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCase_name())
        );

        amount_column.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(cellData.getValue().getTotal_amount()).asObject()
        );

        charity_case_table.getItems().setAll(charityCaseList);

        List<Donor> allDonors = new ArrayList<>();
        service.getAllDonors().forEach(allDonors::add);

        name_column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName())
        );

        surname_column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSurname())
        );

        address_column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAddress())
        );

        phone_column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPhoneNumber())
        );

        donor_table.getItems().setAll(allDonors);
    }

    private void refreshDonorTable(){
        String searchText = search_field.getText();
        List<Donor> donors;

    if (searchText == null || searchText.isEmpty()) {
        donors = new ArrayList<>();
        Iterable<Donor> donors_i = service.getAllDonors();
        donors_i.forEach(donors::add);

    } else {
        donors = service.searchDonors(searchText);
    }

    donor_table.getItems().clear();
    donor_table.getItems().addAll(donors);
    }

    @FXML
    private void handleAddDonorButton(){
        String name = name_field.getText();
        String surname = surname_field.getText();
        String address = address_field.getText();
        String phone = phone_field.getText();
        double amount = Double.parseDouble(amount_field.getText());

        Donor d = service.addDonor(surname, name, address, phone);
        service.updateCharityCase(charity_case_table.getSelectionModel().getSelectedItem(), amount);

        CharityCase charityCase = charity_case_table.getSelectionModel().getSelectedItem();

        service.addDonation(d.getId(), charityCase.getId(), amount);
        clear_text_fields();
        showAlert("Donor added", "Donation and donor added successfully!", Alert.AlertType.INFORMATION);
        initModel();
    }

    @FXML
    private void handleLogoutButton(){
        showAlert("Logout", "See you later!", Alert.AlertType.INFORMATION);
        dialogStage.close();
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

}
