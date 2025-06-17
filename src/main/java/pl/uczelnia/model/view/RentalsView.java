package pl.uczelnia.model.view;


import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class RentalsView extends VBox {
    private TextField customerIdField = new TextField();
    private TextField gameIdField = new TextField();
    private Button addRentalBtn = new Button("Dodaj wypożyczenie");
    private ListView<String> rentalsList = new ListView<>();

    public RentalsView() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Wypożyczenia");
        customerIdField.setPromptText("ID klienta");
        gameIdField.setPromptText("ID gry");

        VBox form = new VBox(5, new Label("Dodaj nowe wypożyczenie:"), customerIdField, gameIdField, addRentalBtn);
        getChildren().addAll(title, form, rentalsList);
    }

    public String getCustomerId() {
        return customerIdField.getText();
    }

    public String getGameId() {
        return gameIdField.getText();
    }

    public Button getAddRentalButton() {
        return addRentalBtn;
    }

    public void addRentalToList(String rental) {
        rentalsList.getItems().add(rental);
    }

    public void clearForm() {
        customerIdField.clear();
        gameIdField.clear();
    }
}

