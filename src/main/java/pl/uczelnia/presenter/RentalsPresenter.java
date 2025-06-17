package pl.uczelnia.presenter;

import pl.uczelnia.model.Rental;
import pl.uczelnia.model.managers.RentalService;
import pl.uczelnia.view.RentalsView;

import java.util.List;

public class RentalsPresenter {
    private final RentalsView view;
    private final RentalService service;

    public RentalsPresenter(RentalsView view, RentalService service) {
        this.view = view;
        this.service = service;

        init();
        loadRentals();
    }

    private void init() {
        view.getAddRentalButton().setOnAction(e -> {
            try {
//                long customerName = Long.parseLong(view.getCustomerId());
//                long gameName = Long.parseLong(view.getGameId());
//                Rental rental = new Rental();
//
//                service.addRental(rental, ); // <- zakładamy że masz taką metodę
                loadRentals();
                view.clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                view.addRentalToList("❌ Błąd: " + ex.getMessage());
            }
        });
    }

    private void loadRentals() {
        List<Rental> rentals = service.findAllRentals();
        view.getRentalsList().getItems().clear();
        for (Rental r : rentals) {
            String info = "Wypożyczenie ID " + r.getId() + " | Klient: " + r.getCustomer().getFirstName()
                    + " | Gra: " + r.getGame().getTitle();
            view.addRentalToList(info);
        }
    }
}
