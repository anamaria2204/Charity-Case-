package repository.database;

import domain.CharityCase;
import domain.Donation;
import domain.Donor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.DonationRepo;
import utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

public class DonationDBRepository implements DonationRepo {

    public JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public DonationDBRepository(Properties prop){
        logger.info("Initializing DonationDBRepository with properties: {}", prop);
        dbUtils = new JdbcUtils(prop);
    }

    @Override
    public Optional save(Donation donation) {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        try(PreparedStatement prepStmt = conn.prepareStatement("insert into donation (id_donor, id_charity_case, amount, date) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)){
            prepStmt.setInt(1, donation.getDonor().getId());
            prepStmt.setInt(2, donation.getCharityCase().getId());
            prepStmt.setDouble(3, donation.getAmount());
            LocalDateTime date = donation.getDate();
            prepStmt.setTimestamp(4, java.sql.Timestamp.valueOf(date));
            int result = prepStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
            try (var generatedKeys = prepStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    donation.setId(id);
                    logger.traceExit("Saved donation with ID {}", id);
                    return Optional.of(donation);
                } else {
                    logger.error("Creating donation failed, no ID obtained.");
                    return Optional.empty();
                }
            } catch (SQLException e) {
                logger.error(e);
                System.out.println("Error DB " + e);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<Donation> findOne(Integer id) {
        logger.traceEntry("Finding donation with id: {}", id);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT d.id, d.id_donor, d.id_charity_case, d.amount, d.date, " +
                "dr.surname AS donor_surname, dr.name AS donor_name, dr.adress AS donor_address, dr.phone_number AS donor_phone, " +
                "cc.case_name, cc.total_amount " +
                "FROM donation d " +
                "JOIN donor dr ON d.id_donor = dr.id " +
                "JOIN charity_case cc ON d.id_charity_case = cc.id " +
                "WHERE d.id = ?")) {
            prepStmt.setInt(1, id);
            try (var result = prepStmt.executeQuery()) {
                if (result.next()) {
                    Integer donationId = result.getInt("id");
                    Double amount = result.getDouble("amount");
                    LocalDateTime date = result.getTimestamp("date").toLocalDateTime();

                    // Create Donor object
                    Donor donor = new Donor(
                        result.getInt("id_donor"),
                        result.getString("donor_surname"),
                        result.getString("donor_name"),
                        result.getString("donor_address"),
                        result.getString("donor_phone")
                    );

                    // Create CharityCase object
                    CharityCase charityCase = new CharityCase(
                        result.getInt("id_charity_case"),
                        result.getString("case_name"),
                        result.getDouble("total_amount")
                    );

                    Donation donation = new Donation(donationId, donor, charityCase, amount, date);
                    logger.traceExit(donation);
                    return Optional.of(donation);
                }
            }
        } catch (Exception e) {
            logger.error("Error finding donation: {}", e);
            throw new RuntimeException("Error finding donation", e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Error closing connection: {}", e);
            }
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Iterable<Donation> findAll() {
        logger.traceEntry("Finding all donations");
        Connection conn = dbUtils.getConnection();
        List<Donation> donations = new ArrayList<>();

        try (PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT d.id, d.id_donor, d.id_charity_case, d.amount, d.date, " +
                "dr.surname AS donor_surname, dr.name AS donor_name, dr.adress AS donor_address, dr.phone_number AS donor_phone, " +
                "cc.case_name, cc.total_amount " +
                "FROM donations d " +
                "JOIN donors dr ON d.id_donor = dr.id " +
                "JOIN charity_case cc ON d.id_charity_case = cc.id")) {
            try (var result = prepStmt.executeQuery()) {
                while (result.next()) {
                    Integer id = result.getInt("id");
                    Double amount = result.getDouble("amount");
                    LocalDateTime date = result.getTimestamp("date").toLocalDateTime();

                    Donor donor = new Donor(
                        result.getInt("id_donor"),
                        result.getString("donor_surname"),
                        result.getString("donor_name"),
                        result.getString("donor_address"),
                        result.getString("donor_phone")
                    );

                    // Create CharityCase object
                    CharityCase charityCase = new CharityCase(
                        result.getInt("id_charity_case"),
                        result.getString("case_name"),
                        result.getDouble("total_amount")
                    );

                    Donation donation = new Donation(id, donor, charityCase, amount, date);
                    donations.add(donation);
                }
            }
        } catch (Exception e) {
            logger.error("Error finding all donations: {}", e);
            throw new RuntimeException("Error finding all donations", e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Error closing connection: {}", e);
            }
        }

        logger.traceExit(donations);
        return donations;
    }
}
