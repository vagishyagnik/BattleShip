package org.phonepe;

import org.phonepe.service.Game;

public class MainTesting {
    public static void main(String[] args) {
        Game g = new Game();

        // Sample run similar to PDF sample. Adjust coordinates/sizes as needed.
        // NOTE: 'size' is radius. In sample PDF they used N=6, we pick N=6.
        g.initGame(6);

        // Example: add SH1 with radius 1 (occupies 3x3 block). Positions chosen inside halves.
        // PlayerA center at (1,1), PlayerB center at (4,4)
        try {
            g.addShip("SH1", 1, 1, 1, 4, 4);
            // add a second ship
            g.addShip("SH2", 0, 0, 4, 5, 1); // size=0 => single cell ship
        } catch (Exception ex) {
            System.err.println("Error adding ship: " + ex.getMessage());
            return;
        }

        // optional: view battlefield
        g.viewBattleField();

        // start the automated random-missile game
        g.startGame();
    }
}
