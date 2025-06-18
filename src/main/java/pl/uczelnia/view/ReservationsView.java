package pl.uczelnia.view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.uczelnia.model.Reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationsView extends VBox {
    private final TableView<Reservation> reservationsTable = new TableView<>();
    private final TextField searchField = new TextField();
    private final CheckBox assignedOnlyCheckbox = new CheckBox("Tylko przydzielone");
    private final Button cleanupButton = new Button("Wyczyść wygasłe");
    private List<Reservation> allReservations = new ArrayList<>();

    public ReservationsView() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Lista rezerwacji:");

        HBox filters = new HBox(10, new Label("Szukaj:"), searchField, assignedOnlyCheckbox, cleanupButton);

        TableColumn<Reservation, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<Reservation, String> customerCol = new TableColumn<>("Klient");
        customerCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCustomer().getFirstName() + " " +
                        data.getValue().getCustomer().getLastName()));

        TableColumn<Reservation, String> gameCol = new TableColumn<>("Gra");
        gameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGame().getTitle()));

        TableColumn<Reservation, LocalDate> reservationDateCol = new TableColumn<>("Data rezerwacji");
        reservationDateCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getReservationDate()));

        TableColumn<Reservation, Boolean> assignedCol = new TableColumn<>("Przydzielono");
        assignedCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isAssigned()));

        TableColumn<Reservation, LocalDate> availableFromCol = new TableColumn<>("Dostępne od");
        availableFromCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAvailableFrom()));

        reservationsTable.getColumns().addAll(idCol, customerCol, gameCol, reservationDateCol, assignedCol, availableFromCol);
        reservationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Listenery
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterReservations());
        assignedOnlyCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> filterReservations());

        getChildren().addAll(title, filters, reservationsTable);
    }

    public void setReservations(List<Reservation> reservations) {
        this.allReservations = reservations;
        filterReservations();
    }

    public void filterReservations() {
        String search = searchField.getText().toLowerCase();
        boolean onlyAssigned = assignedOnlyCheckbox.isSelected();

        reservationsTable.getItems().clear();
        for (Reservation res : allReservations) {
            String customerName = (res.getCustomer().getFirstName() + " " + res.getCustomer().getLastName()).toLowerCase();
            String gameTitle = res.getGame().getTitle().toLowerCase();

            boolean matchesSearch = customerName.contains(search) || gameTitle.contains(search);
            boolean matchesAssigned = !onlyAssigned || res.isAssigned();

            if (matchesSearch && matchesAssigned) {
                reservationsTable.getItems().add(res);
            }
        }
    }

    public TableView<Reservation> getReservationsTable() {
        return reservationsTable;
    }

    public Button getCleanupButton() {
        return cleanupButton;
    }
}
