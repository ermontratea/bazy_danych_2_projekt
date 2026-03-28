package pl.uczelnia.model.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import pl.uczelnia.model.Game;
import pl.uczelnia.model.Customer;
import pl.uczelnia.model.managers.CustomerService;
import pl.uczelnia.model.managers.GameService;
import pl.uczelnia.model.managers.RentalService;
import pl.uczelnia.model.managers.ReservationService;
import pl.uczelnia.presenter.*;
import pl.uczelnia.view.*;
import java.time.LocalDate;
import java.math.BigDecimal;
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
    private VBox rankingsPane;
    private EntityManager em;
    private CustomerService customerService;
    private GameService gameService;
    private ReservationService reservationService;
    private RentalService rentalsService;


    private Button backButton;

    @Override
    public void start(Stage primaryStage) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-oracle");
        this.em = emf.createEntityManager();
        reservationService = new ReservationService(em);
        rentalsService = new RentalService(em);
        customerService = new CustomerService(em, rentalsService, reservationService);
        gameService = new GameService(em, reservationService);

        try {
            em.getTransaction().begin();
            seedData(em, customerService, gameService, rentalsService, reservationService);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        }

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
        Button rankingsBtn = new Button("Rankingi");

        rentalsBtn.setOnAction(e -> showRentalsPane());
        customersBtn.setOnAction(e -> showCustomersPane());
        gamesBtn.setOnAction(e -> showGamesPane());
        reservationsBtn.setOnAction(e -> showReservationsPane());
        rankingsBtn.setOnAction(e -> showRankingsPane());

        mainMenuPane.getChildren().addAll(rentalsBtn, customersBtn, gamesBtn, reservationsBtn, rankingsBtn);
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
        new RentalsPresenter(view, rentalsService);
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
    private void showRankingsPane() {
        RankingsView view = new RankingsView();
        new RankingsPresenter(view, rentalsService);
        root.setCenter(view);
        backButton.setVisible(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
    public static void seedData(EntityManager em, CustomerService customerService,
                                GameService gameService, RentalService rentalService,
                                ReservationService reservationService) {

        if (!customerService.findAllCustomers().isEmpty()) {
            System.out.println("W bazie już są dane, pomijam inicjalizację.");
            return;
        }

        Customer c1 = new Customer("Jan", "Kowalski", "jan.kowalski@email.com", LocalDate.of(1990, 5, 15));
        Customer c2 = new Customer("Anna", "Zaradna", "ania.z@poczta.pl", LocalDate.of(2005, 10, 20));
        Customer c3 = new Customer("Marek", "Nocny", "marek88@gmail.com", LocalDate.of(2011, 2, 10)); //

        customerService.addCustomer(c1);
        customerService.addCustomer(c2);
        customerService.addCustomer(c3);

        Game g1 = new Game("Cyberpunk 2077: Gra Karciana", "Mike Pondsmith", "R. Talsorian", 1, 4, 30, 60, 18, "Trudna", "Karciana", 5, new BigDecimal("10.00"));
        Game g2 = new Game("Wsiąść do Pociągu: Europa", "Alan R. Moon", "Days of Wonder", 2, 5, 30, 90, 8, "Łatwa", "Planszowa", 2, new BigDecimal("15.00"));
        Game g3 = new Game("Szachy Deluxe", "Nieznany", "Classic Games", 2, 2, 10, 120, 3, "Średnia", "Strategiczna", 1, new BigDecimal("15.00"));

        gameService.addGame(g1);
        gameService.addGame(g2);
        gameService.addGame(g3);

        em.flush();

        rentalService.createRental(c1.getId(), g1.getId(), false, reservationService);
        rentalService.createRental(c2.getId(), g2.getId(), true, reservationService);

        rentalService.createRental(c1.getId(), g3.getId(), false, reservationService);

        reservationService.addReservation(c3.getId(), g3.getId());

        System.out.println("Dane testowe wygenerowane pomyślnie!");
    }
    public void clearDatabase() {
        try {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM Reservation").executeUpdate();
            em.createQuery("DELETE FROM Rental").executeUpdate();

            em.createQuery("DELETE FROM Game").executeUpdate();
            em.createQuery("DELETE FROM Customer").executeUpdate();

            em.getTransaction().commit();
            System.out.println("Baza została wyczyszczona.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
