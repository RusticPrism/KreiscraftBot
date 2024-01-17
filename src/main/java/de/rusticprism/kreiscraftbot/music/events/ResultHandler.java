package de.rusticprism.kreiscraftbot.music.events;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.donkaos.systensor.Sys;
import de.rusticprism.kreiscraftbot.KreiscraftBot;
import de.rusticprism.kreiscraftbot.music.AudioHandler;
import de.rusticprism.kreiscraftbot.music.QueuedTrack;
import de.rusticprism.kreiscraftbot.utils.EmbedUtil;
import de.rusticprism.kreiscraftbot.utils.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.awt.*;
import java.util.Arrays;

public class ResultHandler implements AudioLoadResultHandler {
    private final SlashCommandInteraction interaction;
    public ResultHandler(SlashCommandInteraction interaction) {
        this.interaction = interaction;
    }
    public void sendSingleMessage(AudioTrack track, User user) {
        EmbedBuilder builder = new EmbedUtil("[" + FormatUtil.shorten(track.getInfo().title, 30) + "]" + "(" + track.getInfo().uri + ")", Color.GREEN).getBuilder();
        builder.setAuthor("Song Added to Queue #1", user.getAvatarUrl(), user.getAvatarUrl());
        interaction.replyEmbeds(builder.build()).queue();
    }
    @Override
    public void trackLoaded(AudioTrack track) {
        Sys.debug(track.getInfo().title);
        AudioHandler handler = KreiscraftBot.getPlayerManager().getAudioHandler(interaction.getGuild());
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
        sendSingleMessage(track, user.getUser());
        assert handler != null;
        handler.addTrack(new QueuedTrack(track, user.getUser()), interaction.getChannel().asTextChannel());
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        Sys.debug(playlist.getTracks().stream().map(track -> track.getInfo().title).toList().toString());
        if(playlist.getTracks().size() == 1 || playlist.isSearchResult()) {
            interaction.getGuild().getAudioManager().openAudioConnection(interaction.getMember().getVoiceState().getChannel());
            sendSingleMessage(playlist.getTracks().get(0), interaction.getUser());
            KreiscraftBot.getPlayerManager().getAudioHandler(interaction.getGuild()).addTrack(new QueuedTrack(playlist.getTracks().get(0), interaction.getUser()), interaction.getChannel().asTextChannel());
        }
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
        Sys.error(exception.getMessage(), exception.getCause().getLocalizedMessage());
    }
}
