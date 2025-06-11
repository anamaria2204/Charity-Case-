package controller;

import domain.Volunteer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import repository.database.EncryptUtils;
import service.Service_Main;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField textFieldUsername_log;
    @FXML
    private TextField textFieldPassword_log;

    private Service_Main service;
    private Stage dialogStage;
    private Volunteer volunteer;
    private static final EncryptUtils encryptUtils = new EncryptUtils();

    public LoginController() {
    }

    public void setService(Service_Main service){
        this.service = service;
    }

    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }


    @FXML
    private void initialize() {
    }

    @FXML
    private void handelLoginButton(){
        String username = textFieldUsername_log.getText();
        String password = textFieldPassword_log.getText();

        if(username.isEmpty() || password.isEmpty()){
            MessageAlert.showErrorMessage(null, "Nu ati completat toate campurile!");
            return;
        }

        Volunteer volunteer = service.findOneUsername(username);
        String pass = encryptUtils.decrypt1(volunteer.getPassword());
        if(volunteer != null && pass.equals(password)){
            showVolunteerWindow(volunteer);
            dialogStage.close();
        }
        else {
            if (volunteer == null)
                MessageAlert.showErrorMessage(null, "Voluntarul nu exista!");
            else{
                MessageAlert.showErrorMessage(null, "Parola este incorecta!");
            }
            return;
        }
    }
     private void showAlert(String title, String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showVolunteerWindow(Volunteer volunteer){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/volunteer-view.fxml"));

            AnchorPane loginLayout = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(loginLayout));

            VolunteerController volunteerController = fxmlLoader.getController();
            volunteerController.setService(service, volunteer);
            volunteerController.setDialogStage(stage);
            stage.setTitle("Hello " + volunteer.getUsername() +"! <3");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Eroare", "Eroare la deschiderea ferestrei principale", Alert.AlertType.ERROR);
        }
    }

}
