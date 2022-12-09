abstract class ActionClass {
    ActionKind kind;
    Entity entity;
    WorldModel world;
    int repeatCount;
    ImageStore imageStore;

    ActionClass (ActionKind kind, Entity entity, WorldModel world, ImageStore imageStore, int repeatCount){
        this.kind = kind;
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }
}