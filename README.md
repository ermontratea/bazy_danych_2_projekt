# BoardGame Rental System 

A desktop application for managing a board game rental business. The system handles customers, game inventory, rentals, and a reservation system with automated copy assignment.

## Key Features
* **Rental Management:** Full lifecycle of a rental (borrowing, returning, automatic price calculation).
* **Smart Reservations:** Automatic assignment of available copies to the next person in line when a game is returned.
* **Validation Logic:** Age restrictions (18+ games), reservation limits (max 5 per customer), and date consistency checks.
* **Rankings:** Top 5 most rented games and most active customers.
* **Concurrency Control:** Implementation of **Pessimistic Locking** to handle simultaneous database access.

## Technologies
* **Java 21**
* **JavaFX** (UI Stack)
* **Hibernate / JPA** (ORM)
* **H2 Database** (Embedded SQL)
* **Maven** (Dependency Management)

## 📸 Preview
![Main Menu]()

## Setup & Installation

### Prerequisites
* JDK 17 or higher
* Maven

### Running the application
1. Clone the repository:
   ```bash
   git clone https://github.com/ermontratea/bazy_danych_2_projekt

### Running the application
1. Clone the repository:
   ```bash
   git clone https://github.com/ermontratea/bazy_danych_2_projekt
2. **Open in IntelliJ:** File -> Open -> Select the project folder.
3. **Configure SDK:** Ensure your Project SDK is set to **Java 17 or 21** (File -> Project Structure).
4. **Run the Application:**
   * Find `src/main/java/pl/uczelnia/model/app/LauchApp.java`.
   * Right-click the `LaunchApp` class and select **Run 'LaunchApp.main()'**.
Note: The application automatically initializes an H2 database file (mojabaza.mv.db) and seeds it with sample data on the first launch.

## Project Structure
- pl.uczelnia.model.managers - Business logic (Services).
- pl.uczelnia.presenter - MVP (Model-View-Presenter) Pattern implementation.
- pl.uczelnia.view - JavaFX UI components.
- pl.uczelnia.model - JPA Entities (Game, Customer, Rental, Reservation).


# Raport projektu - Internetowa Wypożyczalnia Gier Planszowych

## Autorzy
- Tymon Bobiński
- Mariusz Krause

---

## Opis projektu

Projekt przedstawia uproszczony system internetowej wypożyczalni gier planszowych, którego głównym celem jest zarządzanie danymi związanymi z klientami, grami, rezerwacjami oraz wypożyczeniami. Aplikacja napisana w Javie z wykorzystaniem Hibernate do komunikacji z bazą danych Oracle.

---

## Struktura bazy danych – tabele i kolumny

### Tabela `Customer`
| Kolumna | Typ |
|---------|-----|
| id | Long |
| firstName | String |
| lastName | String |
| email | String |
| birthday | LocalDate |

### Tabela `Reservation`
| Kolumna | Typ |
|---------|-----|
| id | Long |
| customer | Customer |
| game | Game |
| reservationDate | LocalDate |
| availableFrom | LocalDate |

### Tabela `Game`
| Kolumna | Typ |
|---------|-----|
| id | Long |
| title | String |
| author | String |
| publisher | String |
| minPlayers | int |
| maxPlayers | int |
| minDurationMinutes | int |
| maxDurationMinutes | int |
| ageRating | int |
| difficultyLevel | String |
| gameType | String |
| availableCopies | int |
| totalCopies | int |
| basePrice | BigDecimal |

### Tabela `Rental`
| Kolumna | Typ |
|---------|-----|
| id | Long |
| game | Game |
| customer | Customer |
| rentalDate | LocalDate |
| expectedReturnDate | LocalDate |
| actualReturnDate | LocalDate |
| pricePaid | BigDecimal |



---

## Struktura projektu i pliki źródłowe

### Pliki prezentera
Obsługuje logikę i pośrednictwo między widokiem a warstwą modelu

#### ReservationsPresenter.java
```java
package pl.uczelnia.presenter;

import pl.uczelnia.model.Reservation;
import pl.uczelnia.model.managers.ReservationService;
import pl.uczelnia.view.ReservationsView;

import java.util.List;

public class ReservationsPresenter {
    private final ReservationsView view;
    private final ReservationService service;

    public ReservationsPresenter(ReservationsView view, ReservationService service) {
        this.view = view;
        this.service = service;
        loadReservations();

        view.getCleanupButton().setOnAction(e -> {
            service.cleanupExpiredReservationsForAllGames();
            loadReservations();
        });
    }

    private void loadReservations() {
        List<Reservation> reservations = service.findAllReservations();
        view.setReservations(reservations);
    }
}

```

