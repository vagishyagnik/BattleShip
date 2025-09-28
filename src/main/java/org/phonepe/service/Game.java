package org.phonepe.service;


import org.phonepe.models.Player;
import org.phonepe.models.Point2D;
import org.phonepe.models.Ship;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Main game logic and public APIs:
 *  - initGame(N)
 *  - addShip(id, size, ax, ay, bx, by)
 *  - startGame()
 *  - viewBattleField() (optional)
 *
 * Size interpretation:
 *   We treat the given 'size' as a radius (half-size). For a ship at (x,y) with size S,
 *   it occupies cells from (x-S, y-S) to (x+S, y+S) inclusive.
 *
 * Battlefield division:
 *   Left half columns [0 .. N/2 - 1] belong to PlayerA.
 *   Right half columns [N/2 .. N-1] belong to PlayerB.
 */
public class Game {
    private int N = -1;
    public Player playerA; // vagi :: make them private after testing
    public Player playerB; // vagi :: make them private after testing
    private final Random random = new Random();

    // track all fired coordinates globally to ensure no repeats
    private final Set<Point2D> firedCoordinates = new HashSet<>();

    private boolean initialized = false;

    public void initGame(int N) {
        if (N <= 1) throw new IllegalArgumentException("N must be > 1");
        if (N%2 != 0) throw new IllegalArgumentException("N cannot be odd!! We need to divide the space equally among Player A & Player B");
        this.N = N;
        this.playerA = new Player("PlayerA");
        this.playerB = new Player("PlayerB");
        this.firedCoordinates.clear();
        this.initialized = true;
        System.out.println("Game initialized: battlefield size " + N + "x" + N +
                ". Left half columns [0.." + (N/2 - 1) + "] => PlayerA; Right half columns [" + (N/2) + ".." + (N-1) + "] => PlayerB.");
    }

    /**
     * Add ship to both players.
     * @param id ship id
     * @param size interpreted as radius (see class comment)
     * @param ax center-x for playerA
     * @param ay center-y for playerA
     * @param bx center-x for playerB
     * @param by center-y for playerB
     */
    public void addShip(String id, int size, int ax, int ay, int bx, int by) {
        ensureInitialized();
        if (size < 0) throw new IllegalArgumentException("size must be >= 0");
        // Compute cells for both ships
        Set<Point2D> cellsA = computeCellsForShip(ax, ay, size);
        Set<Point2D> cellsB = computeCellsForShip(bx, by, size);

        // Validate within respective halves
        for (Point2D p : cellsA) {
            if (!isWithinBounds(p)) throw new IllegalArgumentException("PlayerA ship '" + id + "' cell out of bounds: " + p);
            if (!isInLeftHalf(p)) throw new IllegalArgumentException("PlayerA ship '" + id + "' cell not in PlayerA half: " + p);
        }
        for (Point2D p : cellsB) {
            if (!isWithinBounds(p)) throw new IllegalArgumentException("PlayerB ship '" + id + "' cell out of bounds: " + p);
            if (!isInRightHalf(p)) throw new IllegalArgumentException("PlayerB ship '" + id + "' cell not in PlayerB half: " + p);
        }

        // Validate overlap with existing ships (ships cannot overlap, but can touch boundaries)
        for (Point2D p : cellsA) {
            if (playerA.hasShipAt(p)) throw new IllegalArgumentException("PlayerA ship '" + id + "' overlaps existing ship at " + p);
        }
        for (Point2D p : cellsB) {
            if (playerB.hasShipAt(p)) throw new IllegalArgumentException("PlayerB ship '" + id + "' overlaps existing ship at " + p);
        }

        // Add ships
        Ship shipA = new Ship("A-" + id, cellsA);
        Ship shipB = new Ship("B-" + id, cellsB);

        playerA.addShip(shipA);
        playerB.addShip(shipB);

        System.out.println("Added ship '" + id + "' with size length=" + size + " for both players.");
    }

