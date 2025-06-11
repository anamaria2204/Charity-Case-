package Problema_7.persistence.repository.hibernate;

import Problema_7.model.domain.CharityCase;
import Problema_7.model.domain.Donation;
import Problema_7.model.domain.Donor;
import Problema_7.model.domain.Volunteer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateUtils {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(){
        if ((sessionFactory==null)||(sessionFactory.isClosed()))
            sessionFactory=createNewSessionFactory();
        return sessionFactory;
    }

    private static  SessionFactory createNewSessionFactory(){
        try {
            var inputStream = HibernateUtils.class.getClassLoader()
                    .getResourceAsStream("hibernate.properties");

            Properties properties = new Properties();
            properties.load(inputStream);

            Configuration sessionFactory = new Configuration()
                    .addProperties(properties)
                    .addAnnotatedClass(Donor.class)
                    .addAnnotatedClass(CharityCase.class)
                    .addAnnotatedClass(Donation.class)
                    .addAnnotatedClass(Volunteer.class);
            return sessionFactory.buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SessionFactory", e);
        }
    }

    public static  void closeSessionFactory(){
        if (sessionFactory!=null)
            sessionFactory.close();
    }
}
