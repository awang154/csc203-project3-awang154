import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An action that can be taken by an entity
 */
public final class Action extends Animation {
    /*
    public ActionKind kind;
    public Entity entity;
    public WorldModel world;
    public int repeatCount;
    public ImageStore imageStore;

     */

    public Action(ActionKind kind, Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
        super(kind, entity, world, imageStore, repeatCount);
    }

    public static void executeDudeNotFullActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = WorldModel.findNearest(world, entity.position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (target.isEmpty() || !moveToNotFull(entity, world, target.get(), scheduler) || !transformNotFull(entity, world, scheduler, imageStore)) {
            EventScheduler.scheduleEvent(scheduler, entity, createActivityAction(entity, world, imageStore), entity.actionPeriod);
        }
    }

    public static void executeDudeFullActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = WorldModel.findNearest(world, entity.position, new ArrayList<>(List.of(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && moveToFull(entity, world, fullTarget.get(), scheduler)) {
            transformFull(entity, world, scheduler, imageStore);
        } else {
            EventScheduler.scheduleEvent(scheduler, entity, createActivityAction(entity, world, imageStore), entity.actionPeriod);
        }
    }

    public static void executeAnimationAction(Action action, EventScheduler scheduler) {
        ImageStore.nextImage(action.entity);

        if (action.repeatCount != 1) {
            EventScheduler.scheduleEvent(scheduler, action.entity, createAnimationAction(action.entity, Math.max(action.repeatCount - 1, 0)), getAnimationPeriod(action.entity));
        }
    }

    public static void executeActivityAction(Action action, EventScheduler scheduler) {
        switch (action.entity.kind) {
            case SAPLING:
                executeSaplingActivity(action.entity, action.world, action.imageStore, scheduler);
                break;
            case TREE:
                executeTreeActivity(action.entity, action.world, action.imageStore, scheduler);
                break;
            case FAIRY:
                executeFairyActivity(action.entity, action.world, action.imageStore, scheduler);
                break;
            case DUDE_NOT_FULL:
                Action.executeDudeNotFullActivity(action.entity, action.world, action.imageStore, scheduler);
                break;
            case DUDE_FULL:
                Action.executeDudeFullActivity(action.entity, action.world, action.imageStore, scheduler);
                break;
            default:
                throw new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", action.entity.kind));
        }
    }

    public static void scheduleActions(Entity entity, EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        switch (entity.kind) {
            case DUDE_FULL:
                EventScheduler.scheduleEvent(scheduler, entity, createActivityAction(entity, world, imageStore), entity.actionPeriod);
                EventScheduler.scheduleEvent(scheduler, entity, createAnimationAction(entity, 0), getAnimationPeriod(entity));
                break;

            case DUDE_NOT_FULL:
                EventScheduler.scheduleEvent(scheduler, entity, createActivityAction(entity, world, imageStore), entity.actionPeriod);
                EventScheduler.scheduleEvent(scheduler, entity, createAnimationAction(entity, 0), getAnimationPeriod(entity));
                break;

            case OBSTACLE:
                EventScheduler.scheduleEvent(scheduler, entity, createAnimationAction(entity, 0), getAnimationPeriod(entity));
                break;

            case FAIRY:
                EventScheduler.scheduleEvent(scheduler, entity, createActivityAction(entity, world, imageStore), entity.actionPeriod);
                EventScheduler.scheduleEvent(scheduler, entity, createAnimationAction(entity, 0), getAnimationPeriod(entity));
                break;

            case SAPLING:
                EventScheduler.scheduleEvent(scheduler, entity, createActivityAction(entity, world, imageStore), entity.actionPeriod);
                EventScheduler.scheduleEvent(scheduler, entity, createAnimationAction(entity, 0), getAnimationPeriod(entity));
                break;

            case TREE:
                EventScheduler.scheduleEvent(scheduler, entity, createActivityAction(entity, world, imageStore), entity.actionPeriod);
                EventScheduler.scheduleEvent(scheduler, entity, createAnimationAction(entity, 0), getAnimationPeriod(entity));
                break;

            default:
        }
    }

    public static void executeAction(Action action, EventScheduler scheduler) {
        switch (action.kind) {
            case ACTIVITY:
                Action.executeActivityAction(action, scheduler);
                break;

            case ANIMATION:
                Action.executeAnimationAction(action, scheduler);
                break;
        }
    }

    public static Action createAnimationAction(Entity entity, int repeatCount) {
        return new Action(ActionKind.ANIMATION, entity, null, null, repeatCount);
    }

    public static Action createActivityAction(Entity entity, WorldModel world, ImageStore imageStore) {
        return new Action(ActionKind.ACTIVITY, entity, world, imageStore, 0);
    }

    public static void executeSaplingActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        entity.health++;
        if (!transformPlant(entity, world, scheduler, imageStore)) {
            EventScheduler.scheduleEvent(scheduler, entity, Action.createActivityAction(entity, world, imageStore), entity.actionPeriod);
        }
    }

