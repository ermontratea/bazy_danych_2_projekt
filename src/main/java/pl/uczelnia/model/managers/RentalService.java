package pl.uczelnia.model.managers;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Game;
import pl.uczelnia.model.Rental;
import pl.uczelnia.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RentalService {
    private EntityManager em;

    public RentalService(EntityManager em) {
        this.em = em;
    }
    public List<Rental> findAllRentals() {
        return em.createQuery("SELECT r FROM Rental r", Rental.class).getResultList();
    }

    public void addRental(Rental rental, ReservationService reservationService) {
        Game game = em.find(Game.class, rental.getGame().getId(), LockModeType.PESSIMISTIC_WRITE);
        Customer customer = em.find(Customer.class, rental.getCustomer().getId(), LockModeType.PESSIMISTIC_WRITE);


        // Sprawdź wiek jeśli gra 18+
        if (game.getAgeRating() == 18 && customer.getAge() < 18) {
            throw new IllegalArgumentException("Nie możesz wypożyczyć gry 18+, jeśli nie masz 18 lat.");
        }

        // Sprawdź daty
        if (rental.getExpectedReturnDate() != null && rental.getExpectedReturnDate().isBefore(rental.getRentalDate())) {
            throw new IllegalArgumentException("Data oczekiwanego zwrotu nie może być wcześniejsza niż data wypożyczenia.");
        }


        // Pobierz rezerwacje klienta na tę grę
        List<Reservation> reservations = reservationService.getReservationsForGame(game);

        if (game.getAvailableCopies() > 0) {
            // Jeśli dostępne egzemplarze są, zmniejsz ilość dostępnych
            game.setAvailableCopies(game.getAvailableCopies() - 1);
        } else {
            // Jeśli brak dostępnych egzemplarzy, klient musi mieć przypisaną rezerwację
            Optional<Reservation> customerReservation = reservations.stream()
                    .filter(r -> r.getCustomer().equals(customer) && r.isAssigned())
                    .findFirst();

            if (customerReservation.isEmpty()) {
                throw new IllegalStateException("Brak dostępnych egzemplarzy oraz brak przypisanej rezerwacji.");
            }

            Reservation res = customerReservation.get();
            if (res.getAvailableFrom().plusDays(5).isBefore(LocalDate.now())) {
                em.remove(res);
                reservationService.assignAvailableCopies(game);
                throw new IllegalStateException("Twoja rezerwacja wygasła.");
            }

            // Usuwamy rezerwację, bo klient bierze grę
            em.remove(res);
        }

        // Dodajemy wypożyczenie
        em.persist(rental);


    }

    public void returnGame(Long rentalId) {
        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null && rental.getActualReturnDate() == null) {
            rental.setActualReturnDate(LocalDate.now());
            Game game = em.find(Game.class, rental.getGame().getId(), LockModeType.PESSIMISTIC_WRITE);

            // Znajdź najstarszą nieprzypisaną rezerwację
            TypedQuery<Reservation> query = em.createQuery(
                    "SELECT r FROM Reservation r WHERE r.game = :game AND r.assigned = false ORDER BY r.reservationDate ASC",
                    Reservation.class);
            query.setParameter("game", game);
            query.setMaxResults(1);

            try {
                Reservation nextReservation = query.getSingleResult();
                nextReservation.setAssigned(true);
                nextReservation.setAvailableFrom(LocalDate.now());
                em.persist(nextReservation);
            } catch (NoResultException e) {
                // Brak rezerwacji - zwiększamy dostępne egzemplarze
                game.setAvailableCopies(game.getAvailableCopies() + 1);
                em.persist(game);
            }
        }
    }

    public void deleteRental(Long id) {
        Rental rental = em.find(Rental.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            if (rental.getActualReturnDate() == null) {
                throw new IllegalStateException("Nie można usunąć wypożyczenia, które nie zostało zwrócone.");
            }

            em.remove(rental);

        }
    }


    public Rental findRental(Long id) {
        return em.find(Rental.class, id);
    }

    public void updateRentalDate(Long rentalId, LocalDate newDate) {
        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            // jeśli expectedReturnDate jest ustawione, sprawdzamy czy nowe rentalDate nie jest późniejsze
            if (rental.getExpectedReturnDate() != null && newDate.isAfter(rental.getExpectedReturnDate())) {
                throw new IllegalArgumentException("Data wypożyczenia nie może być późniejsza niż data planowanego zwrotu.");
            }
            rental.setRentalDate(newDate);
        }
    }

    public void updateExpectedReturnDate(Long rentalId, LocalDate newDate) {
        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            // expectedReturnDate >= rentalDate
            if (rental.getRentalDate() != null && newDate.isBefore(rental.getRentalDate())) {
                throw new IllegalArgumentException("Data planowanego zwrotu nie może być wcześniejsza niż data wypożyczenia.");
            }
            // jeśli actualReturnDate jest ustawione, expectedReturnDate nie może być późniejsza niż actualReturnDate
            if (rental.getActualReturnDate() != null && newDate.isAfter(rental.getActualReturnDate())) {
                throw new IllegalArgumentException("Data planowanego zwrotu nie może być późniejsza niż data faktycznego zwrotu.");
            }
            rental.setExpectedReturnDate(newDate);
        }
    }

    public void updateActualReturnDate(Long rentalId, LocalDate newDate) {
        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            // actualReturnDate >= expectedReturnDate
            if (rental.getExpectedReturnDate() != null && newDate.isBefore(rental.getExpectedReturnDate())) {
                throw new IllegalArgumentException("Data faktycznego zwrotu nie może być wcześniejsza niż data planowanego zwrotu.");
            }
            rental.setActualReturnDate(newDate);
        }
    }

    public void updatePricePaid(Long rentalId, BigDecimal newPrice) {

        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            rental.setPricePaid(newPrice);
        }

    }

    public List<Rental> findRentalsByCustomer(Customer customer) {
        return em.createQuery("SELECT r FROM Rental r WHERE r.customer = :customer", Rental.class)
                .setParameter("customer", customer)
                .getResultList();
    }

    public boolean hasRentals(Customer customer) {
        Long count = em.createQuery("SELECT COUNT(r) FROM Rental r WHERE r.customer = :customer", Long.class)
                .setParameter("customer", customer)
                .getSingleResult();
        return count > 0;
    }
    public List<GameCount> getTopGames(int limit) {
        var query = em.createQuery(
                "SELECT r.game.id, COUNT(r) " +
                        "FROM Rental r " +
                        "GROUP BY r.game.id " +
                        "ORDER BY COUNT(r) DESC", Object[].class
        ).setMaxResults(limit);

        List<Object[]> results = query.getResultList();

        List<GameCount> gameCounts = new ArrayList<>();
        for (Object[] row : results) {
            Long gameId = ((Number) row[0]).longValue();
            Long count = ((Number) row[1]).longValue();

            Game game = em.find(Game.class, gameId);
            if (game != null) {
                gameCounts.add(new GameCount(game, count));
            } else {
                System.out.println("Nie znaleziono gry o id: " + gameId);
            }
        }
        return gameCounts;
    }



    public List<CustomerCount> getTopCustomers(int limit) {
        // 1. Pobierz listę customer_id i liczby wypożyczeń
        var query = em.createQuery(
                "SELECT r.customer.id, COUNT(r) " +
                        "FROM Rental r " +
                        "GROUP BY r.customer.id " +
                        "ORDER BY COUNT(r) DESC", Object[].class
        ).setMaxResults(limit);

        List<Object[]> idsAndCounts = query.getResultList();

        // 2. Załaduj klientów i połącz z liczbą wypożyczeń
        List<CustomerCount> result = new ArrayList<>();
        for (Object[] row : idsAndCounts) {
            Long customerId = (Long) row[0];
            Long count = (Long) row[1];

            Customer customer = em.find(Customer.class, customerId); // załaduj pełny obiekt

            result.add(new CustomerCount(customer, count));
        }

        return result;
    }



    public static class CustomerCount {
        private final Customer customer;
        private final long count;

        public CustomerCount(Customer customer, long count) {
            this.customer = customer;
            this.count = count;
        }

        public Customer getCustomer() {
            return customer;
        }

        public long getCount() {
            return count;
        }
    }

    public static class GameCount {
        private final Game game;
        private final long count;

        public GameCount(Game game, long count) {
            this.game = game;
            this.count = count;
        }

        public Game getGame() {
            return game;
        }

        public long getCount() {
            return count;
        }
    }

    public boolean createRental(long customerId, long gameId, boolean isMonth) {
        try {
            Customer customer = em.find(Customer.class, customerId);
            Game game = em.find(Game.class, gameId);

            if (customer == null || game == null) {
                return false; // klient lub gra nie istnieje
            }

            BigDecimal basePrice = game.getBasePrice();
            if (basePrice == null) {
                return false; // nie ustawiono ceny bazowej
            }

            BigDecimal price = isMonth ? basePrice.multiply(BigDecimal.valueOf(2)) : basePrice;

            LocalDate rentalDate = LocalDate.now();
            LocalDate expectedReturnDate = isMonth ? rentalDate.plusDays(30) : rentalDate.plusDays(7);

            Rental rental = new Rental();
            rental.setCustomer(customer);
            rental.setGame(game);
            rental.setRentalDate(rentalDate);
            rental.setExpectedReturnDate(expectedReturnDate);
            rental.setActualReturnDate(null);
            rental.setPricePaid(price);

            em.getTransaction().begin();
            em.persist(rental);
            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

}
