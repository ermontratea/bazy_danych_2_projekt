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
    }

    private void loadReservations() {
        List<Reservation> reservations = service.findAllReservations();
        view.getReservationsList().getItems().clear();

        for (Reservation r : reservations) {
            String info = "Rezerwacja ID: " + r.getId() + " | Klient: " + r.getCustomer().getFirstName()
                    + " | Gra: " + r.getGame().getTitle()
                    + " | Od: " + r.getAvailableFrom();
            view.getReservationsList().getItems().add(info);
        }
    }
}
