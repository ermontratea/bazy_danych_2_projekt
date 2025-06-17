package pl.uczelnia.model.presenter;


import pl.uczelnia.model.view.RentalsView;

public class RentalsPresenter {
    private final RentalsView view;

    public RentalsPresenter(RentalsView view) {
        this.view = view;
        init();
    }

    private void init() {
        // Przykładowe dane + logika podpięcia zdarzeń
        view.getAddRentalButton().setOnAction(e -> {
            String customerId = view.getCustomerId();
            String gameId = view.getGameId();

            if (!customerId.isEmpty() && !gameId.isEmpty()) {
                // Tu docelowo wstaw logikę zapisu do DB
                String info = "Wypożyczono grę o ID " + gameId + " klientowi o ID " + customerId;
                view.addRentalToList(info);
                view.clearForm();
            }
        });

        view.addRentalToList("Wypożyczenie 1");
        view.addRentalToList("Wypożyczenie 2");
    }
}

