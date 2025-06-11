package repository.database;

import domain.Volunteer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.VolunteerRepo;
import utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class VolunteerDBRepository implements VolunteerRepo {

    private JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();
    private static final EncryptUtils encryptUtils = new EncryptUtils();

    public VolunteerDBRepository(Properties props){
        logger.info("Initializing VolunteerDBRepository with properties: {} ",props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Optional findOne(Integer integer) {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from volunteer where id = ?")){
            preStmt.setInt(1, integer);
            try(ResultSet result = preStmt.executeQuery()){
                if(result.next()){
                    Integer id = result.getInt("id");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    Volunteer volunteer = new Volunteer(id, username, password);
                    volunteer.setId(id);
                    logger.traceExit(volunteer);
                    return Optional.of(volunteer);
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
    public Iterable<Volunteer> findAll() {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        List<Volunteer> volunteers = new ArrayList<>();
        try(PreparedStatement preStmt = conn.prepareStatement("select * from volunteer")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Integer id = result.getInt("id");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    Volunteer volunteer = new Volunteer(id, username, password);
                    volunteer.setId(id);
                    volunteers.add(volunteer);
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit(volunteers);
        return volunteers;
    }

    @Override
    public void save(Volunteer volunteer) {
        logger.traceEntry("Saving volunteer {}", volunteer);
        Connection conn = dbUtils.getConnection();
        try(PreparedStatement preStmt = conn.prepareStatement("insert into volunteer (username, password) values (?, ?)")){
            preStmt.setString(1, volunteer.getUsername());
            String encryptedPassword = encryptUtils.encrypt1(volunteer.getPassword());
            preStmt.setString(2, encryptedPassword);
            int result = preStmt.executeUpdate();
            logger.traceExit("Saved {} volunteer(s)", result);

        }
        catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
    }
}
