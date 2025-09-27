package org.phonepe;

import org.phonepe.service.Game;

import java.util.Scanner;

/**
 * CLI-based Battleship Game
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Game game = new Game();

        System.out.println("=== Welcome to Battleship CLI Game ===");
        System.out.println("Type 'help' for list of commands.");

        while (true) {
            System.out.print(">> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            String cmd = tokens[0].toLowerCase();

            try {
                switch (cmd) {
                    case "help":
                        printHelp();
                        break;

                    case "exit":
                        System.out.println("Exiting Battleship. Goodbye!");
                        return;

                    case "initgame":
                        if (tokens.length != 2) {
                            System.out.println("Usage: initGame <N>");
                            break;
                        }
                        int N = Integer.parseInt(tokens[1]);
                        game.initGame(N);
                        break;

                    case "addship":
                        if (tokens.length != 7) {
                            System.out.println("Usage: addShip <id> <size> <ax> <ay> <bx> <by>");
                            break;
                        }
                        String id = tokens[1];
                        int size = Integer.parseInt(tokens[2]);
                        int ax = Integer.parseInt(tokens[3]);
                        int ay = Integer.parseInt(tokens[4]);
                        int bx = Integer.parseInt(tokens[5]);
                        int by = Integer.parseInt(tokens[6]);
                        game.addShip(id, size, ax, ay, bx, by);
                        break;

                    case "viewbattlefield":
                        game.viewBattleField();
                        break;

                    case "startgame":
                        game.startGame();
                        break;

                    default:
                        System.out.println("Unknown command: " + cmd + ". Type 'help' for list.");
                }

            } catch (Exception e) {
                System.err.println("⚠️  Error: " + e.getMessage());
            }
        }
    }

    private static void printHelp() {
        System.out.println("Available Commands:");
        System.out.println("  initGame <N>                     - Initialize the game with N x N battlefield");
        System.out.println("  addShip <id> <size> <ax> <ay> <bx> <by>   - Add a ship for both players");
        System.out.println("  viewBattleField                  - Display the battlefield grid");
        System.out.println("  startGame                        - Begin the game (PlayerA fires first)");
        System.out.println("  help                             - Show this help menu");
        System.out.println("  exit                             - Exit the game");
    }
}
