package Problema_7.persistence.repository.hibernate;

import Problema_7.model.domain.Volunteer;
import Problema_7.persistence.repository.VolunteerRepo;
import Problema_7.persistence.repository.database.EncryptUtils;
import org.hibernate.Session;

import java.util.Optional;

public class VolunteerHibertateRepo implements VolunteerRepo {

    private static final EncryptUtils encryptUtils = new EncryptUtils();

    @Override
    public void save(Volunteer volunteer) {
        String encryptedPassword = encryptUtils.encrypt1(volunteer.getPassword());
        volunteer.setPassword(encryptedPassword);

        HibernateUtils.getSessionFactory().inTransaction(session -> {
            session.persist(volunteer);
            session.flush();
        });
    }
    @Override
    public Optional<Volunteer> findByUsernameAndPassword(String username, String password) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Volunteer volunteer = session.createQuery(
                    "FROM Volunteer WHERE username = :username", Volunteer.class)
                    .setParameter("username", username)
                    .uniqueResult();
                return Optional.of(volunteer);
        }
        catch (Exception e) {
            System.out.println("Error DB " + e);
        }
        return Optional.empty();
    }

    @Override
    public Optional findOne(Integer id) {
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            return Optional.ofNullable(session.find(Volunteer.class, id));
        }
    }

    @Override
    public Iterable<Volunteer> findAll() {
        try(Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM Volunteer", Volunteer.class).getResultList();
        }
    }
}
