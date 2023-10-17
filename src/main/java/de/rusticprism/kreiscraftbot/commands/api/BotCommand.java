package de.rusticprism.kreiscraftbot.commands.api;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public abstract class BotCommand {
    public abstract void execute(Member member, TextChannel channel, SlashCommandInteraction interaction);
}
