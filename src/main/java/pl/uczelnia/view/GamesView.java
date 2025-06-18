package pl.uczelnia.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import pl.uczelnia.model.Game;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GamesView extends VBox {
    private final TableView<Game> gamesTable = new TableView<>();
    private final TextField searchField = new TextField();
    private final CheckBox onlyTwoPlayersCheckBox = new CheckBox("Tylko gry dla dwóch osób");
    private final ComboBox<String> difficultyComboBox = new ComboBox<>();

    private List<Game> allGames; // Pełna lista gier do filtrowania

    public GamesView() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Lista gier:");

        // == Kolumny tabeli ==
        TableColumn<Game, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<Game, String> titleCol = new TableColumn<>("Tytuł");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Game, String> authorCol = new TableColumn<>("Autor");
        authorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));

        TableColumn<Game, String> publisherCol = new TableColumn<>("Wydawca");
        publisherCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPublisher()));

        TableColumn<Game, Integer> minPlayersCol = new TableColumn<>("Min. graczy");
        minPlayersCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMinPlayers()));

        TableColumn<Game, Integer> maxPlayersCol = new TableColumn<>("Max. graczy");
        maxPlayersCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMaxPlayers()));

        TableColumn<Game, Integer> minDurationCol = new TableColumn<>("Min. czas [min]");
        minDurationCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMinDurationMinutes()));

        TableColumn<Game, Integer> maxDurationCol = new TableColumn<>("Max. czas [min]");
        maxDurationCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMaxDurationMinutes()));

        TableColumn<Game, Integer> ageRatingCol = new TableColumn<>("Wiek");
        ageRatingCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAgeRating()));

        TableColumn<Game, String> difficultyCol = new TableColumn<>("Poziom trudności");
        difficultyCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDifficultyLevel()));

        TableColumn<Game, String> typeCol = new TableColumn<>("Typ gry");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGameType()));

        TableColumn<Game, Integer> availableCol = new TableColumn<>("Dostępne sztuki");
        availableCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAvailableCopies()));

        TableColumn<Game, Integer> totalCol = new TableColumn<>("Wszystkich sztuk");
        totalCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTotalCopies()));

        TableColumn<Game, BigDecimal> priceCol = new TableColumn<>("Bazowa cena wypożyczenia");
        priceCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getBasePrice()));

        gamesTable.getColumns().addAll(
                idCol, titleCol, authorCol, publisherCol,
                minPlayersCol, maxPlayersCol, minDurationCol, maxDurationCol,
                ageRatingCol, difficultyCol, typeCol,
                availableCol, totalCol, priceCol
        );
        gamesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // == Sekcja filtrów ==
        searchField.setPromptText("Szukaj po tytule gry...");
        difficultyComboBox.setItems(FXCollections.observableArrayList("", "Łatwy", "Średni", "Trudny"));
        difficultyComboBox.setPromptText("Poziom trudności");

        // Obsługa zmiany filtrów
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        onlyTwoPlayersCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        difficultyComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        HBox filtersBox = new HBox(10, searchField, onlyTwoPlayersCheckBox, difficultyComboBox);
        filtersBox.setPadding(new Insets(5));

        getChildren().addAll(title, filtersBox, gamesTable);
    }

    public void setGames(List<Game> games) {
        this.allGames = games;
        applyFilters();
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        boolean onlyTwoPlayers = onlyTwoPlayersCheckBox.isSelected();
        String selectedDifficulty = difficultyComboBox.getValue();

        Predicate<Game> filter = game -> {
            boolean matchesSearch = game.getTitle().toLowerCase().contains(searchText);
            boolean matchesPlayers = !onlyTwoPlayers || (game.getMinPlayers() <= 2 && game.getMaxPlayers() >= 2);
            boolean matchesDifficulty = (selectedDifficulty == null || selectedDifficulty.isEmpty())
                    || game.getDifficultyLevel().equalsIgnoreCase(selectedDifficulty);

            return matchesSearch && matchesPlayers && matchesDifficulty;
        };

        List<Game> filteredGames = allGames.stream()
                .filter(filter)
                .collect(Collectors.toList());

        gamesTable.getItems().setAll(filteredGames);
    }

    public TableView<Game> getGamesTable() {
        return gamesTable;
    }
}
