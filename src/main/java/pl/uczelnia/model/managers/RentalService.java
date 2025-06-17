package pl.uczelnia.model.managers;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Game;
import pl.uczelnia.model.Rental;
import pl.uczelnia.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RentalService {
    private EntityManager em;

    public RentalService(EntityManager em) {
        this.em = em;
    }
    public List<Rental> findAllRentals() {
        return em.createQuery("SELECT r FROM Rental r", Rental.class).getResultList();
    }

    public void addRental(Rental rental, ReservationService reservationService) {
        em.getTransaction().begin();

        Game game = rental.getGame();
        Customer customer = rental.getCustomer();

        List<Reservation> reservations = reservationService.getReservationsForGame(game);

        if (game.getAvailableCopies() > 0 ) {
            game.setAvailableCopies(game.getAvailableCopies() - 1);
        } else {
            Optional<Reservation> customerReservation = reservations.stream()
                    .filter(r -> r.getCustomer().equals(customer) && r.isAssigned())
                    .findFirst();

            if (customerReservation.isEmpty()) {
                em.getTransaction().rollback();
                throw new IllegalStateException("No available copies");
            }

            Reservation res = customerReservation.get();
            if (res.getAvailableFrom().plusDays(5).isBefore(LocalDate.now())) {
                em.remove(res);
                reservationService.assignAvailableCopies(game); // przypisz komuś innemu
                em.getTransaction().rollback();
                throw new IllegalStateException("Your reservation has expired.");
            }

            em.remove(res); // wypożyczenie zakończyło rezerwację
        }

        em.persist(rental);
        em.getTransaction().commit();
    }
    public void returnGame(Long rentalId) {
        em.getTransaction().begin();
        Rental rental = em.find(Rental.class, rentalId);
        if (rental != null && rental.getActualReturnDate() == null) {
            rental.setActualReturnDate(LocalDate.now());
            Game game = rental.getGame();

            // Znajdź najstarszą aktywną rezerwację (assigned == false)
            TypedQuery<Reservation> query = em.createQuery(
                    "SELECT r FROM Reservation r WHERE r.game = :game AND r.assigned = false ORDER BY r.reservationDate ASC",
                    Reservation.class);
            query.setParameter("game", game);
            query.setMaxResults(1);

            try {
                Reservation nextReservation = query.getSingleResult();
                // Przypisz tę kopię do rezerwacji
                nextReservation.setAssigned(true);
                nextReservation.setAvailableFrom(LocalDate.now());
                em.persist(nextReservation);
            } catch (NoResultException e) {
                // Nie ma rezerwacji - zwiększamy dostępną liczbę kopii
                game.setAvailableCopies(game.getAvailableCopies() + 1);
                em.persist(game);
            }
        }
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
