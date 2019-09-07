/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
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

import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandSchematic extends CommandBase {

  public static final String HELP_TEXT = "used to manage island schematics";

  public static CommandSpec commandSpec;

  public static void register() {
    commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_SCHEMATIC)
        .description(Text.of(HELP_TEXT))
        .child(CommandSchematicCommand.commandSpec, "command")
        .child(CommandSchematicCreate.commandSpec, "create")
        .child(CommandSchematicDelete.commandSpec, "delete")
        .child(CommandSchematicInfo.commandSpec, "info")
        .child(CommandSchematicList.commandSpec, "list")
        .child(CommandSchematicSetBiome.commandSpec, "setbiome")
        .child(CommandSchematicSetHeight.commandSpec, "setheight")
        .child(CommandSchematicSetIcon.commandSpec, "seticon")
        .child(CommandSchematicSetName.commandSpec, "setname")
        .child(CommandSchematicSetPreset.commandSpec, "setpreset")
        .childArgumentParseExceptionFallback(false)
        .executor(new CommandSchematicList())
        .build();

    try {
      registerSubCommands();
      CommandIsland.addSubCommand(commandSpec, "schematic");
      Sponge.getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSchematic");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSchematic", e);
    }
  }

  private static void registerSubCommands() {
    CommandSchematicCommand.register();
    CommandSchematicCreate.register();
    CommandSchematicDelete.register();
    CommandSchematicInfo.register();
    CommandSchematicList.register();
    CommandSchematicSetBiome.register();
    CommandSchematicSetHeight.register();
    CommandSchematicSetIcon.register();
    CommandSchematicSetName.register();
    CommandSchematicSetPreset.register();
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    return CommandResult.success();
  }
}
