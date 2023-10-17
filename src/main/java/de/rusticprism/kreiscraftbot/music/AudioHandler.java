package de.rusticprism.kreiscraftbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class AudioHandler implements AudioSendHandler {
    private AudioFrame lastframe;
    private final AudioPlayer player;
    private Channel channel;
    private final Queue queue;
    public AudioHandler(AudioPlayer player) {
        this.player = player;
        this.queue = new Queue();
    }

    public void addTrack(QueuedTrack qtrack, Channel channel) {
        this.channel = channel;
        if (player.getPlayingTrack() == null && queue.isEmpty()) {
            player.playTrack(qtrack.getTrack());
        } else if (player.getPlayingTrack() == null) {
            player.playTrack(queue.pull().getTrack());
            queue.add(qtrack);
        } else {
            queue.add(qtrack);
        }
    }
    @Override
    public boolean canProvide() {
        lastframe = player.provide();
        return lastframe != null;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastframe.getData());
    }

    @Override
    public boolean isOpus() {
        return AudioSendHandler.super.isOpus();
    }
}
