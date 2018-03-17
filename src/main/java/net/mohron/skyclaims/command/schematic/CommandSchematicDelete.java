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
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.schematic.IslandSchematic;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class CommandSchematicDelete extends CommandBase {

  public static final String HELP_TEXT = "deletes a schematic";
  private static final Text SCHEMATIC = Text.of("schematic");

  public static CommandSpec commandSpec = CommandSpec.builder()
      .permission(Permissions.COMMAND_SCHEMATIC_DELETE)
      .description(Text.of(HELP_TEXT))
      .arguments(Arguments.schematic(SCHEMATIC))
      .executor(new CommandSchematicDelete())
      .build();

  public static void register() {
    try {
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSchematicDelete");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSchematicDelete", e);
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    IslandSchematic schematic = args.<IslandSchematic>getOne(SCHEMATIC)
        .orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must provide a schematic to use this command!")));

    if (PLUGIN.getSchematicManager().delete(schematic)) {
      src.sendMessage(Text.of(TextColors.GREEN, "Successfully deleted schematic."));
      return CommandResult.success();
    } else {
      throw new CommandException(Text.of(TextColors.RED, "Failed to delete schematic."));
    }
  }
}
