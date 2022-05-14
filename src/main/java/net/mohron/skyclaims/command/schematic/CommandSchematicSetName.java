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
import org.spongepowered.api.text.serializer.TextSerializers;

public class CommandSchematicSetName extends CommandBase {

  public static final String HELP_TEXT = "used to set the name for a schematic";
  private static final Text SCHEMATIC = LinearComponents.linear("schematic");
  private static final Text NAME = LinearComponents.linear("name");

  public static CommandSpec commandSpec = CommandSpec.builder()
      .permission(Permissions.COMMAND_SCHEMATIC_SET_NAME)
      .description(LinearComponents.linear(HELP_TEXT))
      .arguments(
          Arguments.schematic(SCHEMATIC),
          GenericArguments.optional(GenericArguments.text(NAME, TextSerializers.FORMATTING_CODE, true))
      )
      .executor(new CommandSchematicSetName())
      .build();

  public static void register() {
    try {
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSchematicSetName");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSchematicSetName", e);
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    IslandSchematic schematic = args.<IslandSchematic>getOne(SCHEMATIC)
        .orElseThrow(() -> new CommandException(LinearComponents.linear(NamedTextColor.RED, "You must provide a schematic to use this command!")));
    Text text = args.<Text>getOne(NAME).orElse(LinearComponents.linear(schematic.getName()));

    schematic.setText(text);

    if (PLUGIN.getSchematicManager().save(schematic)) {
      src.sendMessage(LinearComponents.linear(
          NamedTextColor.GREEN, "Successfully updated schematic name to ", NamedTextColor.WHITE, text, NamedTextColor.GREEN, "."
      ));
      return CommandResult.success();
    } else {
      throw new CommandException(LinearComponents.linear(NamedTextColor.RED, "Failed to update schematic."));
    }
  }
}
