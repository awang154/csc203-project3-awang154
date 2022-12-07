import java.util.List;
import java.util.Objects;
import java.util.Optional;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity {
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;
    public int resourceLimit;
    public int resourceCount;
    public double actionPeriod;
    public double animationPeriod;
    public int health;
    public int healthLimit;

    public Entity(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        this.kind = kind;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public static void addEntity(WorldModel world, Entity entity) {
        if (Functions.withinBounds(world, entity.position)) {
            Functions.setOccupancyCell(world, entity.position, entity);
            world.entities.add(entity);
        }
    }

    public static void moveEntity(WorldModel world, EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.position;
        if (Functions.withinBounds(world, pos) && !pos.equals(oldPos)) {
            Functions.setOccupancyCell(world, oldPos, null);
            Optional<Entity> occupant = Functions.getOccupant(world, pos);
            occupant.ifPresent(target -> removeEntity(world, scheduler, target));
            Functions.setOccupancyCell(world, pos, entity);
            entity.position = pos;
        }
    }

    public static void removeEntity(WorldModel world, EventScheduler scheduler, Entity entity) {
        EventScheduler.unscheduleAllEvents(scheduler, entity);
        removeEntityAt(world, entity.position);
    }

    public static void removeEntityAt(WorldModel world, Point pos) {
        if (Functions.withinBounds(world, pos) && Functions.getOccupancyCell(world, pos) != null) {
            Entity entity = Functions.getOccupancyCell(world, pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.position = new Point(-1, -1);
            world.entities.remove(entity);
            Functions.setOccupancyCell(world, pos, null);
        }
    }

    public static void tryAddEntity(WorldModel world, Entity entity) {
        if (Functions.isOccupied(world, entity.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        Entity.addEntity(world, entity);
    }

    public static void parseEntity(WorldModel world, String line, ImageStore imageStore) {
        String[] properties = line.split(" ", Functions.ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= Functions.ENTITY_NUM_PROPERTIES) {
            String key = properties[Functions.PROPERTY_KEY];
            String id = properties[Functions.PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[Functions.PROPERTY_COL]), Integer.parseInt(properties[Functions.PROPERTY_ROW]));

            properties = properties.length == Functions.ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[Functions.ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case Functions.OBSTACLE_KEY -> Functions.parseObstacle(world, properties, pt, id, imageStore);
                case Functions.DUDE_KEY -> Functions.parseDude(world, properties, pt, id, imageStore);
                case Functions.FAIRY_KEY -> Functions.parseFairy(world, properties, pt, id, imageStore);
                case Functions.HOUSE_KEY -> Functions.parseHouse(world, properties, pt, id, imageStore);
                case Functions.TREE_KEY -> Functions.parseTree(world, properties, pt, id, imageStore);
                case Functions.SAPLING_KEY -> Functions.parseSapling(world, properties, pt, id, imageStore);
                case Functions.STUMP_KEY -> Functions.parseStump(world, properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        }else{
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }

    public static Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = Functions.distanceSquared(nearest.position, pos);

            for (Entity other : entities) {
                int otherDistance = Functions.distanceSquared(other.position, pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public static void drawEntities(WorldView view) {
        for (Entity entity : view.world.entities) {
            Point pos = entity.position;

            if (Functions.contains(view.viewport, pos)) {
                Point viewPoint = Viewport.worldToViewport(view.viewport, pos.x, pos.y);
                view.screen.image(ImageStore.getCurrentImage(entity), viewPoint.x * view.tileWidth, viewPoint.y * view.tileHeight);
            }
        }
    }
}
