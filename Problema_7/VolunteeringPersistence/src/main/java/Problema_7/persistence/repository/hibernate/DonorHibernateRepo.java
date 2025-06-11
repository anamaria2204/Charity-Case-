package Problema_7.persistence.repository.hibernate;

import Problema_7.model.domain.Donor;
import Problema_7.persistence.repository.DonorRepo;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class DonorHibernateRepo implements DonorRepo {
    @Override
    public Optional save(Donor donor) {
        Transaction transaction = null;
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.persist(donor);
            transaction.commit();
            return Optional.of(donor);
        }
        catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Donor> searchDonors(String searchText) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            String queryStr = "FROM Donor d WHERE lower(d.name) LIKE :text " +
                              "OR lower(d.surname) LIKE :text ";
            Query<Donor> query = session.createQuery(queryStr, Donor.class);
            query.setParameter("text", "%" + searchText.toLowerCase() + "%");
            return query.getResultList();
        }
    }

    @Override
    public Optional findOne(Integer id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Donor donor = session.get(Donor.class, id);
            return Optional.ofNullable(donor);
        }
    }

    @Override
    public Iterable<Donor> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM Donor", Donor.class).getResultList();
        }
    }
}
