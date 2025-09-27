package org.phonepe.models;

import java.util.*;

public class Player {
    private final String name;
    private final Map<String, Ship> shipsById = new LinkedHashMap<>();
    // Set of all occupied cells for quick hit-checking
    private final Set<Point2D> occupied = new HashSet<>();

    public Player(String name) { this.name = name; }

    public String getName() { return name; }

    public void addShip(Ship ship) {
        if (shipsById.containsKey(ship.getId())) {
            throw new IllegalArgumentException("Ship id already present for player " + name + ": " + ship.getId());
        }
        shipsById.put(ship.getId(), ship);
        occupied.addAll(ship.getCells());
    }

    public boolean hasShipAt(Point2D p) {
        return occupied.contains(p);
    }

    /**
     * Mark the ship that occupies p as destroyed and return that ship.
     * If no ship at p, returns null.
     */
    public Ship destroyShipAt(Point2D p) {
        for (Ship s : shipsById.values()) {
            if (!s.isDestroyed() && s.occupies(p)) {
                s.destroy();
                // remove its cells from occupied so later queries reflect destruction
                occupied.removeAll(s.getCells());
                return s;
            }
        }
        return null;
    }

    public int remainingShips() {
        int cnt = 0;
        for (Ship s : shipsById.values()) if (!s.isDestroyed()) cnt++;
        return cnt;
    }

    public Collection<Ship> getShips() {
        return Collections.unmodifiableCollection(shipsById.values());
    }
}
