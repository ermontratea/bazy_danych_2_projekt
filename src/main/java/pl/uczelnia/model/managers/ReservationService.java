package pl.uczelnia.model.managers;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Game;
import pl.uczelnia.model.Rental;
import pl.uczelnia.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    private EntityManager em;

    public ReservationService(EntityManager em) {
        this.em = em;
    }
    public List<Reservation> findAllReservations() {
        return em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
    }
    public void addReservation(Long customerID, Long gameID) {
        Game game = em.find(Game.class, gameID, LockModeType.PESSIMISTIC_WRITE);
        Customer customer = em.find(Customer.class, customerID, LockModeType.PESSIMISTIC_WRITE);

        // 1. Sprawdzenie wieku klienta, jeśli gra 18+
        if (game.getAgeRating() == 18 &&
                (customer.getBirthday() == null ||
                        customer.getBirthday().plusYears(18).isAfter(LocalDate.now()))) {
            throw new IllegalArgumentException("Klient musi mieć ukończone 18 lat, aby zarezerwować tę grę.");
        }

        // 2. Sprawdzenie czy klient nie ma już rezerwacji na tę grę
        Long countSameReservation = em.createQuery(
                        "SELECT COUNT(r) FROM Reservation r WHERE r.customer = :customer AND r.game = :game",
                        Long.class)
                .setParameter("customer", customer)
                .setParameter("game", game)
                .getSingleResult();
        if (countSameReservation > 0) {
            throw new IllegalArgumentException("Klient już ma rezerwację tej gry.");
        }

        // 3. Sprawdzenie liczby rezerwacji klienta
        Long countReservations = em.createQuery(
                        "SELECT COUNT(r) FROM Reservation r WHERE r.customer = :customer",
                        Long.class)
                .setParameter("customer", customer)
                .getSingleResult();
        if (countReservations >= 5) {
            throw new IllegalArgumentException("Klient może mieć maksymalnie 5 rezerwacji.");
        }

        // 4. Sprawdzenie czy są dostępne egzemplarze (availableCopies > 0)
        // dostępne kopie to total - rented - assigned reservations
//        Long rentedCount = em.createQuery(
//                        "SELECT COUNT(r) FROM Rental r WHERE r.game = :game AND r.actualReturnDate IS NULL",
//                        Long.class)
//                .setParameter("game", game)
//                .getSingleResult();
//
//        Long assignedReservationsCount = em.createQuery(
//                        "SELECT COUNT(r) FROM Reservation r WHERE r.game = :game AND r.assigned = true",
//                        Long.class)
//                .setParameter("game", game)
//                .getSingleResult();

//        int available = game.getTotalCopies() - rentedCount.intValue() - assignedReservationsCount.intValue();
        int available= game.getAvailableCopies();
        if (available > 0) {
            throw new IllegalArgumentException("Gra jest dostępna, nie można rezerwować.");
        }

        // 5. Dodanie rezerwacji
        Reservation reservation = new Reservation(customer, game, LocalDate.now());
        em.persist(reservation);


    }


    public void deleteReservation(Long id) {
        Reservation res = em.find(Reservation.class, id, LockModeType.PESSIMISTIC_WRITE);
        Game game = em.find(Game.class, res.getGame().getId(), LockModeType.PESSIMISTIC_WRITE);
        Customer customer = em.find(Customer.class, res.getCustomer().getId(), LockModeType.PESSIMISTIC_WRITE);

        if (res != null) {
            em.remove(res);
        }
    }

    public List<Reservation> getReservationsForGame(Game game) {
        return em.createQuery(
                "SELECT r FROM Reservation r WHERE r.game = :game ORDER BY r.reservationDate", Reservation.class
        ).setParameter("game", game).getResultList();
    }

    public void cleanupExpiredReservations(Game game) {
        game = em.find(Game.class, game.getId(), LockModeType.PESSIMISTIC_WRITE);


        LocalDate limit = LocalDate.now().minusDays(5);

        List<Reservation> expired = em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.game = :game AND r.assigned = true AND r.availableFrom < :limit",
                        Reservation.class
                ).setParameter("game", game)
                .setParameter("limit", limit)
                .getResultList();

        for (Reservation r : expired) {
            em.remove(r);
        }

        assignAvailableCopies(game);
    }
    public void cleanupExpiredReservationsForAllGames() {
        List<Game> allGames = em.createQuery("SELECT g FROM Game g", Game.class).getResultList();
        for (Game game : allGames) {
            cleanupExpiredReservations(game);
        }
    }

    public void assignAvailableCopies(Game game) {
        game = em.find(Game.class, game.getId(), LockModeType.PESSIMISTIC_WRITE);
        cleanupExpiredReservations(game);
        // 1. Ile egzemplarzy jest wypożyczonych?
        long rented = em.createQuery(
                "SELECT COUNT(r) FROM Rental r WHERE r.game = :game AND r.actualReturnDate IS NULL",
                Long.class
        ).setParameter("game", game).getSingleResult();

        // 2. Ile rezerwacji jest już przypisanych?
        long assignedReservations = em.createQuery(
                "SELECT COUNT(r) FROM Reservation r WHERE r.game = :game AND r.assigned = true",
                Long.class
        ).setParameter("game", game).getSingleResult();

        // 3. Dostępne do przypisania = total - wypożyczone - już przypisane
        long toAssign = game.getTotalCopies() - rented - assignedReservations;
        if (toAssign <= 0) {
            game.setAvailableCopies(0);
            return;
        }

        // 4. Pobierz osoby z rezerwacją, ale jeszcze nieprzypisaną
        List<Reservation> toAssignList = em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.game = :game AND r.assigned = false ORDER BY r.reservationDate",
                        Reservation.class
                ).setParameter("game", game)
                .setMaxResults((int) toAssign)
                .getResultList();

        // 5. Przypisz im gry
        for (Reservation res : toAssignList) {
            em.lock(res, LockModeType.PESSIMISTIC_WRITE);
            res.setAssigned(true);
            res.setAvailableFrom(LocalDate.now());
        }

        // 6. Zostało coś? To dopiero dodaj do dostępnych
        int remaining = (int)(toAssign - toAssignList.size());
        game.setAvailableCopies(remaining);
    }


    public boolean hasPendingReservations(Game game) {
        Long count = em.createQuery(
                "SELECT COUNT(r) FROM Reservation r WHERE r.game = :game",
                Long.class
        ).setParameter("game", game).getSingleResult();
        return count > 0;
    }
    public List<Reservation> getReservationsByCustomer(Customer customer) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.customer = :customer", Reservation.class)
                .setParameter("customer", customer)
                .getResultList();
    }

    public boolean hasReservations(Customer customer) {
        Long count = em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.customer = :customer", Long.class)
                .setParameter("customer", customer)
                .getSingleResult();
        return count > 0;
    }


}
