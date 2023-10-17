package de.rusticprism.kreiscraftbot.utils;

import de.rusticprism.kreiscraftbot.commands.api.SubCommand;

import java.lang.reflect.Method;

public record SubCommandUtil(Method method, SubCommand subCommand) {
}