#### RentalsPresenter.java
```java
package pl.uczelnia.presenter;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Game;
import pl.uczelnia.model.Rental;
import pl.uczelnia.model.managers.RentalService;
import pl.uczelnia.view.RentalsView;

import java.util.List;

public class RentalsPresenter {
    private final RentalsView view;
    private final RentalService service;


    public RentalsPresenter(RentalsView view, RentalService service) {
        this.view = view;
        this.service = service;

        loadRentals();
    }

    private void loadRentals() {
            List<Rental> rentals = service.findAllRentals();
            view.setRentals(rentals);

    }

}

```

#### GamesPresenter.java
```java
package pl.uczelnia.presenter;

import pl.uczelnia.model.Game;
import pl.uczelnia.model.managers.GameService;
import pl.uczelnia.view.GamesView;

import java.util.List;

public class GamesPresenter {
    private final GamesView view;
    private final GameService service;

    public GamesPresenter(GamesView view, GameService gameService) {
        this.view = view;
        this.service = gameService;
        loadGames();
    }

    private void loadGames() {
        List<Game> games = service.findAllGames(); // <- działa, jeśli GameService ma poprawny EntityManager
        view.setGames(games);
    }
}

```

#### RankingsPresenter.java
```java
package pl.uczelnia.presenter;

import pl.uczelnia.model.managers.RentalService;
import pl.uczelnia.view.RankingsView;

public class RankingsPresenter {
    private final RankingsView view;
    private final RentalService rentalService;

    public RankingsPresenter(RankingsView view, RentalService rentalService) {
        this.view = view;
        this.rentalService = rentalService;
        loadRankings();
    }

    private void loadRankings() {
        var topCustomers = rentalService.getTopCustomers(5);
        var topGames = rentalService.getTopGames(5);

        view.setTopCustomers(topCustomers);
        view.setTopGames(topGames);
    }
}


```

#### CustomerPresenter.java
```java
package pl.uczelnia.presenter;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.managers.CustomerService;
import pl.uczelnia.view.CustomerView;

import java.util.List;

public class CustomerPresenter {
    private final CustomerView view;
    private final CustomerService service;

    public CustomerPresenter(CustomerView view, CustomerService service) {
        this.view = view;
        this.service = service;
        loadCustomers();
    }

    private void loadCustomers() {
        List<Customer> customers = service.findAllCustomers();
        view.setCustomers(customers);
    }
}


```

### Pliki modelu danych i logiki
Definicja klasy domenowej w systemie

#### Customer.java
```java
package pl.uczelnia.model;
import javax.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthday;

    // === Konstruktor bezargumentowy (dla Hibernate) ===
    public Customer() {
    }

    // === Konstruktor ze wszystkimi polami (oprócz ID) ===
    public Customer(String firstName, String lastName, String email, LocalDate birthday) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthday;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }


    public int getAge() {
        if (birthday == null) {
            return 0; // lub inna wartość domyślna / rzucenie wyjątku, jeśli brak daty
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}
```

#### Reservation.java
```java
package pl.uczelnia.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Game game;

    private LocalDate reservationDate;

    private boolean assigned = false;

    private LocalDate availableFrom;

    public Reservation() {}

    public Reservation(Customer customer, Game game, LocalDate reservationDate) {
        this.customer = customer;
        this.game = game;
        this.reservationDate = reservationDate;
    }

    // Gettery i settery

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Game getGame() {
        return game;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }


}


```

#### Game.java
```java
package pl.uczelnia.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity

public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String title;
    private String author;
    private String publisher;

    private int minPlayers;
    private int maxPlayers;

    private int minDurationMinutes;
    private int maxDurationMinutes;

    private int ageRating;

    private String difficultyLevel;
    private String gameType;

    private int availableCopies;
    private int totalCopies;

    private BigDecimal basePrice;

    // === Konstruktor bezargumentowy (wymagany przez Hibernate) ===
    public Game() {
    }

    // === Konstruktor ze wszystkimi polami (poza ID, które generuje się automatycznie) ===
    public Game(String title, String author, String publisher, int minPlayers, int maxPlayers,
                int minDurationMinutes, int maxDurationMinutes, int ageRating, String difficultyLevel,
                String gameType, int totalCopies, BigDecimal basePrice) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.minDurationMinutes = minDurationMinutes;
        this.maxDurationMinutes = maxDurationMinutes;
        this.ageRating = ageRating;
        this.difficultyLevel = difficultyLevel;
        this.gameType = gameType;
        this.availableCopies = totalCopies;
        this.basePrice = basePrice;
        this.totalCopies = totalCopies;
    }
    

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public int getMinPlayers() {
        return minPlayers;
    }
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    public int getMinDurationMinutes() {
        return minDurationMinutes;
    }
    public void setMinDurationMinutes(int minDurationMinutes) {
        this.minDurationMinutes = minDurationMinutes;
    }
    public int getMaxDurationMinutes() {
        return maxDurationMinutes;
    }
    public void setMaxDurationMinutes(int maxDurationMinutes) {
        this.maxDurationMinutes = maxDurationMinutes;
    }
    public int getAgeRating() {
        return ageRating;
    }
    public void setAgeRating(int ageRating) {
        this.ageRating = ageRating;
    }
    public String getDifficultyLevel() {
        return difficultyLevel;
    }
    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    public String getGameType() {
        return gameType;
    }
    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
    public int getAvailableCopies() {
        return availableCopies;
    }
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

}

```

