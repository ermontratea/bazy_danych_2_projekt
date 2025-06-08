package pl.uczelnia.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import pl.uczelnia.model.Game;

public class Main {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();

        session.beginTransaction();
        Game game = new Game("Catan", "Kosmos");
        session.save(game);
        session.getTransaction().commit();

        session.close();
        factory.close();
    }
}
