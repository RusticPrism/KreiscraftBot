package de.rusticprism.kreiscraftbot.music.events;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.rusticprism.kreiscraftbot.music.AudioHandler;
import de.rusticprism.kreiscraftbot.music.QueuedTrack;
import de.rusticprism.kreiscraftbot.utils.EmbedUtil;
import de.rusticprism.kreiscraftbot.utils.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Arrays;

public class ResultHandler implements AudioLoadResultHandler {
    private final SlashCommandInteraction interaction;
    public ResultHandler(SlashCommandInteraction interaction) {
        this.interaction = interaction;
    }
    @Override
    public void trackLoaded(AudioTrack track) {
        AudioHandler handler = (AudioHandler) interaction.getGuild().getAudioManager().getSendingHandler();
        Member user = interaction.getMember();

        //Join VoiceChannel of Member
        if (user.getVoiceState().getChannel() == null) {
            interaction.replyEmbeds(new EmbedUtil("You are not connected to any VoiceChannel!", Color.red).getBuilder().build()).queue();
            return;
        }
        if (user.getVoiceState().isDeafened()) {
            interaction.replyEmbeds(new EmbedUtil("How do you wan´t to listen to music if you are deafened!", Color.red).getBuilder().build()).queue();
            return;
        }
        interaction.getGuild().getAudioManager().openAudioConnection(user.getVoiceState().getChannel());
        //Add Track
        EmbedBuilder builder = new EmbedUtil("[" + FormatUtil.shorten(track.getInfo().title, 20) + "]" + "(" + track.getInfo().uri + ")", Color.GREEN).getBuilder();
        builder.setAuthor("**| Song Added to Queue #1", "", user.getAvatarUrl());
        interaction.replyEmbeds(builder.build()).queue();
        handler.addTrack(new QueuedTrack(track, user.getUser()), interaction.getChannel());
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {

    }

    @Override
    public void noMatches() {
        interaction.replyEmbeds(new EmbedUtil("There where no matches Found by this Search-request", Color.RED).getBuilder().build()).queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        switch (exception.severity) {
            case COMMON -> interaction.replyEmbeds(new EmbedUtil("An **COMMON** Error occurred while trying to load that Song", Arrays.toString(exception.getCause().getStackTrace()),Color.ORANGE).getBuilder().build()).queue();
            case SUSPICIOUS -> interaction.replyEmbeds(new EmbedUtil("An **SUSPICIOUS** Error occurred while trying to load that Song", Arrays.toString(exception.getCause().getStackTrace()),Color.RED).getBuilder().build()).queue();
            case FAULT -> interaction.replyEmbeds(new EmbedUtil("An **FAULT** Error occurred while trying to load that Song", Arrays.toString(exception.getCause().getStackTrace()),Color.RED).getBuilder().build()).queue();
        }
        LoggerFactory.getLogger("KreiscraftBot").error(exception.getMessage(),exception.getCause());
    }
}
