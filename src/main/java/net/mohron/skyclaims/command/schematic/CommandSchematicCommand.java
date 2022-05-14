/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2022 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims.command.schematic;

import java.util.List;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.schematic.IslandSchematic;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandSchematicCommand extends CommandBase {

  public static final String HELP_TEXT = "used to configure schematic commands";
  private static final Text SCHEMATIC = LinearComponents.linear("schematic");
  private static final Text ACTION = LinearComponents.linear("add|remove");
  private static final Text COMMAND = LinearComponents.linear("command");

  private enum Action {
    add, remove
  }

  public static CommandSpec commandSpec = CommandSpec.builder()
      .permission(Permissions.COMMAND_SCHEMATIC_COMMAND)
      .description(LinearComponents.linear(HELP_TEXT))
      .arguments(
          Arguments.schematic(SCHEMATIC),
          GenericArguments.enumValue(ACTION, Action.class),
          GenericArguments.remainingJoinedStrings(COMMAND)
      )
      .executor(new CommandSchematicCommand())
      .build();

  public static void register() {
    try {
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSchematicAddCommand");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSchematicAddCommand", e);
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    IslandSchematic schematic = args.<IslandSchematic>getOne(SCHEMATIC)
        .orElseThrow(() -> new CommandException(LinearComponents.linear(NamedTextColor.RED, "You must provide a schematic argument to use this command!")));
    Action action = args.<Action>getOne(ACTION)
        .orElseThrow(() -> new CommandException(LinearComponents.linear(NamedTextColor.RED, "You must provide an action argument to use this command!")));
    String command = args.<String>getOne(COMMAND)
        .orElseThrow(() -> new CommandException(LinearComponents.linear(NamedTextColor.RED, "You must provide a command argument to use this command!")));

    List<String> commands;
    if (action == Action.add) {
      commands = schematic.getCommands();
      commands.add(command);
      schematic.setCommands(commands);
    } else {
      commands = schematic.getCommands();
      commands.remove(command);
      schematic.setCommands(commands);
    }

    if (PLUGIN.getSchematicManager().save(schematic)) {
      src.sendMessage(LinearComponents.linear(NamedTextColor.GREEN, "Successfully updated schematic."));
      return CommandResult.success();
    } else {
      throw new CommandException(LinearComponents.linear(NamedTextColor.RED, "Failed to update schematic."));
    }
  }
}
