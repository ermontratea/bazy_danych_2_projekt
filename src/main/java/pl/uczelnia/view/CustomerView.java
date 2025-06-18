package pl.uczelnia.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.uczelnia.model.Customer;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CustomerView extends VBox {
    private final TableView<Customer> customerTable = new TableView<>();
    private final TextField searchField = new TextField();
    private final CheckBox adultOnlyCheckBox = new CheckBox("Tylko pełnoletni");

    private List<Customer> allCustomers;

    public CustomerView() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Lista klientów:");

        // Kolumny
        TableColumn<Customer, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<Customer, String> firstNameCol = new TableColumn<>("Imię");
        firstNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));

        TableColumn<Customer, String> lastNameCol = new TableColumn<>("Nazwisko");
        lastNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<Customer, LocalDate> birthdayCol = new TableColumn<>("Data urodzenia");
        birthdayCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getBirthday()));

        TableColumn<Customer, Integer> ageCol = new TableColumn<>("Wiek");
        ageCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAge()));

        customerTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, emailCol, birthdayCol, ageCol);
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Pole wyszukiwania
        searchField.setPromptText("Szukaj po imieniu lub nazwisku...");

        // Reakcja na zmiany w filtrach
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        adultOnlyCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        HBox filterBox = new HBox(10, searchField, adultOnlyCheckBox);
        filterBox.setPadding(new Insets(5));

        getChildren().addAll(title, filterBox, customerTable);
    }

    public void setCustomers(List<Customer> customers) {
        this.allCustomers = customers;
        applyFilters();
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        boolean adultsOnly = adultOnlyCheckBox.isSelected();

        Predicate<Customer> filter = c -> {
            boolean matchesName = c.getFirstName().toLowerCase().contains(searchText)
                    || c.getLastName().toLowerCase().contains(searchText);
            boolean matchesAge = !adultsOnly || c.getAge() >= 18;
            return matchesName && matchesAge;
        };

        List<Customer> filtered = allCustomers.stream()
                .filter(filter)
                .collect(Collectors.toList());

        customerTable.getItems().setAll(filtered);
    }

    public TableView<Customer> getCustomerTable() {
        return customerTable;
    }
}
