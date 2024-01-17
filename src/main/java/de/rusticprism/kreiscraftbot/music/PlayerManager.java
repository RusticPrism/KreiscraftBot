package de.rusticprism.kreiscraftbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import de.rusticprism.kreiscraftbot.KreiscraftBot;
import de.rusticprism.kreiscraftbot.music.events.AudioEvents;
import net.dv8tion.jda.api.entities.Guild;

public class PlayerManager extends DefaultAudioPlayerManager {
    public void init() {
        AudioSourceManagers.registerRemoteSources(this);
        AudioSourceManagers.registerLocalSource(this);
        source(YoutubeAudioSourceManager.class).setPlaylistPageCount(10);
        for (Guild guild : KreiscraftBot.getJda().getGuilds()) {
            createPlayer(guild);
        }
    }
    public AudioHandler getAudioHandler(Guild guild) {
        if(guild.getAudioManager().getSendingHandler() instanceof AudioHandler) {
            return (AudioHandler) guild.getAudioManager().getSendingHandler();
        }else return new AudioHandler(createPlayer(guild));
    }
    public AudioPlayer getAudioPlayer(Guild guild) {
        if(guild.getAudioManager().getSendingHandler() instanceof AudioHandler) {
            return ((AudioHandler) guild.getAudioManager().getSendingHandler()).getPlayer();
        }else return createPlayer(guild);
    }

    public AudioPlayer createPlayer(Guild guild) {
        guild.getAudioManager().setSelfDeafened(true);
        AudioPlayer player = super.createPlayer();
        player.addListener(new AudioEvents(this, guild, player));
        player.setVolume(100);
        AudioHandler audioHandler = new AudioHandler(player);
        guild.getAudioManager().setSendingHandler(audioHandler);
        return player;
    }
}
