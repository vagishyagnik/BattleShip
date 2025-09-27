import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.phonepe.models.Player;
import org.phonepe.models.Point2D;
import org.phonepe.models.Ship;
import org.phonepe.service.Game;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test coverage for the BattleShip game implementation.
 */
public class GameTest {

    private Game game;

    @BeforeEach
    public void setup() {
        game = new Game();
        game.initGame(6);
    }

    @Test
    public void testGameInitialization() {
        // Valid initialization should not throw
        assertDoesNotThrow(() -> game.initGame(6));

        // Invalid N (less than 2) should throw
        Game g2 = new Game();
        assertThrows(IllegalArgumentException.class, () -> g2.initGame(1));
    }

    @Test
    public void testAddShipValid() {
        assertDoesNotThrow(() -> game.addShip("SH1", 1, 1, 1, 4, 4));
        assertEquals(1, game.playerA.remainingShips());
        assertEquals(1, game.playerB.remainingShips());
    }

    @Test
    public void testAddShipOutOfBoundsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> game.addShip("SH1", 1, -1, 0, 4, 4));
    }

    @Test
    public void testAddShipOverlapThrows() {
        game.addShip("SH1", 0, 1, 1, 4, 4);
        // Overlaps on PlayerA
        assertThrows(IllegalArgumentException.class,
                () -> game.addShip("SH2", 0, 1, 1, 5, 5));
    }

    @Test
    public void testAddShipWrongHalfThrows() {
        // PlayerA ship in PlayerB half (x=5)
        assertThrows(IllegalArgumentException.class,
                () -> game.addShip("SH1", 0, 5, 1, 4, 4));
    }

    @Test
    public void testStartGameRunsToCompletion() {
        game.addShip("SH1", 0, 1, 1, 4, 4);
        game.addShip("SH2", 0, 0, 0, 5, 5);
        assertDoesNotThrow(() -> game.startGame());
    }

    @Test
    public void testViewBattleFieldOutput() {
        game.addShip("SH1", 0, 1, 1, 4, 4);
        assertDoesNotThrow(() -> game.viewBattleField());
    }

    @Test
    public void testNoShipsCase() {
        Game g = new Game();
        g.initGame(6);
        assertDoesNotThrow(g::startGame);
    }

    @Test
    public void testDestroyShipAt() {
        game.addShip("SH1", 0, 1, 1, 4, 4);
        Player a = game.playerA;
        Player b = game.playerB;

        Point2D target = new Point2D(1, 1);
        assertTrue(a.hasShipAt(target));
        Ship destroyed = a.destroyShipAt(target);
        assertNotNull(destroyed);
        assertTrue(destroyed.isDestroyed());
        assertFalse(a.hasShipAt(target));
    }
}
