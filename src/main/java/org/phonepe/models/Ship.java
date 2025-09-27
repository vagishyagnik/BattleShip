package org.phonepe.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Ship {
    private final String id;
    private final Set<Point2D> cells; // grid cells occupied by this ship
    private boolean destroyed = false;

    public Ship(String id, Set<Point2D> cells) {
        this.id = id;
        this.cells = new HashSet<>(cells);
    }

    public String getId() { return id; }

    public boolean occupies(Point2D p) {
        return cells.contains(p);
    }

    public void destroy() {
        this.destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public Set<Point2D> getCells() {
        return Collections.unmodifiableSet(cells);
    }

    @Override
    public String toString() {
        return id + (destroyed ? " (destroyed)" : "");
    }
}
