package pl.uczelnia.presenter;

import pl.uczelnia.model.Game;
import pl.uczelnia.model.managers.GameService;
import pl.uczelnia.view.GamesView;

import java.util.List;

public class GamesPresenter {
    private final GamesView view;
    private final GameService service;

    public GamesPresenter(GamesView view, GameService gameService) {
        this.view = view;
        this.service = gameService;
        loadGames();
    }

    private void loadGames() {
        List<Game> games = service.findAllGames(); // <- działa, jeśli GameService ma poprawny EntityManager
        view.setGames(games);
    }
}
