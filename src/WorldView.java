import processing.core.PApplet;

public final class WorldView {
    public PApplet screen;
    public WorldModel world;
    public int tileWidth;
    public int tileHeight;
    public Viewport viewport;

    public WorldView(int numRows, int numCols, PApplet screen, WorldModel world, int tileWidth, int tileHeight) {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = new Viewport(numRows, numCols);
    }
    public static void shiftView(WorldView view, int colDelta, int rowDelta) {
        int newCol = WorldView.clamp(view.viewport.col + colDelta, 0, view.world.numCols - view.viewport.numCols);
        int newRow = WorldView.clamp(view.viewport.row + rowDelta, 0, view.world.numRows - view.viewport.numRows);

        Viewport.shift(view.viewport, newCol, newRow);
    }

    public static int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }
}
