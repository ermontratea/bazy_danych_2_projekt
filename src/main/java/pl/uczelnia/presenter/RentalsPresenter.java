package pl.uczelnia.presenter;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Game;
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

        loadRentals();
    }

    private void loadRentals() {
            List<Rental> rentals = service.findAllRentals();
            view.setRentals(rentals);

    }

}
