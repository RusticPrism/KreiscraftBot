package de.rusticprism.kreiscraftbot.utils;

import de.rusticprism.kreiscraftbot.KreiscraftBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class EmbedUtil {
    private final EmbedBuilder builder;
    public EmbedUtil(String title, String description, Color color) {
        this.builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription(description);
        builder.setColor(color);
        builder.setFooter("Version 1.0.0", KreiscraftBot.getJda().getSelfUser().getAvatarUrl());
    }
    public EmbedUtil(String description, Color color) {
        this.builder = new EmbedBuilder();
        builder.setDescription(description);
        builder.setColor(color);
        builder.setFooter("Version 1.0.0", KreiscraftBot.getJda().getSelfUser().getAvatarUrl());
    }
    public EmbedBuilder getBuilder() {
        return builder;
    }
}
