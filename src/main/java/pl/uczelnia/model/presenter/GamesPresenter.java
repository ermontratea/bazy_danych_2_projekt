package pl.uczelnia.model.presenter;


import pl.uczelnia.model.view.GamesView;

public class GamesPresenter {
    private final GamesView view;

    public GamesPresenter(GamesView view) {
        this.view = view;
        loadGames();
    }

    private void loadGames() {
        // Tu będzie np. service.findAllGames();
        view.setGames("Gra 1", "Gra 2", "Gra 3");
    }
}