#### Main.java
```java
package pl.uczelnia.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import pl.uczelnia.model.managers.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-oracle");
        EntityManager em = emf.createEntityManager();
        RentalService rentalService = new RentalService(em);
        ReservationService reservationService = new ReservationService(em);

        CustomerService customerService = new CustomerService(em,rentalService,reservationService);
        GameService gameService = new GameService(em, reservationService);

        try {
            em.getTransaction().begin();
            rentalService.returnGame(7L);







        em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
            em.getTransaction().rollback(); // 💣 COFNIJ transakcję
            }
            e.printStackTrace(); // lub logger
        } finally {
             em.close();
             emf.close();
        }
    }
    public static boolean hasRentedGame(EntityManager em, Customer customer, Long gameId) {
        String jpql = "SELECT COUNT(r) FROM Rental r WHERE r.customer = :customer AND r.game.id = :gameId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("customer", customer)
                .setParameter("gameId", gameId)
                .getSingleResult();
        return count > 0;
    }
}

```

#### Rental.java
```java
package pl.uczelnia.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Game game;

    @ManyToOne
    private Customer customer;

    private LocalDate rentalDate;

    private LocalDate expectedReturnDate;

    private LocalDate actualReturnDate;

    private BigDecimal pricePaid;

    // === Konstruktor bezargumentowy (dla Hibernate) ===
    public Rental() {
    }

    // === Konstruktor ze wszystkimi polami (oprócz ID) ===
    public Rental(Game game, Customer customer, LocalDate rentalDate,
                  LocalDate expectedReturnDate, BigDecimal pricePaid) {
        this.game = game;
        this.customer = customer;
        this.rentalDate = rentalDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = null;
        this.pricePaid = pricePaid;
    }

    public Long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public BigDecimal getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(BigDecimal pricePaid) {
        this.pricePaid = pricePaid;
    }
}
```

### Pliki logiki biznesowej (service/manager)
Operacje CRUD i logika biznesowa na danych

