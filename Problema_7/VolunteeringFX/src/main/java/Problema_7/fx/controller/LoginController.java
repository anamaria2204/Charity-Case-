package Problema_7.fx.controller;

import Problema_7.fx.controller.MessageAlert;
import Problema_7.fx.controller.VolunteerController;
import Problema_7.model.domain.Volunteer;
import Problema_7.services.Exceptions;
import Problema_7.services.IServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField textFieldUsername_log;
    @FXML
    private TextField textFieldPassword_log;


    private IServices server;
    private VolunteerController volunteerController;
    private Volunteer volunteer;
    private Parent volunteerView;
    public LoginController() {
    }

   public void setServer(IServices server) {
        this.server = server;
    }

    public void setVolunteerController(VolunteerController volunteerController) {
        this.volunteerController = volunteerController;
    }

    public void setVolunteerView(Parent view) {
        this.volunteerView = view;
    }

    @FXML
    private void handleLoginButton(ActionEvent actionEvent) {
        String username = textFieldUsername_log.getText();
        String password = textFieldPassword_log.getText();

        if(username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please complete all fields!", Alert.AlertType.ERROR);
            return;
        }

        volunteer = new Volunteer(username, password);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/volunteer-view.fxml"));
            Parent volunteerView = loader.load();
            VolunteerController volunteerController = loader.getController();

            // Login to server
            server.login(volunteer, volunteerController);

            Stage stage = new Stage();
            stage.setTitle("Welcome, " + volunteer.getUsername() + "!");
            stage.setScene(new Scene(volunteerView));

            stage.setOnCloseRequest(event -> {
                try {
                    server.logout(volunteer, volunteerController);
                } catch (Exceptions e) {
                    showAlert("Logout Error", e.getMessage(), Alert.AlertType.ERROR);
                }
                System.exit(0);
            });

            volunteerController.setService(server, stage, volunteer);

            stage.show();
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch (Exceptions e) {
            showAlert("Login Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException e) {
            showAlert("Error", "Failed to load volunteer interface", Alert.AlertType.ERROR);
        }
    }

     private void showAlert(String title, String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
