package pl.uczelnia.presenter;

import pl.uczelnia.model.Reservation;
import pl.uczelnia.model.managers.ReservationService;
import pl.uczelnia.view.ReservationsView;

import java.util.List;

public class ReservationsPresenter {
    private final ReservationsView view;
    private final ReservationService service;

    public ReservationsPresenter(ReservationsView view, ReservationService service) {
        this.view = view;
        this.service = service;
        loadReservations();

        view.getCleanupButton().setOnAction(e -> {
            service.cleanupExpiredReservationsForAllGames();
            loadReservations();
        });
    }

    private void loadReservations() {
        List<Reservation> reservations = service.findAllReservations();
        view.setReservations(reservations);
    }
}