#### RentalService.java
```java
package pl.uczelnia.model.managers;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Game;
import pl.uczelnia.model.Rental;
import pl.uczelnia.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RentalService {
    private EntityManager em;

    public RentalService(EntityManager em) {
        this.em = em;
    }
    public List<Rental> findAllRentals() {
        return em.createQuery("SELECT r FROM Rental r", Rental.class).getResultList();
    }

    public void addRental(Rental rental, ReservationService reservationService) {
        Game game = em.find(Game.class, rental.getGame().getId(), LockModeType.PESSIMISTIC_WRITE);
        Customer customer = em.find(Customer.class, rental.getCustomer().getId(), LockModeType.PESSIMISTIC_WRITE);


        // Sprawdź wiek jeśli gra 18+
        if (game.getAgeRating() == 18 && customer.getAge() < 18) {
            throw new IllegalArgumentException("Nie możesz wypożyczyć gry 18+, jeśli nie masz 18 lat.");
        }

        // Sprawdź daty
        if (rental.getExpectedReturnDate() != null && rental.getExpectedReturnDate().isBefore(rental.getRentalDate())) {
            throw new IllegalArgumentException("Data oczekiwanego zwrotu nie może być wcześniejsza niż data wypożyczenia.");
        }


        // Pobierz rezerwacje klienta na tę grę
        List<Reservation> reservations = reservationService.getReservationsForGame(game);

        if (game.getAvailableCopies() > 0) {
            // Jeśli dostępne egzemplarze są, zmniejsz ilość dostępnych
            game.setAvailableCopies(game.getAvailableCopies() - 1);
        } else {
            // Jeśli brak dostępnych egzemplarzy, klient musi mieć przypisaną rezerwację
            Optional<Reservation> customerReservation = reservations.stream()
                    .filter(r -> r.getCustomer().equals(customer) && r.isAssigned())
                    .findFirst();

            if (customerReservation.isEmpty()) {
                throw new IllegalStateException("Brak dostępnych egzemplarzy oraz brak przypisanej rezerwacji.");
            }

            Reservation res = customerReservation.get();
            if (res.getAvailableFrom().plusDays(5).isBefore(LocalDate.now())) {
                em.remove(res);
                reservationService.assignAvailableCopies(game);
                throw new IllegalStateException("Twoja rezerwacja wygasła.");
            }

            // Usuwamy rezerwację, bo klient bierze grę
            em.remove(res);
        }

        // Dodajemy wypożyczenie
        em.persist(rental);


    }

    public void returnGame(Long rentalId) {
        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null && rental.getActualReturnDate() == null) {
            rental.setActualReturnDate(LocalDate.now());
            Game game = em.find(Game.class, rental.getGame().getId(), LockModeType.PESSIMISTIC_WRITE);

            // Znajdź najstarszą nieprzypisaną rezerwację
            TypedQuery<Reservation> query = em.createQuery(
                    "SELECT r FROM Reservation r WHERE r.game = :game AND r.assigned = false ORDER BY r.reservationDate ASC",
                    Reservation.class);
            query.setParameter("game", game);
            query.setMaxResults(1);

            try {
                Reservation nextReservation = query.getSingleResult();
                nextReservation.setAssigned(true);
                nextReservation.setAvailableFrom(LocalDate.now());
                em.persist(nextReservation);
            } catch (NoResultException e) {
                // Brak rezerwacji - zwiększamy dostępne egzemplarze
                game.setAvailableCopies(game.getAvailableCopies() + 1);
                em.persist(game);
            }
        }
    }

    public void deleteRental(Long id) {
        Rental rental = em.find(Rental.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            if (rental.getActualReturnDate() == null) {
                throw new IllegalStateException("Nie można usunąć wypożyczenia, które nie zostało zwrócone.");
            }

            em.remove(rental);

        }
    }


    public Rental findRental(Long id) {
        return em.find(Rental.class, id);
    }

    public void updateRentalDate(Long rentalId, LocalDate newDate) {
        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            // jeśli expectedReturnDate jest ustawione, sprawdzamy czy nowe rentalDate nie jest późniejsze
            if (rental.getExpectedReturnDate() != null && newDate.isAfter(rental.getExpectedReturnDate())) {
                throw new IllegalArgumentException("Data wypożyczenia nie może być późniejsza niż data planowanego zwrotu.");
            }
            rental.setRentalDate(newDate);
        }
    }

    public void updateExpectedReturnDate(Long rentalId, LocalDate newDate) {
        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            // expectedReturnDate >= rentalDate
            if (rental.getRentalDate() != null && newDate.isBefore(rental.getRentalDate())) {
                throw new IllegalArgumentException("Data planowanego zwrotu nie może być wcześniejsza niż data wypożyczenia.");
            }
            // jeśli actualReturnDate jest ustawione, expectedReturnDate nie może być późniejsza niż actualReturnDate
            if (rental.getActualReturnDate() != null && newDate.isAfter(rental.getActualReturnDate())) {
                throw new IllegalArgumentException("Data planowanego zwrotu nie może być późniejsza niż data faktycznego zwrotu.");
            }
            rental.setExpectedReturnDate(newDate);
        }
    }

    public void updateActualReturnDate(Long rentalId, LocalDate newDate) {
        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            // actualReturnDate >= expectedReturnDate
            if (rental.getExpectedReturnDate() != null && newDate.isBefore(rental.getExpectedReturnDate())) {
                throw new IllegalArgumentException("Data faktycznego zwrotu nie może być wcześniejsza niż data planowanego zwrotu.");
            }
            rental.setActualReturnDate(newDate);
        }
    }

    public void updatePricePaid(Long rentalId, BigDecimal newPrice) {

        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            rental.setPricePaid(newPrice);
        }

    }

    public List<Rental> findRentalsByCustomer(Customer customer) {
        return em.createQuery("SELECT r FROM Rental r WHERE r.customer = :customer", Rental.class)
                .setParameter("customer", customer)
                .getResultList();
    }

    public boolean hasRentals(Customer customer) {
        Long count = em.createQuery("SELECT COUNT(r) FROM Rental r WHERE r.customer = :customer", Long.class)
                .setParameter("customer", customer)
                .getSingleResult();
        return count > 0;
    }
    public List<GameCount> getTopGames(int limit) {
        var query = em.createQuery(
                "SELECT r.game.id, COUNT(r) " +
                        "FROM Rental r " +
                        "GROUP BY r.game.id " +
                        "ORDER BY COUNT(r) DESC", Object[].class
        ).setMaxResults(limit);

        List<Object[]> results = query.getResultList();

        List<GameCount> gameCounts = new ArrayList<>();
        for (Object[] row : results) {
            Long gameId = ((Number) row[0]).longValue();
            Long count = ((Number) row[1]).longValue();

            Game game = em.find(Game.class, gameId);
            if (game != null) {
                gameCounts.add(new GameCount(game, count));
            } else {
                System.out.println("Nie znaleziono gry o id: " + gameId);
            }
        }
        return gameCounts;
    }



    public List<CustomerCount> getTopCustomers(int limit) {
        // 1. Pobierz listę customer_id i liczby wypożyczeń
        var query = em.createQuery(
                "SELECT r.customer.id, COUNT(r) " +
                        "FROM Rental r " +
                        "GROUP BY r.customer.id " +
                        "ORDER BY COUNT(r) DESC", Object[].class
        ).setMaxResults(limit);

        List<Object[]> idsAndCounts = query.getResultList();

        // 2. Załaduj klientów i połącz z liczbą wypożyczeń
        List<CustomerCount> result = new ArrayList<>();
        for (Object[] row : idsAndCounts) {
            Long customerId = (Long) row[0];
            Long count = (Long) row[1];

            Customer customer = em.find(Customer.class, customerId); // załaduj pełny obiekt

            result.add(new CustomerCount(customer, count));
        }

        return result;
    }



    public static class CustomerCount {
        private final Customer customer;
        private final long count;

        public CustomerCount(Customer customer, long count) {
            this.customer = customer;
            this.count = count;
        }

        public Customer getCustomer() {
            return customer;
        }

        public long getCount() {
            return count;
        }
    }

    public static class GameCount {
        private final Game game;
        private final long count;

        public GameCount(Game game, long count) {
            this.game = game;
            this.count = count;
        }

        public Game getGame() {
            return game;
        }

        public long getCount() {
            return count;
        }
    }

    public boolean createRental(long customerId, long gameId, boolean isMonth) {
        try {
            Customer customer = em.find(Customer.class, customerId);
            Game game = em.find(Game.class, gameId);

            if (customer == null || game == null) {
                return false; // klient lub gra nie istnieje
            }

            BigDecimal basePrice = game.getBasePrice();
            if (basePrice == null) {
                return false; // nie ustawiono ceny bazowej
            }

            BigDecimal price = isMonth ? basePrice.multiply(BigDecimal.valueOf(2)) : basePrice;

            LocalDate rentalDate = LocalDate.now();
            LocalDate expectedReturnDate = isMonth ? rentalDate.plusDays(30) : rentalDate.plusDays(7);

            Rental rental = new Rental();
            rental.setCustomer(customer);
            rental.setGame(game);
            rental.setRentalDate(rentalDate);
            rental.setExpectedReturnDate(expectedReturnDate);
            rental.setActualReturnDate(null);
            rental.setPricePaid(price);

            em.getTransaction().begin();
            em.persist(rental);
            em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

}

```

