package de.rusticprism.kreiscraftbot.commands.music;

import de.rusticprism.kreiscraftbot.KreiscraftBot;
import de.rusticprism.kreiscraftbot.commands.api.BotCommand;
import de.rusticprism.kreiscraftbot.commands.api.CommandInfo;
import de.rusticprism.kreiscraftbot.music.AudioHandler;
import de.rusticprism.kreiscraftbot.music.PlayerManager;
import de.rusticprism.kreiscraftbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.awt.*;

@CommandInfo(command = "skip", description = "The Command to skip the current Song")
public class SkipCommand extends BotCommand {
    @Override
    public void execute(Member member, TextChannel channel, SlashCommandInteraction interaction) {
        if(!interaction.getGuild().getAudioManager().isConnected()) {
            interaction.replyEmbeds(new EmbedUtil("I´am not connected to any Voice-channel", Color.RED).getBuilder().build()).queue();
            return;
        }
        AudioHandler handler = KreiscraftBot.getPlayerManager().getAudioHandler(channel.getGuild());
        handler.skipTrack();
        interaction.replyEmbeds(new EmbedUtil("Successfully skipped the current Song", Color.GREEN).getBuilder().build()).queue();
    }
}
