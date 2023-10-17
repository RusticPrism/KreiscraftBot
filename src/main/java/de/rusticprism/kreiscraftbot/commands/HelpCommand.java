package de.rusticprism.kreiscraftbot.commands;

import de.rusticprism.kreiscraftbot.commands.api.Aliases;
import de.rusticprism.kreiscraftbot.commands.api.BotCommand;
import de.rusticprism.kreiscraftbot.commands.api.CommandInfo;
import de.rusticprism.kreiscraftbot.commands.api.SubCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.Arrays;

@CommandInfo(command = "help", description = "A Help Command")
@Aliases("h")
public class HelpCommand extends BotCommand {

    @Override
    public void execute(Member member, TextChannel channel, SlashCommandInteraction interaction) {
        System.out.println("Test");
        interaction.reply("Test").queue();
    }

    @SubCommand(command = "subcommand", description = "subcommand Description")
    public void subcommand(String[] args, SlashCommandInteraction interaction) {
        interaction.reply(Arrays.toString(args)).queue();
    }
}