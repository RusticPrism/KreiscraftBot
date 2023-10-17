package de.rusticprism.kreiscraftbot.music;

import net.dv8tion.jda.api.entities.User;

public class RequestMetaData {
    public static final RequestMetaData EMPTY = new RequestMetaData(null);

    public final UserInfo user;

    public RequestMetaData(User user)
    {
        this.user = user == null ? null : new UserInfo(user.getIdLong(), user.getName(), user.getDiscriminator(), user.getEffectiveAvatarUrl());
    }

    public long getOwner()
    {
        return user == null ? 0L : user.id;
    }

    public static class RequestInfo
    {
        public final String query, url;

        private RequestInfo(String query, String url)
        {
            this.query = query;
            this.url = url;
        }
    }

    public static class UserInfo
    {
        public final long id;
        public final String username, discrim, avatar;

        private UserInfo(long id, String username, String discrim, String avatar)
        {
            this.id = id;
            this.username = username;
            this.discrim = discrim;
            this.avatar = avatar;
        }
    }
}
