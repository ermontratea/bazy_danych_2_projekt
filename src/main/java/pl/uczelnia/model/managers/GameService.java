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
