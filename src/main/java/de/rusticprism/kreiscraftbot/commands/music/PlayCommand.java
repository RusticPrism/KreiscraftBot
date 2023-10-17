package de.rusticprism.kreiscraftbot.commands.music;

import de.rusticprism.kreiscraftbot.KreiscraftBot;
import de.rusticprism.kreiscraftbot.commands.api.BotCommand;
import de.rusticprism.kreiscraftbot.commands.api.CommandInfo;
import de.rusticprism.kreiscraftbot.commands.api.SubCommand;
import de.rusticprism.kreiscraftbot.music.AudioHandler;
import de.rusticprism.kreiscraftbot.music.events.ResultHandler;
import de.rusticprism.kreiscraftbot.utils.EmbedUtil;
import de.rusticprism.kreiscraftbot.utils.FormatUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@CommandInfo(command = "play", description = "The command to play songs in your voice channel")
public class PlayCommand extends BotCommand {
    @Override
    public void execute(Member member, TextChannel channel, SlashCommandInteraction interaction) {
        interaction.replyEmbeds(new EmbedUtil("Song or Playlist Argument Required (Link or Search)", Color.RED).getBuilder().build()).queue();
    }
    @SubCommand(command = "query", description = "The Search Query Required for that Command")
    public void search(String[] args, @NotNull SlashCommandInteraction interaction) {
        String url = "";
        if(args.length == 1) {
            if(FormatUtil.isUrl(args[0])) {
                url = args[0];
            }else url = "ytsearch: " + url;
        }else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) stringBuilder.append(args[i]).append(" ");
            url = "ytsearch: " + stringBuilder.toString().trim();
        }
        KreiscraftBot.getPlayerManager().loadItemOrdered(interaction.getGuild(),url, new ResultHandler(interaction));
    }
}
