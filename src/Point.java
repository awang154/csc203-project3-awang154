/**
 * A simple class representing a location in 2D space.
 */
public final class Point extends PointClass{
    public Point(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /*
    public boolean equals(Object other) {
        return other instanceof Point && ((Point) other).x == this.x && ((Point) other).y == this.y;
    }
    */



}
