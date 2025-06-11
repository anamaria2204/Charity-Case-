package Problema_7.fx;

import Problema_7.fx.controller.LoginController;
import Problema_7.network.jsonprotocol.DonationServicesJsonProxy;
import Problema_7.services.IServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartJsonClientFX extends Application {

    private Stage primaryStage;
    private static int defaultPort = 55556;
    private static String defaultServer = "localhost";

    private static Logger logger = LogManager.getLogger(StartJsonClientFX.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.debug("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartJsonClientFX.class.getResourceAsStream("/volunteer.properties"));
            logger.info("Client properties set {} ",clientProps);
            clientProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot find chatclient.properties " + e);
            logger.debug("Looking for chatclient.properties in folder {}",(new File(".")).getAbsolutePath());
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            logger.error("Wrong port number " + ex.getMessage());
            logger.debug("Using default port: " + defaultPort);
        }
        logger.info("Using server IP " + serverIP);
        logger.info("Using server port " + serverPort);

        IServices server = new DonationServicesJsonProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("view/login-view.fxml"));
        Parent root=loader.load();


        LoginController ctrl =
                loader.<LoginController>getController();
        ctrl.setServer(server);

        primaryStage.setTitle("Here to save the world? <3");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
