package pl.uczelnia.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import pl.uczelnia.model.managers.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-oracle");
        EntityManager em = emf.createEntityManager();

        CustomerService customerService = new CustomerService(em);
        GameService gameService = new GameService(em);
        RentalService rentalService = new RentalService(em);
        ReservationService reservationService = new ReservationService(em);
        try {

        em.getTransaction().begin();

        // 👤 2. Dodaj klientów
        Customer anna = new Customer("Anna", "Nowak", "anna.nowak@example.com", LocalDate.of(1990, 5, 14));
        Customer jan = new Customer("Jan", "Kowalski", "jan.kowalski@example.com", LocalDate.of(1985, 8, 21));
        Customer zofia = new Customer("Zofia", "Wiśniewska", "zofia.wisniewska@example.com", LocalDate.of(1993, 11, 30));
        customerService.addCustomer(anna);
        customerService.addCustomer(jan);
        customerService.addCustomer(zofia);


        // 📦 4. Dodaj wypożyczenia
        Rental rental1 = new Rental(gameService.findByTitle("Wiedźmin: Gra planszowa"), anna,
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 8),
                LocalDate.of(2025, 6, 7),
                new BigDecimal("15"));
        Rental rental2 = new Rental(gameService.findByTitle("Dixit"), jan,
                LocalDate.of(2025, 6, 3),
                LocalDate.of(2025, 6, 10),
                null,
                new BigDecimal("5"));
        Rental rental3 = new Rental(gameService.findByTitle("Terraformacja Marsa"), zofia,
                LocalDate.of(2025, 6, 5),
                LocalDate.of(2025, 6, 12),
                null,
                new BigDecimal("10"));
        rentalService.addRental(rental1, reservationService);
        rentalService.addRental(rental2, reservationService);
        rentalService.addRental(rental3, reservationService);


        em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
            em.getTransaction().rollback(); // 💣 COFNIJ transakcję
            }
            e.printStackTrace(); // lub logger
        } finally {
             em.close();
             emf.close();
        }
    }
}
