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

package net.mohron.skyclaims.command.user;

import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

public class CommandSetName extends CommandBase.IslandCommand {

  public static final String HELP_TEXT = "set your name of your island.";
  private static final Text NAME = Text.of("name");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_SET_NAME)
        .description(Text.of(HELP_TEXT))
        .arguments(
            GenericArguments.optionalWeak(Arguments.island(ISLAND, PrivilegeType.MANAGER)),
            GenericArguments.text(NAME, TextSerializers.FORMATTING_CODE, true)
        )
        .executor(new CommandSetName())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "setname");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSetName");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSetName", e);
    }
  }

  @Override
  public CommandResult execute(Player player, Island island, CommandContext args) throws CommandException {
    if (!island.isManager(player) && !player.hasPermission(Permissions.COMMAND_SET_NAME_OTHERS)) {
      throw new CommandException(Text.of(
          TextColors.RED, "Only an island ", PrivilegeType.MANAGER.toText(), TextColors.RED, " may use this command!"
      ));
    }

    Text name = args.<Text>getOne(NAME).orElse(null);
    island.setName(name);
    player.sendMessage(Text.of(TextColors.GREEN, "Your island name has been changed to ", island.getName()));

    return CommandResult.success();
  }
}
