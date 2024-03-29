package de.rusticprism.kreiscraftbot.music.events;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.donkaos.systensor.Sys;
import de.rusticprism.kreiscraftbot.KreiscraftBot;
import de.rusticprism.kreiscraftbot.music.Queue;
import de.rusticprism.kreiscraftbot.music.QueuedTrack;
import de.rusticprism.kreiscraftbot.music.RequestMetaData;
import de.rusticprism.kreiscraftbot.utils.EmbedUtil;
import de.rusticprism.kreiscraftbot.utils.FormatUtil;
import de.rusticprism.kreiscraftbot.utils.RepeatMode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.awt.*;

public class AudioEvents extends AudioEventAdapter {
    private TextChannel channel;
    private final Queue queue;
    private final AudioPlayerManager manager;
    private final AudioPlayer audioPlayer;
    private final Guild guild;
    public AudioEvents(AudioPlayerManager manager, Guild guild, AudioPlayer player) {
        queue = new Queue();
        this.manager = manager;
        this.audioPlayer = player;
        this.guild = guild;
    }
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        EmbedBuilder builder = new EmbedUtil("[" + FormatUtil.shorten(track.getInfo().title,20) + "](" +track.getInfo().uri + ")`" + FormatUtil.formatTime(track.getDuration()) + "`" , Color.RED).getBuilder();
        builder.setTitle("**Now Playing**");
        builder.setThumbnail(track.getInfo().artworkUrl);
        Button pausebutton = KreiscraftBot.getBetterButtonApi().registerButton(Emoji.fromUnicode("U+23EF"), ButtonStyle.PRIMARY, dummy -> player.setPaused(!player.isPaused()));
        Button stopbutton = KreiscraftBot.getBetterButtonApi().registerButton(Emoji.fromUnicode("U+23F9"), ButtonStyle.DANGER, dummy -> {
            guild.getAudioManager().closeAudioConnection();
            player.stopTrack();
            queue.clear();
        });
        Button skipbutton = KreiscraftBot.getBetterButtonApi().registerButton(Emoji.fromUnicode("U+23ED"), ButtonStyle.PRIMARY, dummy -> KreiscraftBot.getPlayerManager().getAudioHandler(guild).skipTrack());
        Button repeatbutton = KreiscraftBot.getBetterButtonApi().registerButton(Emoji.fromUnicode("U+1F501"), ButtonStyle.SUCCESS, dummy -> Sys.info("Repeating"));
        KreiscraftBot.getPlayerManager().getAudioHandler(guild).getChannel().sendMessageEmbeds(builder.build()).addActionRow(pausebutton, stopbutton, skipbutton, repeatbutton).queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        RepeatMode repeatMode = RepeatMode.OFF;
        QueuedTrack clone = new QueuedTrack(track.makeClone(), track.getUserData(RequestMetaData.class));
        switch (endReason) {
            case FINISHED -> {
                switch (repeatMode) {
                    case SONG -> queue.addAt(0, clone);
                    case QUEUE -> queue.add(clone);
                }
                if (queue.isEmpty()) {
                    if (false) {
                        player.setPaused(false);
                        KreiscraftBot.closeAudioConnection(guild);
                    }
                } else {
                    QueuedTrack queuedTrack = queue.pull();
                    audioPlayer.playTrack(queuedTrack.getTrack());
                }
            }
            case REPLACED -> {
                return;
            }
            case STOPPED -> {
                player.setPaused(true);
                KreiscraftBot.getThreadpool().submit(() -> channel.getGuild().getAudioManager().closeAudioConnection());
            }
            case LOAD_FAILED -> channel.sendMessageEmbeds(new EmbedUtil("There was an Error loading this Track", "Track title:" + track.getInfo().title, Color.RED).getBuilder().build()).queue();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        super.onTrackException(player, track, exception);
    }
}
