package de.rusticprism.kreiscraftbot;

import de.rusticprism.kreiscraftbot.commands.api.Aliases;
import de.rusticprism.kreiscraftbot.commands.api.BotCommand;
import de.rusticprism.kreiscraftbot.commands.api.CommandInfo;
import de.rusticprism.kreiscraftbot.commands.api.SubCommand;
import de.rusticprism.kreiscraftbot.utils.SubCommandUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandManager {
    private final HashMap<String, BotCommand> commands;
    private final HashMap<BotCommand, List<SubCommandUtil>> subcommands;
    public CommandManager() {
        commands = new HashMap<>();
        subcommands = new HashMap<>();
    }

    public boolean performCommand(SlashCommandInteraction interaction) {
        System.out.println(interaction.getCommandString());
        if(commands.containsKey(interaction.getFullCommandName())) {
            BotCommand command = commands.get(interaction.getFullCommandName());
            if(interaction.getCommandString().split(" ").length == 1) {
                command.execute(interaction.getMember(), interaction.getChannel().asTextChannel(), interaction);
                return true;
            }
            if(subcommands.containsKey(command)) {
                List<SubCommandUtil> subcommands = this.subcommands.get(command);
                subcommands.forEach(subCommandUtil -> {
                    String strcommand = interaction.getCommandString();
                    strcommand = strcommand.replace(subCommandUtil.subCommand().command() + ": ", "");
                    strcommand = strcommand.replace("/" + interaction.getFullCommandName() + " ", "");
                    System.out.println(subCommandUtil.method().getName());
                    try {
                        subCommandUtil.method().invoke(command, strcommand.split(" "), interaction);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        KreiscraftBot.getLogger().error("Error invoking Subcommand Method", e.getCause());
                    }
                });
            }
        }
        return true;
    }
    public void registerCommand(Guild guild) {
        List<CommandCreateAction> commandCreateActions = new ArrayList<>();
        Reflections reflections = new Reflections("de.rusticprism.kreiscraftbot.commands");
        reflections.getTypesAnnotatedWith(CommandInfo.class).forEach(clazz -> {
            BotCommand command;
            try {
                command = (BotCommand) clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                KreiscraftBot.getLogger().error("Error instantiating Class!", e.getCause());
                return;
            }
            if (command.getClass().getAnnotation(CommandInfo.class) == null) {
                KreiscraftBot.getLogger().error("Couldn't find CommandInfo Annotation");
                return;
            }
            if (command.getClass().getAnnotation(Aliases.class) != null) {
                String[] aliases = command.getClass().getAnnotation(Aliases.class).value();
                for (String alias : aliases) {
                    commands.put(alias, command);
                }
            }
            commands.put(command.getClass().getAnnotation(CommandInfo.class).command(), command);
            commandCreateActions.add(guild.upsertCommand(command.getClass().getAnnotation(CommandInfo.class).command(), command.getClass().getAnnotation(CommandInfo.class).description()));
            List<SubCommandUtil> sub = new ArrayList<>();
            for(Method method : clazz.getMethods()) {
                if(method.getAnnotation(SubCommand.class) == null) {
                    continue;
                }
                if (method.getParameterCount() != 2) {
                    KreiscraftBot.getLogger().error("To few/many Parameters provided on Method: " + method.getName());
                    return;
                }
                if (Arrays.stream(method.getParameterTypes()).toList().contains(String[].class) && Arrays.stream(method.getParameterTypes()).toList().contains(SlashCommandInteraction.class)) {
                    SubCommand subCommand = method.getAnnotation(SubCommand.class);
                    sub.add(new SubCommandUtil(method, subCommand));
                    for (CommandCreateAction commandCreateAction : commandCreateActions) {
                        commandCreateAction.addOption(OptionType.STRING, subCommand.command(), subCommand.description());
                    }
                } else KreiscraftBot.getLogger().error("Wrong Parameters provided on Method: " + method.getName());
            }
            for (CommandCreateAction commandCreateAction : commandCreateActions) {
                commandCreateAction.queue();
            }
            subcommands.put(command, sub);
        });
    }
}
