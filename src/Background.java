import java.util.List;
import java.util.Optional;

import processing.core.PImage;

/**
 * Represents a background for the 2D world.
 */
public final class Background {
    public String id;
    public List<PImage> images;
    public int imageIndex;

    public Background(String id, List<PImage> images) {
        this.id = id;
        this.images = images;
    }

    public static void drawBackground(WorldView view) {
        for (int row = 0; row < view.viewport.numRows; row++) {
            for (int col = 0; col < view.viewport.numCols; col++) {
                Point worldPoint = VirtualWorld.viewportToWorld(view.viewport, col, row);
                Optional<PImage> image = Functions.getBackgroundImage(view.world, worldPoint);
                if (image.isPresent()) {
                    view.screen.image(image.get(), col * view.tileWidth, row * view.tileHeight);
                }
            }
        }
    }

    public static Background getBackgroundCell(WorldModel world, Point pos) {
        return world.background[pos.y][pos.x];
    }

    public static void setBackgroundCell(WorldModel world, Point pos, Background background) {
        world.background[pos.y][pos.x] = background;
    }

    public static void parseBackgroundRow(WorldModel world, String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < world.numRows){
            int rows = Math.min(cells.length, world.numCols);
            for (int col = 0; col < rows; col++){
                world.background[row][col] = new Background(cells[col], Functions.getImageList(imageStore, cells[col]));
            }
        }
    }
}
