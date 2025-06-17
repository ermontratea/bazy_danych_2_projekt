package pl.uczelnia.model.view;


import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ReservationsView extends VBox {
    private ListView<String> reservationsList = new ListView<>();

    public ReservationsView() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Lista rezerwacji:");
        getChildren().addAll(title, reservationsList);
    }

    public void setReservations(String... reservations) {
        reservationsList.getItems().setAll(reservations);
    }
}

