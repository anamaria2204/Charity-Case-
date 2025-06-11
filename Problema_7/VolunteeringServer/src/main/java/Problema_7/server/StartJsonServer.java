package Problema_7.server;

import Problema_7.network.utils.AbstractServer;
import Problema_7.network.utils.JsonConcurentServer;
import Problema_7.network.utils.ServerException;
import Problema_7.persistence.repository.CharityCaseRepo;
import Problema_7.persistence.repository.DonationRepo;
import Problema_7.persistence.repository.DonorRepo;
import Problema_7.persistence.repository.VolunteerRepo;
import Problema_7.persistence.repository.database.CharityCaseDBRepository;
import Problema_7.persistence.repository.database.DonationDBRepository;
import Problema_7.persistence.repository.database.DonorDBRepository;
import Problema_7.persistence.repository.database.VolunteerDBRepository;
import Problema_7.persistence.repository.hibernate.CharityCaseHibernateRepo;
import Problema_7.persistence.repository.hibernate.DonorHibernateRepo;
import Problema_7.persistence.repository.hibernate.VolunteerHibertateRepo;
import Problema_7.services.IServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StartJsonServer {
    private static final int defaultPort = 55556;
    private static final Logger logger = LogManager.getLogger(StartJsonServer.class);

    public static void main(String[] args) {
        Properties serverProps = new Properties();
    String propFileName = "/server.properties";

    try (InputStream inputStream = StartJsonServer.class.getResourceAsStream(propFileName)) {
        if (inputStream == null) {
            logger.error("Properties file not found: {}", propFileName);
            logger.debug("Current working directory: {}", new File(".").getAbsolutePath());
            return;
        }

        serverProps.load(inputStream);
        logger.info("Server properties loaded successfully");
        logger.debug("Database URL: {}", serverProps.getProperty("jdbc.url"));
        logger.debug("Database user: {}", serverProps.getProperty("jdbc.user"));
    } catch (IOException e) {
        logger.error("Error loading properties file: " + e.getMessage(), e);
        return;
    }

        // Repositories
        var charityRepo = new CharityCaseHibernateRepo();
        var donationRepo = new DonationDBRepository(serverProps);
        var volunteerRepo = new VolunteerHibertateRepo();
        var donorRepo = new DonorDBRepository(serverProps);

        IServices donationServerImpl = new DonationServicesImpl(charityRepo, donationRepo, volunteerRepo, donorRepo);

        int donationServerPort = defaultPort;
        try {
            donationServerPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException e) {
            logger.error("Invalid port number: {}", e.getMessage());
            logger.debug("Using default port {}", defaultPort);
        }

        logger.debug("Starting Donation JSON server on port: {}", donationServerPort);
        AbstractServer server = new JsonConcurentServer(donationServerPort, donationServerImpl);

        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Error starting the donation server: " + e.getMessage());
        }
    }
}
