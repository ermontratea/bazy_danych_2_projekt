package pl.uczelnia.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import pl.uczelnia.model.managers.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-oracle");
        EntityManager em = emf.createEntityManager();
        RentalService rentalService = new RentalService(em);
        ReservationService reservationService = new ReservationService(em);

        CustomerService customerService = new CustomerService(em,rentalService,reservationService);
        GameService gameService = new GameService(em, reservationService);

        try {
            em.getTransaction().begin();
//            ////////
//            Game game = gameService.findGame(3L);
//            rentalService.createRental(8L,3L, false, reservationService);
//            rentalService.createRental(9L,3L, true, reservationService);
//            rentalService.createRental(10L,3L, false, reservationService);
//
//            rentalService.createRental(1L, 2L, false, reservationService);


            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
             em.close();
             emf.close();
        }
    }
    public static boolean hasRentedGame(EntityManager em, Customer customer, Long gameId) {
        String jpql = "SELECT COUNT(r) FROM Rental r WHERE r.customer = :customer AND r.game.id = :gameId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("customer", customer)
                .setParameter("gameId", gameId)
                .getSingleResult();
        return count > 0;
    }
}