    /**
     * Start the game. PlayerA goes first. Each turn one random coordinate (in opponent's half)
     * is selected that hasn't been fired before. Hit destroys the opponent ship wholly.
     */
    public void startGame() {
        ensureInitialized();
        if (playerA.getShips().isEmpty() && playerB.getShips().isEmpty()) {
            System.out.println("No ships placed. Nothing to play.");
            return;
        }

        Player current = playerA;
        Player opponent = playerB;

        while (true) {
            // choose a random coordinate in opponent's half that hasn't been fired before
            Point2D target = chooseRandomTargetInHalf(opponent);
            firedCoordinates.add(target);

            System.out.printf("%s's turn: Missile fired at %s : ", current.getName(), target);
            Ship destroyed = opponent.destroyShipAt(target);
            if (destroyed != null) {
                System.out.printf("\"Hit\" %s destroyed.\n", destroyed.getId());
            } else {
                System.out.println("\"Miss\"");
            }

            System.out.printf("Ships Remaining - PlayerA:%d, PlayerB:%d\n",
                    playerA.remainingShips(), playerB.remainingShips());

            // check for game end
            if (opponent.remainingShips() == 0) {
                System.out.println("GameOver. " + current.getName() + " wins.");
                break;
            }
            // swap turns
            Player tmp = current;
            current = opponent;
            opponent = tmp;

            // guard: if we've fired at all cells and no winner, break to avoid infinite loop
            if (firedCoordinates.size() >= (long)N * N) {
                System.out.println("All coordinates fired. Game ends as draw (no ships remaining?).");
                break;
            }
        }
    }

    public void viewBattleField() {
        ensureInitialized();
        // produce an N x N grid of strings
        String[][] grid = new String[N][N];
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) grid[r][c] = ".";
        }
        // mark PlayerA ships as A-id and PlayerB as B-id (for destroyed ships we still show id)
        for (Ship s : playerA.getShips()) {
            if (s.isDestroyed()) continue;
            for (Point2D p : s.getCells()) {
                    grid[p.y][p.x] = s.getId();
            }
        }
        for (Ship s : playerB.getShips()) {
            if (s.isDestroyed()) continue;
            for (Point2D p : s.getCells()) {
                    grid[p.y][p.x] = s.getId();
            }
        }
        // print grid with y from 0..N-1 (top to bottom). Column x increases left->right
        System.out.println("Battlefield view (format grid[y][x], top-left is (0,0)):");
        for (int r = 0; r < N; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < N; c++) {
                sb.append(String.format("%6s", grid[r][c]));
            }
            System.out.println(sb.toString());
        }
    }

    // --- helpers ---
    private Set<Point2D> computeCellsForShip(int centerX, int centerY, int sizeRadius) {
        Set<Point2D> cells = new HashSet<>();
        int upperLeftX = sizeRadius/2;
        int upperLeftY = sizeRadius - upperLeftX;
        for (int dx = -upperLeftX; dx <= upperLeftY; dx++) {
            for (int dy = -upperLeftX; dy <= upperLeftY; dy++) {
                int x = centerX + dx;
                int y = centerY + dy;
                cells.add(new Point2D(x, y));
            }
        }
        return cells;
    }

    private boolean isWithinBounds(Point2D p) {
        return p.x >= 0 && p.x < N && p.y >= 0 && p.y < N;
    }

    private boolean isInLeftHalf(Point2D p) {
        return p.x >= 0 && p.x < (N / 2);
    }

    private boolean isInRightHalf(Point2D p) {
        return p.x >= (N / 2) && p.x < N;
    }

    private void ensureInitialized() {
        if (!initialized) throw new IllegalStateException("Game not initialized. Call initGame(N) first.");
    }

    private Point2D chooseRandomTargetInHalf(Player targetOwner) {
        // select uniformly a random cell within the owner's half that hasn't been fired at before.
        // We'll attempt random selection; if too many tried, fall back to deterministic scan.
        int attempts = 0;
        int maxAttempts = 5000000;
        while (attempts < maxAttempts) {
            int x = random.nextInt(N);
            int y = random.nextInt(N);
            Point2D p = new Point2D(x, y);
            // ensure p belongs to owner's half
            if (targetOwner == playerA && !isInLeftHalf(p)) { attempts++; continue; }
            if (targetOwner == playerB && !isInRightHalf(p)) { attempts++; continue; }
            if (firedCoordinates.contains(p)) { attempts++; continue; }
            return p;
        }
        // fallback: deterministic scan for first available coordinate
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                Point2D p = new Point2D(x, y);
                if (firedCoordinates.contains(p)) continue;
                if (targetOwner == playerA && !isInLeftHalf(p)) continue;
                if (targetOwner == playerB && !isInRightHalf(p)) continue;
                return p;
            }
        }
        // as last resort, return any unchecked coordinate (shouldn't happen)
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                Point2D p = new Point2D(x, y);
                if (!firedCoordinates.contains(p)) return p;
            }
        }
        throw new IllegalStateException("No available targets left.");
    }
}
