package org.phonepe;

import org.phonepe.service.Game;

public class MainTesting {
    public static void main(String[] args) {
        Game g = new Game();

        g.initGame(6);

        try {
            g.addShip("SH1", 1, 1, 1, 4, 4);
            g.addShip("SH2", 0, 0, 4, 5, 1);
        } catch (Exception ex) {
            System.out.println("Error adding ship: " + ex.getMessage());
            return;
        }
        g.viewBattleField();
        g.startGame();
    }
}
