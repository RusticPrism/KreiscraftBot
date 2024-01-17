package de.rusticprism.kreiscraftbot;

import de.donkaos.systensor.Sys;
import de.rusticprism.bba.BetterButtonApi;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class KreiscraftBot {
    private static JDA jda;
    private static CommandManager commandManager;
    private static ScheduledExecutorService threadpool;
    private static PlayerManager playerManager;
    private static BetterButtonApi betterButtonApi;

    public KreiscraftBot(String token) {
        Sys.setPrefix("KREISCRAFTBOT");
        Sys.setDebugging(false);
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
            betterButtonApi = new BetterButtonApi(jda);
        } catch (IllegalArgumentException ex) {
            Sys.error("Error while logging in!", ex.getCause().getLocalizedMessage());
            System.exit(1);
        }
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

    public static BetterButtonApi getBetterButtonApi() {
        return betterButtonApi;
    }
}
