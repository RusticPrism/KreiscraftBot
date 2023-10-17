package de.rusticprism.kreiscraftbot.listener;

import de.rusticprism.kreiscraftbot.KreiscraftBot;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.isFromGuild()) {
            KreiscraftBot.getCommandManager().performCommand(event.getInteraction());
        }
    }
}
