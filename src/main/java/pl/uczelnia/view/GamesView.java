package pl.uczelnia.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pl.uczelnia.model.Game;

import java.util.List;

public class GamesView extends VBox {
    private ListView<String> gamesList = new ListView<>();

    public GamesView() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Lista gier:");
        getChildren().addAll(title, gamesList);
    }

    public void setGames(List<Game> games) {
        gamesList.getItems().clear();
        for (Game g : games) {
            gamesList.getItems().add(g.getTitle() + " (ID: " + g.getId() + ")");
        }
    }

    public ListView<String> getListView() {
        return gamesList;
    }
}
