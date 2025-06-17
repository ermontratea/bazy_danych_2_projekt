package pl.uczelnia.model.presenter;



import pl.uczelnia.model.view.ReservationsView;

public class ReservationsPresenter {
    private final ReservationsView view;

    public ReservationsPresenter(ReservationsView view) {
        this.view = view;
        loadReservations();
    }

    private void loadReservations() {
        // Docelowo np. reservationService.findAll()
        view.setReservations("Rezerwacja 1", "Rezerwacja 2");
    }
}