#### ReservationService.java
```java
package pl.uczelnia.model.managers;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Game;
import pl.uczelnia.model.Rental;
import pl.uczelnia.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    private EntityManager em;

    public ReservationService(EntityManager em) {
        this.em = em;
    }
    public List<Reservation> findAllReservations() {
        return em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
    }
    public void addReservation(Long customerID, Long gameID) {
        Game game = em.find(Game.class, gameID, LockModeType.PESSIMISTIC_WRITE);
        Customer customer = em.find(Customer.class, customerID, LockModeType.PESSIMISTIC_WRITE);

        // 1. Sprawdzenie wieku klienta, jeśli gra 18+
        if (game.getAgeRating() == 18 &&
                (customer.getBirthday() == null ||
                        customer.getBirthday().plusYears(18).isAfter(LocalDate.now()))) {
            throw new IllegalArgumentException("Klient musi mieć ukończone 18 lat, aby zarezerwować tę grę.");
        }

        // 2. Sprawdzenie czy klient nie ma już rezerwacji na tę grę
        Long countSameReservation = em.createQuery(
                        "SELECT COUNT(r) FROM Reservation r WHERE r.customer = :customer AND r.game = :game",
                        Long.class)
                .setParameter("customer", customer)
                .setParameter("game", game)
                .getSingleResult();
        if (countSameReservation > 0) {
            throw new IllegalArgumentException("Klient już ma rezerwację tej gry.");
        }

        // 3. Sprawdzenie liczby rezerwacji klienta
        Long countReservations = em.createQuery(
                        "SELECT COUNT(r) FROM Reservation r WHERE r.customer = :customer",
                        Long.class)
                .setParameter("customer", customer)
                .getSingleResult();
        if (countReservations >= 5) {
            throw new IllegalArgumentException("Klient może mieć maksymalnie 5 rezerwacji.");
        }

        // 4. Sprawdzenie czy są dostępne egzemplarze (availableCopies > 0)
        // dostępne kopie to total - rented - assigned reservations
        Long rentedCount = em.createQuery(
                        "SELECT COUNT(r) FROM Rental r WHERE r.game = :game AND r.actualReturnDate IS NULL",
                        Long.class)
                .setParameter("game", game)
                .getSingleResult();

        Long assignedReservationsCount = em.createQuery(
                        "SELECT COUNT(r) FROM Reservation r WHERE r.game = :game AND r.assigned = true",
                        Long.class)
                .setParameter("game", game)
                .getSingleResult();

//        int available = game.getTotalCopies() - rentedCount.intValue() - assignedReservationsCount.intValue();
        int available= game.getAvailableCopies();
        if (available > 0) {
            throw new IllegalArgumentException("Gra jest dostępna, nie można rezerwować.");
        }

        // 5. Dodanie rezerwacji
        Reservation reservation = new Reservation(customer, game, LocalDate.now());
        em.persist(reservation);


    }


    public void deleteReservation(Long id) {
        Reservation res = em.find(Reservation.class, id, LockModeType.PESSIMISTIC_WRITE);
        Game game = em.find(Game.class, res.getGame().getId(), LockModeType.PESSIMISTIC_WRITE);
        Customer customer = em.find(Customer.class, res.getCustomer().getId(), LockModeType.PESSIMISTIC_WRITE);

        if (res != null) {
            em.remove(res);
        }
    }

    public List<Reservation> getReservationsForGame(Game game) {
        return em.createQuery(
                "SELECT r FROM Reservation r WHERE r.game = :game ORDER BY r.reservationDate", Reservation.class
        ).setParameter("game", game).getResultList();
    }

    public void cleanupExpiredReservations(Game game) {
        game = em.find(Game.class, game.getId(), LockModeType.PESSIMISTIC_WRITE);


        LocalDate limit = LocalDate.now().minusDays(5);

        List<Reservation> expired = em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.game = :game AND r.assigned = true AND r.availableFrom < :limit",
                        Reservation.class
                ).setParameter("game", game)
                .setParameter("limit", limit)
                .getResultList();

        for (Reservation r : expired) {
            em.remove(r);
        }

        assignAvailableCopies(game);
    }
    public void cleanupExpiredReservationsForAllGames() {
        List<Game> allGames = em.createQuery("SELECT g FROM Game g", Game.class).getResultList();
        for (Game game : allGames) {
            cleanupExpiredReservations(game);
        }
    }

    public void assignAvailableCopies(Game game) {
        game = em.find(Game.class, game.getId(), LockModeType.PESSIMISTIC_WRITE);
        cleanupExpiredReservations(game);
        // 1. Ile egzemplarzy jest wypożyczonych?
        long rented = em.createQuery(
                "SELECT COUNT(r) FROM Rental r WHERE r.game = :game AND r.actualReturnDate IS NULL",
                Long.class
        ).setParameter("game", game).getSingleResult();

        // 2. Ile rezerwacji jest już przypisanych?
        long assignedReservations = em.createQuery(
                "SELECT COUNT(r) FROM Reservation r WHERE r.game = :game AND r.assigned = true",
                Long.class
        ).setParameter("game", game).getSingleResult();

        // 3. Dostępne do przypisania = total - wypożyczone - już przypisane
        long toAssign = game.getTotalCopies() - rented - assignedReservations;
        if (toAssign <= 0) {
            game.setAvailableCopies(0);
            return;
        }

        // 4. Pobierz osoby z rezerwacją, ale jeszcze nieprzypisaną
        List<Reservation> toAssignList = em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.game = :game AND r.assigned = false ORDER BY r.reservationDate",
                        Reservation.class
                ).setParameter("game", game)
                .setMaxResults((int) toAssign)
                .getResultList();

        // 5. Przypisz im gry
        for (Reservation res : toAssignList) {
            em.lock(res, LockModeType.PESSIMISTIC_WRITE);
            res.setAssigned(true);
            res.setAvailableFrom(LocalDate.now());
        }

        // 6. Zostało coś? To dopiero dodaj do dostępnych
        int remaining = (int)(toAssign - toAssignList.size());
        game.setAvailableCopies(remaining);
    }


    public boolean hasPendingReservations(Game game) {
        Long count = em.createQuery(
                "SELECT COUNT(r) FROM Reservation r WHERE r.game = :game",
                Long.class
        ).setParameter("game", game).getSingleResult();
        return count > 0;
    }
    public List<Reservation> getReservationsByCustomer(Customer customer) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.customer = :customer", Reservation.class)
                .setParameter("customer", customer)
                .getResultList();
    }

    public boolean hasReservations(Customer customer) {
        Long count = em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.customer = :customer", Long.class)
                .setParameter("customer", customer)
                .getSingleResult();
        return count > 0;
    }


}

```

