package pl.uczelnia.model.managers;

import pl.uczelnia.model.Rental;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RentalService {
    private EntityManager em;

    public RentalService(EntityManager em) {
        this.em = em;
    }

    public void addRental(Rental rental) {
        em.getTransaction().begin();
        em.persist(rental);
        em.getTransaction().commit();
    }

    public Rental findRental(Long id) {
        return em.find(Rental.class, id);
    }

    public void updateRentalDate(Long rentalId, LocalDate newDate) {
        em.getTransaction().begin();
        Rental rental = em.find(Rental.class, rentalId);
        if (rental != null) {
            rental.setRentalDate(newDate);
        }
        em.getTransaction().commit();
    }

    public void updateExpectedReturnDate(Long rentalId, LocalDate newDate) {
        em.getTransaction().begin();
        Rental rental = em.find(Rental.class, rentalId);
        if (rental != null) {
            rental.setExpectedReturnDate(newDate);
        }
        em.getTransaction().commit();
    }

    public void updateActualReturnDate(Long rentalId, LocalDate newDate) {
        em.getTransaction().begin();
        Rental rental = em.find(Rental.class, rentalId);
        if (rental != null) {
            rental.setActualReturnDate(newDate);
        }
        em.getTransaction().commit();
    }

    public void updatePricePaid(Long rentalId, BigDecimal newPrice) {
        em.getTransaction().begin();
        Rental rental = em.find(Rental.class, rentalId);
        if (rental != null) {
            rental.setPricePaid(newPrice);
        }
        em.getTransaction().commit();
    }

    public void deleteRental(Long id) {
        em.getTransaction().begin();
        Rental rental = em.find(Rental.class, id);
        if (rental != null) {
            em.remove(rental);
        }
        em.getTransaction().commit();
    }
}
