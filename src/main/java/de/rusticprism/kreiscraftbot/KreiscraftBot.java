package de.rusticprism.kreiscraftbot;

import de.rusticprism.kreiscraftbot.listener.ReadyListener;
import de.rusticprism.kreiscraftbot.listener.SlashCommandListener;
import de.rusticprism.kreiscraftbot.music.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class KreiscraftBot {
    private static Logger logger = null;
    private static JDA jda;
    private static CommandManager commandManager;
    private static ScheduledExecutorService threadpool;
    private static PlayerManager playerManager;

    public KreiscraftBot(String token) {
        logger = LoggerFactory.getLogger("KreiscraftBot");
        commandManager = new CommandManager();
        threadpool = Executors.newSingleThreadScheduledExecutor();
        playerManager = new PlayerManager();
        try {
            jda = JDABuilder.create(token, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.ONLINE_STATUS, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                    .setActivity(Activity.listening(" B-tight"))
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(new ReadyListener(), new SlashCommandListener())
                    .setBulkDeleteSplittingEnabled(false)
                    .build();

        } catch (IllegalArgumentException ex) {
            logger.error("Error while logging in!", ex.getCause());
            System.exit(1);
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static JDA getJda() {
        return jda;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static ScheduledExecutorService getThreadpool() {
        return threadpool;
    }
    public static void closeAudioConnection(Guild guild) {
        threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
    }
    public static PlayerManager getPlayerManager() {
        return playerManager;
    }
}
