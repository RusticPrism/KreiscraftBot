package de.rusticprism.kreiscraftbot;

import de.rusticprism.kreiscraftbot.config.Config;
import de.rusticprism.kreiscraftbot.config.ConfigManager;
import de.rusticprism.kreiscraftbot.music.audio.AudioHandler;
import de.rusticprism.kreiscraftbot.music.audio.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {
    private final ScheduledExecutorService threadpool;
    private final PlayerManager manager;

    private boolean shuttingDown = false;

    public List<Activity> activities = List.of(Activity.playing("auf kreiscraft.net")
            , Activity.watching("kreiscraft.net/youtube")
            , Activity.listening("kreiscraft.net/discord")
            , Activity.playing("League of Legends")
            , Activity.playing("Fortnite"));
    private JDA jda;

    public Bot() {
        this.threadpool = Executors.newSingleThreadScheduledExecutor();
        this.manager = new PlayerManager(this);
        this.manager.init();
    }

    public ScheduledExecutorService getThreadpool() {
        return threadpool;
    }

    public PlayerManager getPlayerManager() {
        return manager;
    }

    public JDA getJDA() {
        return jda;
    }

    public void closeAudioConnection(long guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null)
            threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
    }

    public void shutdown() {
        ConfigManager.configs.stream().forEach(Config::saveConfig);
        if (shuttingDown)
            return;
        shuttingDown = true;
        threadpool.shutdownNow();
        if (jda.getStatus() != JDA.Status.SHUTTING_DOWN) {
            jda.getGuilds().forEach(g ->
            {
                g.getAudioManager().closeAudioConnection();
                AudioHandler ah = (AudioHandler) g.getAudioManager().getSendingHandler();
                if (ah != null) {
                    ah.stopAndClear();
                    ah.getPlayer().destroy();
                }
            });
            jda.shutdown();
        }
        System.exit(0);
    }
    public void setJDA(JDA jda) {
        this.jda = jda;
    }


}
