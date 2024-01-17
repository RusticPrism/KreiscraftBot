package de.rusticprism.kreiscraftbot;

import de.donkaos.systensor.Sys;
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
        String splitcommand = interaction.getCommandString().split(" ")[0].replace("/", "");
        if(commands.containsKey(splitcommand)) {
            BotCommand command = commands.get(splitcommand);
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
                    try {subCommandUtil.method().invoke(command, strcommand.split(" "), interaction);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        Sys.error("Error invoking Subcommand Method", e.getCause().getLocalizedMessage());
                    }
                });
            }
        }
        return false;
    }
    public void registerCommand(Guild guild) {
        Reflections reflections = new Reflections("de.rusticprism.kreiscraftbot.commands");
        for(Class<?> clazz : reflections.getTypesAnnotatedWith(CommandInfo.class)) {
            List<CommandCreateAction> commandCreateActions = new ArrayList<>();
            List<SubCommandUtil> sub = new ArrayList<>();
            BotCommand command;
            try {
                command = (BotCommand) clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Sys.error("Error instantiating Class!", e.getCause().getLocalizedMessage());
                return;
            }
            if (command.getClass().getAnnotation(CommandInfo.class) == null) {
                Sys.error("Couldn't find CommandInfo Annotation");
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
            for(Method method : clazz.getMethods()) {
                if(method.getAnnotation(SubCommand.class) == null) {
                    continue;
                }
                if (method.getParameterCount() != 2) {
                    Sys.error("To few/many Parameters provided on Method: " + method.getName());
                    return;
                }
                if (Arrays.stream(method.getParameterTypes()).toList().contains(String[].class) && Arrays.stream(method.getParameterTypes()).toList().contains(SlashCommandInteraction.class)) {
                    SubCommand subCommand = method.getAnnotation(SubCommand.class);
                    sub.add(new SubCommandUtil(method, subCommand));
                    for (CommandCreateAction commandCreateAction : commandCreateActions) {
                        commandCreateAction.addOption(OptionType.STRING, subCommand.command(), subCommand.description());
                    }
                } else Sys.error("Wrong Parameters provided on Method: " + method.getName());
            }
            for (CommandCreateAction commandCreateAction : commandCreateActions) {
                commandCreateAction.queue();
            }
            subcommands.put(command, sub);
        }
    }

    public HashMap<String, BotCommand> getCommands() {
        return commands;
    }

    public HashMap<BotCommand, List<SubCommandUtil>> getSubcommands() {
        return subcommands;
    }
}