#### CustomerService.java
```java
package pl.uczelnia.model.managers;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Rental;
import pl.uczelnia.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;

public class CustomerService {
    private EntityManager em;
    private final RentalService rentalService;
    private final ReservationService reservationService;

    public CustomerService(EntityManager em,RentalService rentalService, ReservationService reservationService) {
        this.em = em;
        this.rentalService = rentalService;
        this.reservationService = reservationService;
    }
    public List<Customer> findAllCustomers() {
        return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }

    public void addCustomer(Customer customer) {
        if (!isAtLeast13YearsOld(customer.getBirthday())) {
            throw new IllegalArgumentException("Klient musi mieć co najmniej 13 lat.");
        }
        em.persist(customer);
    }


    public Customer findCustomer(Long id) {
        return em.find(Customer.class, id);
    }

    public void updateFirstName(Long customerId, String newFirstName) {

        Customer customer = em.find(Customer.class, customerId, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            customer.setFirstName(newFirstName);
        }

    }

    public void updateLastName(Long customerId, String newLastName) {

        Customer customer = em.find(Customer.class, customerId, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            customer.setLastName(newLastName);
        }

    }

    public void updateEmail(Long customerId, String newEmail) {

        Customer customer = em.find(Customer.class, customerId, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            customer.setEmail(newEmail);
        }

    }

    public void updateBirthday(Long customerId, LocalDate newBirthday) {
        if (!isAtLeast13YearsOld(newBirthday)) {
            throw new IllegalArgumentException("Klient musi mieć co najmniej 13 lat.");
        }

        Customer customer = em.find(Customer.class, customerId, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            customer.setBirthday(newBirthday);
        }
    }

    public void deleteCustomerWithRelations(Long id) {
        Customer customer = em.find(Customer.class, id, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            // Usuń wszystkie wypożyczenia klienta używając RentalService
            List<Rental> rentals = rentalService.findRentalsByCustomer(customer);
            for (Rental rental : rentals) {
                rentalService.deleteRental(rental.getId());
            }

            // Usuń wszystkie rezerwacje klienta używając ReservationService
            List<Reservation> reservations = reservationService.getReservationsByCustomer(customer);
            for (Reservation reservation : reservations) {
                reservationService.deleteReservation(reservation.getId());
            }

            // Usuń klienta
            em.remove(customer);
        }
    }

    public boolean deleteCustomerIfNoRelations(Long id) {
        Customer customer = em.find(Customer.class, id, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            boolean hasRentals = rentalService.hasRentals(customer);
            boolean hasReservations = reservationService.hasReservations(customer);

            if (!hasRentals && !hasReservations) {
                em.remove(customer);
                return true;
            }
        }
        return false;
    }
    private boolean isAtLeast13YearsOld(LocalDate birthday) {
        return birthday != null && birthday.plusYears(13).isBefore(LocalDate.now());
    }
}

```

