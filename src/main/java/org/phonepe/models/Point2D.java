package org.phonepe.models;

public class Point2D {
    public final int x;
    public final int y;

    public Point2D(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point2D)) return false;
        Point2D p = (Point2D) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() { return "(" + x + "," + y + ")"; }
}