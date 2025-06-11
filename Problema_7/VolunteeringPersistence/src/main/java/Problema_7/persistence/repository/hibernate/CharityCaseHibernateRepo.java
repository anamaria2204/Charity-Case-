package Problema_7.persistence.repository.hibernate;

import Problema_7.model.domain.CharityCase;
import Problema_7.persistence.repository.CharityCaseRepo;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Component
public class CharityCaseHibernateRepo implements CharityCaseRepo {
   @Override
public Optional<CharityCase> update(CharityCase charityCase, Integer id) {
    try (Session session = HibernateUtils.getSessionFactory().openSession()) {
        Transaction tx = session.beginTransaction();
        CharityCase existing = session.get(CharityCase.class, id);
        if (existing != null) {
            existing.setCase_name(charityCase.getCase_name());
            existing.setTotal_amount(charityCase.getTotal_amount());
            session.merge(existing);
        }
        tx.commit();
        return findOne(id);
    } catch (Exception e) {
        e.printStackTrace();
        return Optional.empty();
    }
}


    @Override
    public Optional<CharityCase> save(CharityCase charityCase) {
         try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Integer id = (Integer) session.save(charityCase);
            tx.commit();
            return findOne(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<CharityCase> delete(Integer id) {
        Optional<CharityCase> toDelete = findOne(id);
        toDelete.ifPresent(charityCase -> {
            try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.remove(charityCase);
                tx.commit();
            }
        });
        return toDelete;
    }

    @Override
    public Optional findOne(Integer integer) {
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            return Optional.ofNullable(session.find(CharityCase.class, integer));
        }
    }

    @Override
    public Iterable<CharityCase> findAll() {
        try(Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM CharityCase", CharityCase.class).getResultList();
        }
    }
}
