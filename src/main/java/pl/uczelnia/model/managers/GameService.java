package pl.uczelnia.model.managers;
import javax.persistence.*;
import pl.uczelnia.model.Game;

import java.math.BigDecimal;
import java.util.List;

public class GameService {
    private EntityManager em;

    public GameService(EntityManager em) {
        this.em = em;
    }
    public List<Game> findAllGames() {
        return em.createQuery("SELECT g FROM Game g", Game.class).getResultList();
    }
    public Game findByTitle(String title) {
        return em.createQuery(
                        "SELECT g FROM Game g WHERE g.title = :title", Game.class)
                .setParameter("title", title)
                .getSingleResult();
    }

    public void addGame(Game game) {

        em.persist(game);

    }

    public Game findGame(Long id) {
        return em.find(Game.class, id);
    }

    public void updateGameTitle(Long gameId, String newTitle) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setTitle(newTitle);
        }

    }

    public void updateGameAuthor(Long gameId, String newAuthor) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setAuthor(newAuthor);
        }

    }

    public void updateGamePublisher(Long gameId, String newPublisher) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setPublisher(newPublisher);
        }

    }

    public void updateMinPlayers(Long gameId, int newMinPlayers) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setMinPlayers(newMinPlayers);
        }

    }

    public void updateMaxPlayers(Long gameId, int newMaxPlayers) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setMaxPlayers(newMaxPlayers);
        }

    }

    public void updateDurationMin(Long gameId, int newDurationMin) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setMinDurationMinutes(newDurationMin);
        }

    }

    public void updateDurationMax(Long gameId, int newDurationMax) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setMaxDurationMinutes(newDurationMax);
        }

    }

    public void updateAgeRating(Long gameId, int newAgeRating) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setAgeRating(newAgeRating);
        }

    }

    public void updateDifficultyLevel(Long gameId, String newDifficultyLevel) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setDifficultyLevel(newDifficultyLevel);
        }

    }

    public void updateGameType(Long gameId, String newGameType) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setGameType(newGameType);
        }

    }

    public void updateAvailableCopies(Long gameId, int newAvailableCopies) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setAvailableCopies(newAvailableCopies);
        }

    }

    public void updateBasePrice(Long gameId, BigDecimal newBasePrice) {

        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setBasePrice(newBasePrice);
        }

    }


    public void deleteGame(Long id) {

        Game game = em.find(Game.class, id);
        if (game != null) {
            em.remove(game);
        }

    }
}

