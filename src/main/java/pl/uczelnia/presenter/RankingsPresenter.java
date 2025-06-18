package pl.uczelnia.presenter;

import pl.uczelnia.model.managers.RentalService;
import pl.uczelnia.view.RankingsView;

public class RankingsPresenter {
    private final RankingsView view;
    private final RentalService rentalService;

    public RankingsPresenter(RankingsView view, RentalService rentalService) {
        this.view = view;
        this.rentalService = rentalService;
        loadRankings();
    }

    private void loadRankings() {
        var topCustomers = rentalService.getTopCustomers(5);
        var topGames = rentalService.getTopGames(5);

        view.setTopCustomers(topCustomers);
        view.setTopGames(topGames);
    }
}

