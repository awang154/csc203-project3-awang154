import java.util.*;

/**
 * Keeps track of events that have been scheduled.
 */
public final class EventScheduler {
    public PriorityQueue<Event> eventQueue;
    public Map<Entity, List<Event>> pendingEvents;
    public double currentTime;

    public EventScheduler() {
        this.eventQueue = new PriorityQueue<>(new EventComparator());
        this.pendingEvents = new HashMap<>();
        this.currentTime = 0;
    }

    public static void scheduleEvent(EventScheduler scheduler, Entity entity, Action action, double afterPeriod) {
        double time = scheduler.currentTime + afterPeriod;

        Event event = new Event(action, time, entity);

        scheduler.eventQueue.add(event);

        // update list of pending events for the given entity
        List<Event> pending = scheduler.pendingEvents.getOrDefault(entity, new LinkedList<>());
        pending.add(event);
        scheduler.pendingEvents.put(entity, pending);
    }

    public static void unscheduleAllEvents(EventScheduler scheduler, Entity entity) {
        List<Event> pending = scheduler.pendingEvents.remove(entity);

        if (pending != null) {
            for (Event event : pending) {
                scheduler.eventQueue.remove(event);
            }
        }
    }

    public static void updateOnTime(EventScheduler scheduler, double time) {
        double stopTime = scheduler.currentTime + time;
        while (!scheduler.eventQueue.isEmpty() && scheduler.eventQueue.peek().time <= stopTime) {
            Event next = scheduler.eventQueue.poll();
            Event.removePendingEvent(scheduler, next);
            scheduler.currentTime = next.time;
            Action.executeAction(next.action, scheduler);
        }
        scheduler.currentTime = stopTime;
    }


}
