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

