package de.rusticprism.kreiscraftbot.utils;

public enum RepeatMode {
    OFF("Off"),
    QUEUE("Queue"),
    SONG("Song");
    private final String mode;

    RepeatMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return mode;
    }

    @Override
    public String toString() {
        return mode;
    }
}
