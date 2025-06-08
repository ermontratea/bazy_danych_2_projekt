package pl.uczelnia.model;

import javax.persistence.*;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String publisher;

    public Game() {}

    public Game(String title, String publisher) {
        this.title = title;
        this.publisher = publisher;
    }

    // gettery i settery
}
