package pl.edu.pg.eti.backend.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventsBroker {

    private final Map<EventType, List<Consumer<Event>>> listeners;

    public EventsBroker() {
        listeners = new HashMap<>();
    }

    public void addListener(Consumer<Event> listener, EventType eventType) {
        if (!listeners.containsKey(eventType)) {
            listeners.put(eventType, new ArrayList<>());
        }

        listeners.get(eventType).add(listener);
    }

    public void publishEvent(Event event) {
        final var listenersForEventType = listeners.get(event.getType());
        listenersForEventType.forEach(consumer -> consumer.accept(event));
    }
}
