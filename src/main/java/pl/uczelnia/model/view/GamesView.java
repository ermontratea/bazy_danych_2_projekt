package pl.uczelnia.model.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class GamesView extends VBox {
    private ListView<String> gamesList = new ListView<>();

    public GamesView() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Lista gier:");
        getChildren().addAll(title, gamesList);
    }

    public void setGames(String... games) {
        gamesList.getItems().setAll(games);
    }
}
