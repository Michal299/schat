package pl.edu.pg.eti.backend.container;

import java.util.HashMap;
import java.util.Map;

public class BackendContainer {

    private static BackendContainer instance;
    final private Map<String, Object> objects;
    final private Map<String, Thread> threads;

    BackendContainer() {
        objects = new HashMap<>();
        threads = new HashMap<>();
    }

    public static BackendContainer getInstance() {
        if (instance == null) {
            instance = new BackendContainer();
        }

        return instance;
    }

    public void register(String key, Object object) {
        objects.put(key, object);
    }

    public void register(Class registeredClass, Object object) {
        register(registeredClass.getName(), object);
    }

    public Thread registerThread(final String threadId, Runnable threadService) {
        final Thread thread = new Thread(threadService);
        threads.put(threadId, thread);
        return thread;
    }

    public Object getObject(String id) {
        return objects.get(id);
    }

    public Object getObject(Class objectClass) {
        return objects.get(objectClass.getName());
    }

    public Thread getThread(final String threadId) {
        return threads.get(threadId);
    }

    public <T> T getComponent(final Class<T> componentClass) {
        return (T)objects.get(componentClass.getName());
    }
}
