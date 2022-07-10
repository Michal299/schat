package pl.edu.pg.eti.backend.message.entity;

public enum MessageType {
    TEXT,
    FILE,
    SESSION_KEY,
    SESSION_KEY_ACKNOWLEDGE,
    PUBLIC_KEY_REQUEST,
    PUBLIC_KEY_RESPONSE,
}
