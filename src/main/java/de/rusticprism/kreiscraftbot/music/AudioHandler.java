package de.rusticprism.kreiscraftbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.nio.ByteBuffer;

public class AudioHandler implements AudioSendHandler {
    private AudioFrame lastframe;
    private final AudioPlayer player;
    private TextChannel channel;
    private final Queue queue;
    public AudioHandler(AudioPlayer player) {
        this.player = player;
        this.queue = new Queue();
    }

    public void addTrack(QueuedTrack qtrack, TextChannel channel) {
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
    public void skipTrack() {
        if(queue.isEmpty()) {
            player.stopTrack();
            channel.getGuild().getAudioManager().closeAudioConnection();
            return;
        }
        player.startTrack(queue.pull().getTrack(), false);
    }
    @Override
    public boolean canProvide() {
        lastframe = player.provide();
        return lastframe != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastframe.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    public Queue getQueue() {
        return queue;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public TextChannel getChannel() {
        return channel;
    }
}
