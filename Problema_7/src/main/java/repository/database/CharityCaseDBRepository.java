package repository.database;

import domain.CharityCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.CharityCaseRepo;
import utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class CharityCaseDBRepository implements CharityCaseRepo {

    private JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public CharityCaseDBRepository(Properties prop){
        logger.info("Initializing CharityCaseDBRepository with properties: {}", prop);
        dbUtils = new JdbcUtils(prop);
    }

    @Override
    public Optional update(CharityCase charityCase, Integer id) {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("update charity_case set total_amount = ? where id = ?")){
            preStmt.setDouble(1, charityCase.getTotal_amount());
            preStmt.setInt(2, id);
            int result = preStmt.executeUpdate();
            logger.trace("Updated {} instances", result);
        }
        catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional findOne(Integer integer) {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from charity_case where id = ?")){
            preStmt.setInt(1, integer);
            try(ResultSet result = preStmt.executeQuery()){
                if(result.next()){
                    Integer id = result.getInt("id");
                    String case_name = result.getString("case_name");
                    Double total_amount = result.getDouble("total_amount");
                    CharityCase charityCase = new CharityCase(id, case_name, total_amount);
                    charityCase.setId(id);
                    logger.traceExit(charityCase);
                    return Optional.of(charityCase);
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Iterable<CharityCase> findAll() {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        List<CharityCase> charityCases = new ArrayList<>();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from charity_case")){
            try(ResultSet result = preStmt.executeQuery()){
                while(result.next()){
                    Integer id = result.getInt("id");
                    String case_name = result.getString("case_name");
                    Double total_amount = result.getDouble("total_amount");
                    CharityCase charityCase = new CharityCase(id, case_name, total_amount);
                    charityCase.setId(id);
                    charityCases.add(charityCase);
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit(charityCases);
        return charityCases;
    }
}