    public static void executeTreeActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!transformPlant(entity, world, scheduler, imageStore)) {

            EventScheduler.scheduleEvent(scheduler, entity, Action.createActivityAction(entity, world, imageStore), entity.actionPeriod);
        }
    }

    public static void executeFairyActivity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = WorldModel.findNearest(world, entity.position, new ArrayList<>(List.of(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (moveToFairy(entity, world, fairyTarget.get(), scheduler)) {

                Entity sapling = Entity.createSapling(Functions.SAPLING_KEY + "_" + fairyTarget.get().id, tgtPos, imageStore.getImageList(imageStore, Functions.SAPLING_KEY), 0);

                Entity.addEntity(world, sapling);
                Action.scheduleActions(sapling, scheduler, world, imageStore);
            }
        }

        EventScheduler.scheduleEvent(scheduler, entity, Action.createActivityAction(entity, world, imageStore), entity.actionPeriod);
    }

    public static boolean transformNotFull(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (entity.resourceCount >= entity.resourceLimit) {
            Entity dude = Entity.createDudeFull(entity.id, entity.position, entity.actionPeriod, entity.animationPeriod, entity.resourceLimit, entity.images);

            Entity.removeEntity(world, scheduler, entity);
            EventScheduler.unscheduleAllEvents(scheduler, entity);

            Entity.addEntity(world, dude);
            Action.scheduleActions(dude, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public static void transformFull(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity dude = Entity.createDudeNotFull(entity.id, entity.position, entity.actionPeriod, entity.animationPeriod, entity.resourceLimit, entity.images);

        Entity.removeEntity(world, scheduler, entity);

        Entity.addEntity(world, dude);
        Action.scheduleActions(dude, scheduler, world, imageStore);
    }


    public static boolean transformPlant(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (entity.kind == EntityKind.TREE) {
            return transformTree(entity, world, scheduler, imageStore);
        } else if (entity.kind == EntityKind.SAPLING) {
            return transformSapling(entity, world, scheduler, imageStore);
        } else {
            throw new UnsupportedOperationException(String.format("transformPlant not supported for %s", entity));
        }
    }

    public static boolean transformTree(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (entity.health <= 0) {
            Entity stump = Entity.createStump(Functions.STUMP_KEY + "_" + entity.id, entity.position, imageStore.getImageList(imageStore, Functions.STUMP_KEY));

            Entity.removeEntity(world, scheduler, entity);

            Entity.addEntity(world, stump);

            return true;
        }

        return false;
    }

    public static boolean transformSapling(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (entity.health <= 0) {
            Entity stump = Entity.createStump(Functions.STUMP_KEY + "_" + entity.id, entity.position, imageStore.getImageList(imageStore, Functions.STUMP_KEY));

            Entity.removeEntity(world, scheduler, entity);

            Entity.addEntity(world, stump);

            return true;
        } else if (entity.health >= entity.healthLimit) {
            Entity tree = Entity.createTree(Functions.TREE_KEY + "_" + entity.id, entity.position, Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN), Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN), Functions.getIntFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN), imageStore.getImageList(imageStore, Functions.TREE_KEY));

            Entity.removeEntity(world, scheduler, entity);

            Entity.addEntity(world, tree);
            Action.scheduleActions(tree, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public static boolean moveToFairy(Entity fairy, WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(fairy.position, target.position)) {
            Entity.removeEntity(world, scheduler, target);
            return true;
        } else {
            Point nextPos = nextPositionFairy(fairy, world, target.position);

            if (!fairy.position.equals(nextPos)) {
                Entity.moveEntity(world, scheduler, fairy, nextPos);
            }
            return false;
        }
    }

    public static boolean moveToNotFull(Entity dude, WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(dude.position, target.position)) {
            dude.resourceCount += 1;
            target.health--;
            return true;
        } else {
            Point nextPos = nextPositionDude(dude, world, target.position);

            if (!dude.position.equals(nextPos)) {
                Entity.moveEntity(world, scheduler, dude, nextPos);
            }
            return false;
        }
    }

    public static boolean moveToFull(Entity dude, WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(dude.position, target.position)) {
            return true;
        } else {
            Point nextPos = nextPositionDude(dude, world, target.position);

            if (!dude.position.equals(nextPos)) {
                Entity.moveEntity(world, scheduler, dude, nextPos);
            }
            return false;
        }
    }

    public static Point nextPositionFairy(Entity entity, WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - entity.position.x);
        Point newPos = new Point(entity.position.x + horiz, entity.position.y);

        if (horiz == 0 || WorldModel.isOccupied(world, newPos)) {
            int vert = Integer.signum(destPos.y - entity.position.y);
            newPos = new Point(entity.position.x, entity.position.y + vert);

            if (vert == 0 || WorldModel.isOccupied(world, newPos)) {
                newPos = entity.position;
            }
        }

        return newPos;
    }

    public static Point nextPositionDude(Entity entity, WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - entity.position.x);
        Point newPos = new Point(entity.position.x + horiz, entity.position.y);

        if (horiz == 0 || WorldModel.isOccupied(world, newPos) && WorldModel.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.y - entity.position.y);
            newPos = new Point(entity.position.x, entity.position.y + vert);

            if (vert == 0 || WorldModel.isOccupied(world, newPos) && WorldModel.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
                newPos = entity.position;
            }
        }

        return newPos;
    }

    public static double getAnimationPeriod(Entity entity) {
        switch (entity.kind) {
            case DUDE_FULL:
            case DUDE_NOT_FULL:
            case OBSTACLE:
            case FAIRY:
            case SAPLING:
            case TREE:
                return entity.animationPeriod;
            default:
                throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported for %s", entity.kind));
        }
    }
}
