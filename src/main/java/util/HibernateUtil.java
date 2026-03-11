package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static{
        try{
            // Apartir du fichier de config standard: hibernate.cfg.xml on cree la sessionFactory
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex){
            // Log l'exception pour le debogage
            System.err.println("Echec de la creation de SessionFactory."+ ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
