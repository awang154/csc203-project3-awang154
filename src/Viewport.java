public final class Viewport {
    public int row;
    public int col;
    public int numRows;
    public int numCols;

    public Viewport(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public static void shift(Viewport viewport, int col, int row) {
        viewport.col = col;
        viewport.row = row;
    }

    public static Point worldToViewport(Viewport viewport, int col, int row) {
        return new Point(col - viewport.col, row - viewport.row);
    }

    public static void drawViewport(WorldView view) {
        Background.drawBackground(view);
        Entity.drawEntities(view);
    }
}
