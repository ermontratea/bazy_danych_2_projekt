package pl.uczelnia.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.uczelnia.model.Rental;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RentalsView extends VBox {

    private TableView<Rental> rentalsTable = new TableView<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final TextField searchField = new TextField();
    private final CheckBox unreturnedCheckBox = new CheckBox("Tylko nieoddane");
    private final CheckBox overdueCheckBox = new CheckBox("Tylko spóźnione");


    private List<Rental> allRentals;

    public RentalsView() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Wypożyczenia");

        // Kolumny
        TableColumn<Rental, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

        TableColumn<Rental, String> customerNameCol = new TableColumn<>("Klient");
        customerNameCol.setCellValueFactory(cellData -> {
            var c = cellData.getValue().getCustomer();
            return new SimpleStringProperty(c.getFirstName() + " " + c.getLastName());
        });

        TableColumn<Rental, Long> customerIdCol = new TableColumn<>("ID klienta");
        customerIdCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCustomer().getId()));

        TableColumn<Rental, String> gameTitleCol = new TableColumn<>("Gra");
        gameTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getTitle()));

        TableColumn<Rental, Long> gameIdCol = new TableColumn<>("ID gry");
        gameIdCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getGame().getId()));

        TableColumn<Rental, String> rentalDateCol = new TableColumn<>("Data rozpoczęcia");
        rentalDateCol.setCellValueFactory(cellData -> {
            var date = cellData.getValue().getRentalDate();
            return new SimpleStringProperty(date != null ? date.format(formatter) : "");
        });

        TableColumn<Rental, String> expectedReturnDateCol = new TableColumn<>("Data zwrotu oczekiwana");
        expectedReturnDateCol.setCellValueFactory(cellData -> {
            var date = cellData.getValue().getExpectedReturnDate();
            return new SimpleStringProperty(date != null ? date.format(formatter) : "");
        });

        TableColumn<Rental, String> actualReturnDateCol = new TableColumn<>("Data zwrotu faktyczna");
        actualReturnDateCol.setCellValueFactory(cellData -> {
            var date = cellData.getValue().getActualReturnDate();
            return new SimpleStringProperty(date != null ? date.format(formatter) : "");
        });

        TableColumn<Rental, String> pricePaidCol = new TableColumn<>("Cena zapłacona");
        pricePaidCol.setCellValueFactory(cellData -> {
            var price = cellData.getValue().getPricePaid();
            return new SimpleStringProperty(price != null ? price.toString() : "");
        });

        rentalsTable.getColumns().addAll(
                idCol, customerNameCol, customerIdCol, gameTitleCol, gameIdCol,
                rentalDateCol, expectedReturnDateCol, actualReturnDateCol, pricePaidCol
        );

        // Wyszukiwarka + checkboxy
        searchField.setPromptText("Szukaj klienta...");

        searchField.textProperty().addListener((obs, old, val) -> applyFilters());
        unreturnedCheckBox.selectedProperty().addListener((obs, old, val) -> applyFilters());
        overdueCheckBox.selectedProperty().addListener((obs, old, val) -> applyFilters());

        HBox filters = new HBox(10, searchField, unreturnedCheckBox, overdueCheckBox);
        filters.setPadding(new Insets(5, 0, 5, 0));

        getChildren().addAll(title, filters, rentalsTable);
    }

    public void setRentals(List<Rental> rentals) {
        this.allRentals = rentals;
        applyFilters();
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        boolean onlyUnreturned = unreturnedCheckBox.isSelected();
        boolean onlyOverdue = overdueCheckBox.isSelected();

        LocalDate now = LocalDate.now();

        Predicate<Rental> filter = rental -> {
            String fullName = rental.getCustomer().getFirstName().toLowerCase() + " " +
                    rental.getCustomer().getLastName().toLowerCase();
            boolean nameMatch = fullName.contains(searchText);

            boolean isUnreturned = rental.getActualReturnDate() == null;
            boolean isOverdue = isUnreturned &&
                    rental.getExpectedReturnDate() != null &&
                    rental.getExpectedReturnDate().isBefore(now);

            boolean unreturnedMatch = !onlyUnreturned || isUnreturned;
            boolean overdueMatch = !onlyOverdue || isOverdue;

            return nameMatch && unreturnedMatch && overdueMatch;
        };

        var filtered = allRentals.stream()
                .filter(filter)
                .collect(Collectors.toList());

        rentalsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    public TableView<Rental> getRentalsTable() {
        return rentalsTable;
    }
}
