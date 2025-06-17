package pl.uczelnia.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
                String gameType, int availableCopies, BigDecimal basePrice) {
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
        this.availableCopies = availableCopies;
        this.basePrice = basePrice;
        this.totalCopies = availableCopies;
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
