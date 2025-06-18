package pl.uczelnia.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Game;
import pl.uczelnia.model.managers.RentalService;

import java.util.List;

public class RankingsView extends VBox {

    private final TableView<RentalService.CustomerCount> customersTable = new TableView<>();
    private final TableView<RentalService.GameCount> gamesTable = new TableView<>();

    public RankingsView() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label customersLabel = new Label("Top 5 klientów (najwięcej wypożyczeń)");
        Label gamesLabel = new Label("Top 5 gier (najwięcej wypożyczeń)");

        // Klienci
        TableColumn<RentalService.CustomerCount, String> customerNameCol = new TableColumn<>("Klient");
        customerNameCol.setCellValueFactory(cell -> {
            Customer c = cell.getValue().getCustomer();
            return new SimpleStringProperty(c.getFirstName() + " " + c.getLastName());
        });

        TableColumn<RentalService.CustomerCount, Long> customerCountCol = new TableColumn<>("Liczba wypożyczeń");
        customerCountCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCount()));

        customersTable.getColumns().addAll(customerNameCol, customerCountCol);

        // Gry
        TableColumn<RentalService.GameCount, String> gameTitleCol = new TableColumn<>("Gra");
        gameTitleCol.setCellValueFactory(cell -> {
            Game g = cell.getValue().getGame();
            return new SimpleStringProperty(g.getTitle());
        });

        TableColumn<RentalService.GameCount, Long> gameCountCol = new TableColumn<>("Liczba wypożyczeń");
        gameCountCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCount()));

        gamesTable.getColumns().addAll(gameTitleCol, gameCountCol);

        getChildren().addAll(customersLabel, customersTable, gamesLabel, gamesTable);
    }

    public void setTopCustomers(List<RentalService.CustomerCount> customers) {
        customersTable.setItems(FXCollections.observableArrayList(customers));
    }

    public void setTopGames(List<RentalService.GameCount> games) {
        gamesTable.setItems(FXCollections.observableArrayList(games));
    }
}

