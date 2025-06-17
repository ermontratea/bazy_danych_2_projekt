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

        GameService gameService = new GameService(em);
        CustomerService customerService = new CustomerService(em);
        RentalService rentalService = new RentalService(em);

        // CREATE
        Game game = new Game(
                "Terraforming Mars",
                "Jacob Fryxelius",
                "Rebel",
                1, 5,
                90, 120,
                12,
                "medium",
                "strategy",
                3,
                new BigDecimal("10")
        );
        gameService.addGame(game);

        Customer customer = new Customer(
                "Anna",
                "Nowak",
                "anna@example.com",
                LocalDate.of(1995, 3, 14)
        );
        customerService.addCustomer(customer);

        Rental rental = new Rental(
                game,
                customer,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                null,
                new BigDecimal("20")
        );
        //rentalService.addRental(rental);

//        // READ
//        Game fetchedGame = gameService.findGame(game.getId());
//        System.out.println("Fetched game: " + fetchedGame.getTitle());
//
//        // UPDATE
//        gameService.updateAvailableCopies(game.getId(), 2);
//        System.out.println("Updated available copies.");

//        // DELETE
//        rentalService.deleteRental(rental.getId());
//        System.out.println("Rental deleted.");

        em.close();
        emf.close();
    }
}
