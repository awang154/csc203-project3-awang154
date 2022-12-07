import java.util.List;

/**
 * An event is made up of an Entity that is taking an
 * Action a specified time.
 */
public final class Event {
    public Action action;
    public double time;
    public Entity entity;

    public Event(Action action, double time, Entity entity) {
        this.action = action;
        this.time = time;
        this.entity = entity;
    }

    public static void removePendingEvent(EventScheduler scheduler, Event event) {
        List<Event> pending = scheduler.pendingEvents.get(event.entity);

        if (pending != null) {
            pending.remove(event);
        }
    }
}
