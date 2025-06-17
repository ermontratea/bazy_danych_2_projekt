package pl.uczelnia.model.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import pl.uczelnia.model.managers.CustomerService;
import pl.uczelnia.model.managers.GameService;
import pl.uczelnia.model.managers.RentalService;
import pl.uczelnia.model.managers.ReservationService;
import pl.uczelnia.presenter.CustomerPresenter;
import pl.uczelnia.presenter.GamesPresenter;
import pl.uczelnia.presenter.RentalsPresenter;
import pl.uczelnia.presenter.ReservationsPresenter;
import pl.uczelnia.view.CustomerView;
import pl.uczelnia.view.GamesView;
import pl.uczelnia.view.RentalsView;
import pl.uczelnia.view.ReservationsView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class MainApp extends Application {

    private BorderPane root;
    private VBox mainMenuPane;
    private VBox rentalsPane;
    private VBox customersPane;
    private VBox gamesPane;
    private VBox reservationsPane;
    private EntityManager em;
    private CustomerService customerService;
    private GameService gameService;
    private ReservationService reservationService;
    private RentalService rentalsService;


    private Button backButton;

    @Override
    public void start(Stage primaryStage) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-oracle");
        EntityManager em = emf.createEntityManager();
        customerService = new CustomerService(em);
        gameService = new GameService(em);
        reservationService = new ReservationService(em);
        rentalsService = new RentalService(em);



        root = new BorderPane();

        // Tworzymy przycisk powrotu, początkowo niewidoczny
        backButton = new Button("← Wstecz do menu");
        backButton.setOnAction(e -> showMainMenu());
        backButton.setVisible(false);

        HBox topBar = new HBox(backButton);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);

        createMainMenu();

        showMainMenu();

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Panel główny");
        primaryStage.show();
    }

    private void createMainMenu() {
        mainMenuPane = new VBox(10);
        mainMenuPane.setPadding(new Insets(20));

        Button rentalsBtn = new Button("Wypożyczenia");
        Button customersBtn = new Button("Klienci");
        Button gamesBtn = new Button("Gry");
        Button reservationsBtn = new Button("Rezerwacje");

        rentalsBtn.setOnAction(e -> showRentalsPane());
        customersBtn.setOnAction(e -> showCustomersPane());
        gamesBtn.setOnAction(e -> showGamesPane());
        reservationsBtn.setOnAction(e -> showReservationsPane());

        mainMenuPane.getChildren().addAll(rentalsBtn, customersBtn, gamesBtn, reservationsBtn);
    }


    private void showMainMenu() {
        root.setCenter(mainMenuPane);
        backButton.setVisible(false);
    }

    private void showCustomersPane() {
        CustomerView view = new CustomerView();
        new CustomerPresenter(view, customerService);
        root.setCenter(view);
        backButton.setVisible(true);
    }

    private void showRentalsPane() {
        RentalsView view = new RentalsView();
        new RentalsPresenter(view, rentalsService); // tutaj można podać też service'y
        root.setCenter(view);
        backButton.setVisible(true);
    }

    private void showGamesPane() {
        GamesView view = new GamesView();
        new GamesPresenter(view, gameService);
        root.setCenter(view);
        backButton.setVisible(true);
    }

    private void showReservationsPane() {
        ReservationsView view = new ReservationsView();
        new ReservationsPresenter(view, reservationService);
        root.setCenter(view);
        backButton.setVisible(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
