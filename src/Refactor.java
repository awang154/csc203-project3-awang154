import processing.core.PImage;

import java.util.List;

abstract class Refactor {
    //String objName = "";
    EntityKind kind;
    String id;
    Point position;
    List<PImage> images;
    int imageIndex;
    int resourceLimit;
    int resourceCount;
    double actionPeriod;
    double animationPeriod;

    int health;
    int healthLimit;


    Refactor(EntityKind kind, Point position, List<PImage> images, int resourceCount, int resourceLimit, double actionPeriod, double animationPeriod, int health, int healthLimit){
        this.kind = kind;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }
    abstract public String log();
}

