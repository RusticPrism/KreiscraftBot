package de.rusticprism.kreiscraftbot.commands.music;

import de.rusticprism.kreiscraftbot.KreiscraftBot;
import de.rusticprism.kreiscraftbot.commands.api.BotCommand;
import de.rusticprism.kreiscraftbot.commands.api.CommandInfo;
import de.rusticprism.kreiscraftbot.commands.api.SubCommand;
import de.rusticprism.kreiscraftbot.utils.EmbedUtil;
import kotlin.ranges.RangesKt;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.awt.*;

@CommandInfo(command = "volume", description = "The Command to change the Volume")
public class VolumeCommand extends BotCommand {
    @Override
    public void execute(Member member, TextChannel channel, SlashCommandInteraction interaction) {
        interaction.replyEmbeds(new EmbedUtil("The Volume Argument ist required!", Color.RED).getBuilder().build()).queue();
    }
    @SubCommand(command = "volume", description = "The Volume Argument")
    public void volume(String[] args, SlashCommandInteraction interaction) {
        int value;
        try {
           value = Integer.parseInt(args[0]);
        }catch (NumberFormatException e) {
            interaction.replyEmbeds(new EmbedUtil("The Argument has to be of type Integer", Color.RED).getBuilder().build()).queue();
            return;
        }
        if(value > 301 || value < 10) {
            interaction.replyEmbeds(new EmbedUtil("The Volume has to be between 0 and 300", Color.RED).getBuilder().build()).queue();
            return;
        }
        KreiscraftBot.getPlayerManager().getAudioPlayer(interaction.getGuild()).setVolume(value);
    }
}