#### GameService.java
```java
package pl.uczelnia.model.managers;

import pl.uczelnia.model.Game;
import pl.uczelnia.model.Rental;
import pl.uczelnia.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;

public class GameService {

    private EntityManager em;
    private ReservationService reservationService;

    public GameService(EntityManager em, ReservationService reservationService) {
        this.em = em;
        this.reservationService = reservationService;
    }

    public List<Game> findAllGames() {
        return em.createQuery("SELECT g FROM Game g", Game.class).getResultList();
    }

    public Game findByTitle(String title) {
        return em.createQuery("SELECT g FROM Game g WHERE g.title = :title", Game.class)
                .setParameter("title", title)
                .getSingleResult();
    }

    public Game findGame(Long id) {
        return em.find(Game.class, id);
    }

    public void addGame(Game game) {
        validateGameData(game);
        if (game.getTotalCopies() < 1) {
            throw new IllegalArgumentException("Gra musi mieć co najmniej 1 egzemplarz.");
        }
        em.persist(game);
    }

    public void updateGameTitle(Long gameId, String newTitle) {
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null) {
            game.setTitle(newTitle);
        }
    }

    public void updateGameAuthor(Long gameId, String newAuthor) {
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null) {
            game.setAuthor(newAuthor);
        }
    }

    public void updateGamePublisher(Long gameId, String newPublisher) {
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null) {
            game.setPublisher(newPublisher);
        }
    }

    public void updateMinPlayers(Long gameId, int newMinPlayers) {
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null && newMinPlayers <= game.getMaxPlayers()) {
            game.setMinPlayers(newMinPlayers);
        } else {
            throw new IllegalArgumentException("minPlayers nie może być większe niż maxPlayers.");
        }
    }

    public void updateMaxPlayers(Long gameId, int newMaxPlayers) {
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null && newMaxPlayers >= game.getMinPlayers()) {
            game.setMaxPlayers(newMaxPlayers);
        } else {
            throw new IllegalArgumentException("maxPlayers nie może być mniejsze niż minPlayers.");
        }
    }

    public void updateDurationMin(Long gameId, int newDurationMin) {
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null && newDurationMin <= game.getMaxDurationMinutes()) {
            game.setMinDurationMinutes(newDurationMin);
        } else {
            throw new IllegalArgumentException("Minimalny czas nie może być większy niż maksymalny.");
        }
    }

    public void updateDurationMax(Long gameId, int newDurationMax) {
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null && newDurationMax >= game.getMinDurationMinutes()) {
            game.setMaxDurationMinutes(newDurationMax);
        } else {
            throw new IllegalArgumentException("Maksymalny czas nie może być mniejszy niż minimalny.");
        }
    }

    public void updateAgeRating(Long gameId, int newAgeRating) {
        if (newAgeRating < 0 || newAgeRating > 18) {
            throw new IllegalArgumentException("Ocena wiekowa musi być między 0 a 18.");
        }
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null) {
            game.setAgeRating(newAgeRating);
        }
    }

    public void updateDifficultyLevel(Long gameId, String newDifficultyLevel) {
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null) {
            game.setDifficultyLevel(newDifficultyLevel);
        }
    }

    public void updateGameType(Long gameId, String newGameType) {
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null) {
            game.setGameType(newGameType);
        }
    }

    public void updateBasePrice(Long gameId, BigDecimal newBasePrice) {
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setBasePrice(newBasePrice);
        }
    }

    public void updateTotalCopies(Long gameId, int newTotalCopies) {
        Game game = em.find(Game.class, gameId, LockModeType.PESSIMISTIC_WRITE);

        if (game != null) {
            int oldTotal = game.getTotalCopies();
            int diff = newTotalCopies - oldTotal;

            if (diff > 0) {
                game.setTotalCopies(newTotalCopies);

                // Odpalamy logikę rezerwacji – przypisuje chętnych i ewentualnie zwiększa availableCopies
                reservationService.assignAvailableCopies(game);

            } else {
                int available = game.getAvailableCopies();
                if (available + diff < 0) {
                    throw new IllegalArgumentException("Nie można zmniejszyć totalCopies, bo za mało dostępnych egzemplarzy.");
                }
                game.setTotalCopies(newTotalCopies);
                game.setAvailableCopies(available + diff);
            }
        }
    }

    public void deleteGame(Long id) {
        Game game = em.find(Game.class, id, LockModeType.PESSIMISTIC_WRITE);

        if (game != null) {
            // Sprawdź, czy gra ma powiązane wypożyczenia
            Long rentalsCount = em.createQuery(
                            "SELECT COUNT(r) FROM Rental r WHERE r.game = :game", Long.class)
                    .setParameter("game", game)
                    .getSingleResult();

            // Sprawdź, czy gra ma powiązane rezerwacje
            Long reservationsCount = em.createQuery(
                            "SELECT COUNT(r) FROM Reservation r WHERE r.game = :game", Long.class)
                    .setParameter("game", game)
                    .getSingleResult();

            if (rentalsCount > 0 || reservationsCount > 0) {
                throw new IllegalStateException("Nie można usunąć gry, która ma przypisane wypożyczenia lub rezerwacje.");
            }

            em.remove(game);
        }
    }


    private void validateGameData(Game game) {
        if (game.getMinPlayers() > game.getMaxPlayers()) {
            throw new IllegalArgumentException("minPlayers nie może być większe niż maxPlayers.");
        }
        if (game.getMinDurationMinutes() > game.getMaxDurationMinutes()) {
            throw new IllegalArgumentException("Minimalny czas gry nie może być większy niż maksymalny.");
        }
        if (game.getAgeRating() < 0 || game.getAgeRating() > 18) {
            throw new IllegalArgumentException("Ocena wiekowa musi być między 0 a 18.");
        }
    }
}

```

