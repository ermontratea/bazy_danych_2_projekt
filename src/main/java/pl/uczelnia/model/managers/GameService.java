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

    public void addGame(Game game) {
        em.getTransaction().begin();
        em.persist(game);
        em.getTransaction().commit();
    }

    public Game findGame(Long id) {
        return em.find(Game.class, id);
    }

    public void updateGameTitle(Long gameId, String newTitle) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setTitle(newTitle);
        }
        em.getTransaction().commit();
    }

    public void updateGameAuthor(Long gameId, String newAuthor) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setAuthor(newAuthor);
        }
        em.getTransaction().commit();
    }

    public void updateGamePublisher(Long gameId, String newPublisher) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setPublisher(newPublisher);
        }
        em.getTransaction().commit();
    }

    public void updateMinPlayers(Long gameId, int newMinPlayers) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setMinPlayers(newMinPlayers);
        }
        em.getTransaction().commit();
    }

    public void updateMaxPlayers(Long gameId, int newMaxPlayers) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setMaxPlayers(newMaxPlayers);
        }
        em.getTransaction().commit();
    }

    public void updateDurationMin(Long gameId, int newDurationMin) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setMinDurationMinutes(newDurationMin);
        }
        em.getTransaction().commit();
    }

    public void updateDurationMax(Long gameId, int newDurationMax) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setMaxDurationMinutes(newDurationMax);
        }
        em.getTransaction().commit();
    }

    public void updateAgeRating(Long gameId, int newAgeRating) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setAgeRating(newAgeRating);
        }
        em.getTransaction().commit();
    }

    public void updateDifficultyLevel(Long gameId, String newDifficultyLevel) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setDifficultyLevel(newDifficultyLevel);
        }
        em.getTransaction().commit();
    }

    public void updateGameType(Long gameId, String newGameType) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setGameType(newGameType);
        }
        em.getTransaction().commit();
    }

    public void updateAvailableCopies(Long gameId, int newAvailableCopies) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setAvailableCopies(newAvailableCopies);
        }
        em.getTransaction().commit();
    }

    public void updateBasePrice(Long gameId, BigDecimal newBasePrice) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            game.setBasePrice(newBasePrice);
        }
        em.getTransaction().commit();
    }


    public void deleteGame(Long id) {
        em.getTransaction().begin();
        Game game = em.find(Game.class, id);
        if (game != null) {
            em.remove(game);
        }
        em.getTransaction().commit();
    }
}

