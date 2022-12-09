import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    public int numRows;
    public int numCols;
    public Background[][] background;
    public Entity[][] occupancy;
    public Set<Entity> entities;

    public WorldModel() {

    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }

    public static boolean withinBounds(WorldModel world, Point pos) {
        return pos.y >= 0 && pos.y < world.numRows && pos.x >= 0 && pos.x < world.numCols;
    }

    public static boolean isOccupied(WorldModel world, Point pos) {
        return withinBounds(world, pos) && getOccupancyCell(world, pos) != null;
    }

    public static Optional<Entity> findNearest(WorldModel world, Point pos, List<EntityKind> kinds) {
        List<Entity> ofType = new LinkedList<>();
        for (EntityKind kind : kinds) {
            for (Entity entity : world.entities) {
                if (entity.kind == kind) {
                    ofType.add(entity);
                }
            }
        }

        return Entity.nearestEntity(ofType, pos);
    }

    /*
       Assumes that there is no entity currently occupying the
       intended destination cell.
    */



    public static Optional<Entity> getOccupant(WorldModel world, Point pos) {
        if (isOccupied(world, pos)) {
            return Optional.of(getOccupancyCell(world, pos));
        } else {
            return Optional.empty();
        }
    }

    public static Entity getOccupancyCell(WorldModel world, Point pos) {
        return world.occupancy[pos.y][pos.x];
    }

    public static void setOccupancyCell(WorldModel world, Point pos, Entity entity) {
        world.occupancy[pos.y][pos.x] = entity;
    }
}
