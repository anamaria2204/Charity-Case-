package Problema_7.persistence.repository.database;

import Problema_7.model.domain.CharityCase;
import Problema_7.persistence.repository.CharityCaseRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Component
@Primary
public class CharityCaseDBRepository implements CharityCaseRepo {

    private final JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public CharityCaseDBRepository(@Qualifier("props") Properties prop){        logger.info("Initializing CharityCaseDBRepository with properties: {}", prop);
        this.dbUtils = new JdbcUtils(prop);
    }

    @Override
    public Optional<CharityCase> update(CharityCase charityCase, Integer id) {
        logger.traceEntry();
        String sql = "UPDATE charity_case SET case_name = ?, total_amount = ? WHERE id = ?";
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement preStmt = conn.prepareStatement(sql)) {

            preStmt.setString(1, charityCase.getCase_name());
            preStmt.setDouble(2, charityCase.getTotal_amount());
            preStmt.setInt(3, id);

            int rowsUpdated = preStmt.executeUpdate();
            logger.trace("Updated {} rows", rowsUpdated);

            if (rowsUpdated > 0) {
                return findOne(id);
            } else {
                return Optional.empty();
            }

        } catch (SQLException ex) {
            logger.error("Error updating CharityCase", ex);
            return Optional.empty();
        } finally {
            logger.traceExit();
        }
    }

    @Override
    public Optional<CharityCase> save(CharityCase charityCase) {
        logger.traceEntry();
        String sql = "INSERT INTO charity_case (case_name, total_amount) VALUES (?, ?)";
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement preStmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preStmt.setString(1, charityCase.getCase_name());
            preStmt.setDouble(2, charityCase.getTotal_amount());

            int affectedRows = preStmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Insert failed, no rows affected.");
                return Optional.empty();
            }

            try (ResultSet generatedKeys = preStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Integer newId = generatedKeys.getInt(1);
                    charityCase.setId(newId);
                    return Optional.of(charityCase);
                } else {
                    logger.warn("Insert succeeded but no ID obtained.");
                    return Optional.empty();
                }
            }

        } catch (SQLException ex) {
            logger.error("Error saving CharityCase", ex);
            return Optional.empty();
        } finally {
            logger.traceExit();
        }
    }

    @Override
    public Optional<CharityCase> delete(Integer id) {
        logger.traceEntry();
        String sql = "DELETE FROM charity_case WHERE id = ?";
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement preStmt = conn.prepareStatement(sql)) {

            preStmt.setInt(1, id);
            int rowsDeleted = preStmt.executeUpdate();
            logger.trace("Deleted {} rows", rowsDeleted);

            if (rowsDeleted > 0) {
                return Optional.of(new CharityCase(id, null, null)); // or return deleted entity if you want
            } else {
                return Optional.empty();
            }

        } catch (SQLException ex) {
            logger.error("Error deleting CharityCase", ex);
            return Optional.empty();
        } finally {
            logger.traceExit();
        }
    }

    @Override
    public Optional<CharityCase> findOne(Integer id) {
        logger.traceEntry();
        String sql = "SELECT * FROM charity_case WHERE id = ?";
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement preStmt = conn.prepareStatement(sql)) {

            preStmt.setInt(1, id);
            try (ResultSet rs = preStmt.executeQuery()) {
                if (rs.next()) {
                    CharityCase charityCase = new CharityCase(
                            rs.getInt("id"),
                            rs.getString("case_name"),
                            rs.getDouble("total_amount")
                    );
                    logger.traceExit(charityCase);
                    return Optional.of(charityCase);
                }
            }
        } catch (SQLException ex) {
            logger.error("Error finding CharityCase", ex);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Iterable<CharityCase> findAll() {
        logger.traceEntry();
        String sql = "SELECT * FROM charity_case";
        List<CharityCase> charityCases = new ArrayList<>();
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement preStmt = conn.prepareStatement(sql);
             ResultSet rs = preStmt.executeQuery()) {

            while (rs.next()) {
                CharityCase charityCase = new CharityCase(
                        rs.getInt("id"),
                        rs.getString("case_name"),
                        rs.getDouble("total_amount")
                );
                charityCases.add(charityCase);
            }

        } catch (SQLException ex) {
            logger.error("Error retrieving all CharityCases", ex);
        }
        logger.traceExit(charityCases);
        return charityCases;
    }
}
