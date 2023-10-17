package de.rusticprism.kreiscraftbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MusicConfig {
    private Gson gson;
    public MusicConfig() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }
}
