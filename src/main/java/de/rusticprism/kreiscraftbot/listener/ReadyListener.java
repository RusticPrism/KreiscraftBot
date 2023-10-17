package de.rusticprism.kreiscraftbot.listener;

import de.rusticprism.kreiscraftbot.KreiscraftBot;
import de.rusticprism.kreiscraftbot.commands.HelpCommand;
import de.rusticprism.kreiscraftbot.music.AudioHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReadyListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        KreiscraftBot.getPlayerManager().init();
        KreiscraftBot.getCommandManager().registerCommand(Objects.requireNonNull(event.getJDA().getGuildById(836337063640301629L)));
    }
}
