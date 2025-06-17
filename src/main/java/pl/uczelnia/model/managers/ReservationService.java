package pl.uczelnia.model.managers;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Game;
import pl.uczelnia.model.Rental;
import pl.uczelnia.model.Reservation;

import javax.persistence.EntityManager;
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

    public void deleteReservation(Long id) {
        em.getTransaction().begin();
        Reservation res = em.find(Reservation.class, id);
        if (res != null) {
            em.remove(res);
        }
        em.getTransaction().commit();
    }

    public List<Reservation> getReservationsForGame(Game game) {
        return em.createQuery(
                "SELECT r FROM Reservation r WHERE r.game = :game ORDER BY r.reservationDate", Reservation.class
        ).setParameter("game", game).getResultList();
    }

    public void cleanupExpiredReservations(Game game) {
        em.getTransaction().begin();

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

        em.getTransaction().commit();

        assignAvailableCopies(game);
    }

    public void assignAvailableCopies(Game game) {
        em.getTransaction().begin();

        long totalCopies = game.getTotalCopies();

        long claimed = em.createQuery(
                "SELECT COUNT(r) FROM Reservation r WHERE r.game = :game AND r.assigned = true",
                Long.class
        ).setParameter("game", game).getSingleResult();

        long availableToAssign = totalCopies - claimed;

        List<Reservation> toAssign = em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.game = :game AND r.assigned = false ORDER BY r.reservationDate",
                        Reservation.class
                ).setParameter("game", game)
                .setMaxResults((int) availableToAssign)
                .getResultList();

        for (Reservation r : toAssign) {
            r.setAssigned(true);
            r.setAvailableFrom(LocalDate.now());
        }

        int leftover = (int)(availableToAssign - toAssign.size());
        if (leftover > 0) {
            game.setAvailableCopies(game.getAvailableCopies() + leftover);
        }

        em.getTransaction().commit();
    }

    public boolean hasPendingReservations(Game game) {
        Long count = em.createQuery(
                "SELECT COUNT(r) FROM Reservation r WHERE r.game = :game",
                Long.class
        ).setParameter("game", game).getSingleResult();
        return count > 0;
    }
}
