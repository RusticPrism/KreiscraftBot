package de.rusticprism.kreiscraftbot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.rusticprism.kreiscraftbot.utils.FormatUtil;
import net.dv8tion.jda.api.entities.User;

public class QueuedTrack {
    private final AudioTrack track;

    public QueuedTrack(AudioTrack track, User owner)
    {
        this(track, new RequestMetaData(owner));
    }

    public QueuedTrack(AudioTrack track, RequestMetaData rm)
    {
        this.track = track;
        this.track.setUserData(rm);
    }

    public long getIdentifier()
    {
        return track.getUserData(RequestMetaData.class).getOwner();
    }

    public AudioTrack getTrack()
    {
        return track;
    }

    @Override
    public String toString()
    {
        return "`[" + FormatUtil.formatTime(track.getDuration()) + "]` [**" + track.getInfo().title + "**]("+track.getInfo().uri+") - <@" + track.getUserData(RequestMetaData.class).getOwner() + ">";
    }
}