### Główna aplikacja
Klasa główna uruchamiająca aplikację

#### Main.java
```java
package pl.uczelnia.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import pl.uczelnia.model.managers.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-oracle");
        EntityManager em = emf.createEntityManager();
        RentalService rentalService = new RentalService(em);
        ReservationService reservationService = new ReservationService(em);

        CustomerService customerService = new CustomerService(em,rentalService,reservationService);
        GameService gameService = new GameService(em, reservationService);

        try {
            em.getTransaction().begin();
            rentalService.returnGame(7L);







        em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
            em.getTransaction().rollback(); // 💣 COFNIJ transakcję
            }
            e.printStackTrace(); // lub logger
        } finally {
             em.close();
             emf.close();
        }
    }
    public static boolean hasRentedGame(EntityManager em, Customer customer, Long gameId) {
        String jpql = "SELECT COUNT(r) FROM Rental r WHERE r.customer = :customer AND r.game.id = :gameId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("customer", customer)
                .setParameter("gameId", gameId)
                .getSingleResult();
        return count > 0;
    }
}

```

#### MainApp.java
```java
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
import pl.uczelnia.presenter.*;
import pl.uczelnia.view.*;

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
        EntityManager em = emf.createEntityManager();
        reservationService = new ReservationService(em);
        rentalsService = new RentalService(em);
        customerService = new CustomerService(em, rentalsService, reservationService);
        gameService = new GameService(em, reservationService);




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
    private void showRankingsPane() {
        RankingsView view = new RankingsView();
        new RankingsPresenter(view, rentalsService);
        root.setCenter(view);
        backButton.setVisible(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

```

### Pliki widoku
Interfejs graficzny użytkownika (JavaFX)

#### RankingsView.java
```java
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


```

#### RentalsView.java
```java
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

```

#### GamesView.java
```java
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

```

#### ReservationsView.java
```java
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

```

#### CustomerView.java
```java
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

```

---

## Podsumowanie i wnioski

Projekt ten pokazuje jak zbudować prostą, lecz funkcjonalną aplikację do zarządzania wypożyczalnią gier planszowych, używając Javy i Hibernate. Praktyczne doświadczenie w zakresie modelowania danych, relacji między encjami, oraz integracji z bazą danych Oracle okazało się bardzo wartościowe. Aplikacja może stanowić punkt wyjścia do rozbudowy o interfejs webowy czy bardziej zaawansowaną logikę biznesową.
