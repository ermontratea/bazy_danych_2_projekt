package pl.uczelnia.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import pl.uczelnia.model.Customer;

import java.util.List;

public class CustomerView extends VBox {
    private ListView<String> listView = new ListView<>();

    public CustomerView() {
        setSpacing(10);
        setPadding(new Insets(20));
        getChildren().addAll(new Label("Lista klientów:"), listView);
    }

    public void setCustomers(List<Customer> customers) {
        listView.getItems().clear();
        for (Customer c : customers) {
            listView.getItems().add(c.getFirstName() + " " + c.getLastName());
        }
    }

    public ListView<String> getListView() {
        return listView;
    }
}

