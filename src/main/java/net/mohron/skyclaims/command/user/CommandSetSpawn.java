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
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandSetSpawn extends CommandBase.PlayerCommand {

  public static final String HELP_TEXT = "set your spawn location for your island.";

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_SET_SPAWN)
        .description(LinearComponents.linear(HELP_TEXT))
        .executor(new CommandSetSpawn())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "setspawn");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSetSpawn");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSetSpawn", e);
    }
  }

  @Override
  public CommandResult execute(Player player, CommandContext args) throws CommandException {
    Island island = IslandManager.getByLocation(player.getLocation())
        .orElseThrow(() -> new CommandException(LinearComponents.linear("You must be on an island to use this command!")));

    if (!island.isManager(player) && !player.hasPermission(Permissions.COMMAND_SET_SPAWN_OTHERS)) {
      throw new CommandException(LinearComponents.linear("Only the island owner may use this command!"));
    }

    island.setSpawn(player.getTransform());
    player.sendMessage(LinearComponents.linear("Your island spawn has been set to ", NamedTextColor.GRAY, "(",
        NamedTextColor.LIGHT_PURPLE, island.getSpawn().getPosition().getFloorX(), NamedTextColor.GRAY, ", ",
        NamedTextColor.LIGHT_PURPLE, island.getSpawn().getPosition().getFloorY(), NamedTextColor.GRAY, ", ",
        NamedTextColor.LIGHT_PURPLE, island.getSpawn().getPosition().getFloorZ(), NamedTextColor.GRAY, ")"
    ));

    return CommandResult.success();
  }
}
