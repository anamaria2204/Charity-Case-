package Problema_7.persistence.repository.database;

import Problema_7.model.domain.Donor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Problema_7.persistence.repository.DonorRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class DonorDBRepository implements DonorRepo {

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public DonorDBRepository(Properties props){
        logger.info("Initializing DonorDBRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    public boolean testConnection() {
        try (Connection conn = dbUtils.getConnection()) {
            return conn.isValid(2); // Testează conexiunea cu timeout de 2 secunde
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }

    @Override
    public Optional<Donor> save(Donor donor) {
    logger.traceEntry("saving task {}", donor);
    Connection conn = dbUtils.getConnection();

    // First, check if donor already exists
    try (PreparedStatement checkStmt = conn.prepareStatement(
            "SELECT id FROM donor WHERE surname = ? AND name = ? AND adress = ? AND phone_number = ?")) {
        checkStmt.setString(1, donor.getSurname());
        checkStmt.setString(2, donor.getName());
        checkStmt.setString(3, donor.getAddress());
        checkStmt.setString(4, donor.getPhoneNumber());

        ResultSet rs = checkStmt.executeQuery();
        if (rs.next()) {
            // Donor already exists
            int existingId = rs.getInt("id");
            donor.setId(existingId);
            logger.trace("Donor already exists with ID {}", existingId);
            return Optional.of(donor);
        }
    } catch (SQLException e) {
        logger.error(e);
        System.out.println("Error checking donor existence: " + e);
        return Optional.empty();
    }

    // If we get here, donor doesn't exist, so insert new one
    try (PreparedStatement prepStmt = conn.prepareStatement(
            "INSERT INTO donor (surname, name, adress, phone_number) VALUES (?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)) {
        prepStmt.setString(1, donor.getSurname());
        prepStmt.setString(2, donor.getName());
        prepStmt.setString(3, donor.getAddress());
        prepStmt.setString(4, donor.getPhoneNumber());
        int result = prepStmt.executeUpdate();
        logger.trace("Saved {} instances", result);

        try (ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                donor.setId(id);
                logger.traceExit("Saved donor with ID {}", id);
                return Optional.of(donor);
            } else {
                logger.error("Creating donor failed, no ID obtained.");
                return Optional.empty();
            }
        }
    } catch (SQLException e) {
        logger.error(e);
        System.out.println("Error DB " + e);
        return Optional.empty();
    }
}

    @Override
    public Optional findOne(Integer integer) {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        try(PreparedStatement prepStmt = conn.prepareStatement("select * from donor where id = ?")){
            prepStmt.setInt(1, integer);
            try(var result = prepStmt.executeQuery()){
                if(result.next()){
                    Integer id = result.getInt("id");
                    String surname = result.getString("surname");
                    String name = result.getString("name");
                    String address = result.getString("adress");
                    String phoneNumber = result.getString("phone_number");
                    Donor donor = new Donor(id, surname, name, address, phoneNumber);
                    donor.setId(id);
                    logger.traceExit(donor);
                    return Optional.of(donor);
                }
            }
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Iterable<Donor> findAll() {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        List<Donor> donors = new ArrayList<>();
        try(PreparedStatement prepStmt = conn.prepareStatement("select * from donor")){
            try(var result = prepStmt.executeQuery()){
                while(result.next()){
                    Integer id = result.getInt("id");
                    String surname = result.getString("surname");
                    String name = result.getString("name");
                    String address = result.getString("adress");
                    String phoneNumber = result.getString("phone_number");
                    Donor donor = new Donor(id, surname, name, address, phoneNumber);
                    donor.setId(id);
                    donors.add(donor);
                }
            }
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(donors);
        return donors;
    }

    @Override
    public List<Donor> searchDonors(String searchText) {
    List<Donor> foundDonors = new ArrayList<>();
    String searchPattern = "%" + searchText.toLowerCase() + "%";

    try (Connection conn = dbUtils.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
             "SELECT * FROM donor WHERE LOWER(name) LIKE ? OR LOWER(surname) LIKE ?")) {

        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Donor donor = new Donor(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("adress"),
                rs.getString("phone_number")
            );
            foundDonors.add(donor);
        }
    } catch (SQLException e) {
        System.out.println("Error searching donors: " + e.getMessage());
    }

    return foundDonors;
    }

}
