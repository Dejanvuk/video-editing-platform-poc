package com.dejanvuk.microservices.api.event;

import java.time.LocalDateTime;

public class Event<K, T> {

    public enum Type {CREATE, DELETE, UPDATE}

    private Event.Type eventType;
    private K key;
    private T data;
    private LocalDateTime creationDate;

    public Event() {
        this.eventType = null;
        this.key = null;
        this.data = null;
        this.creationDate = null;
    }

    public Event(Type eventType, K key, T data) {
        this.eventType = eventType;
        this.key = key;
        this.data = data;
        this.creationDate = LocalDateTime.now();
    }

    public Type getEventType() {
        return eventType;
    }

    public K getKey() {
        return key;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
