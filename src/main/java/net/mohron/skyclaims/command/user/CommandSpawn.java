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

package net.mohron.skyclaims.command.user;

import java.util.Optional;
import java.util.function.Consumer;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandSpawn extends CommandBase.ListIslandCommand {

  public static final String HELP_TEXT = "teleport to an island's spawn point.";

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_SPAWN)
        .description(Text.of(HELP_TEXT))
        .arguments(GenericArguments.optional(Arguments.island(ISLAND)))
        .executor(new CommandSpawn())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "spawn", "tp");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSpawn");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSpawn", e);
    }
  }

  @Override
  public CommandResult execute(Player player, CommandContext args) throws CommandException {
    Optional<Island> island = args.getOne(ISLAND);
    if (island.isPresent()) {
      return sendPlayerToSpawn(player, island.get());
    } else {
      return listIslands(player, this::sendPlayerToSpawn);
    }
  }

  private CommandResult sendPlayerToSpawn(Player player, Island island) throws CommandException {
    if (!canTeleport(player, island)) {
      throw new CommandPermissionException(Text.of(
          TextColors.RED, "You must be trusted on ", island.getName(), " to use this command!"
      ));
    }

    teleport(player, island);

    return CommandResult.success();
  }

  private Consumer<CommandSource> sendPlayerToSpawn(Island island) {
    return src -> {
      if (src instanceof Player) {
        Player player = (Player) src;
        if (canTeleport(player, island)) {
          teleport(player, island);
        } else {
          player.sendMessage(Text.of(
              TextColors.RED, "You must be trusted on ", island.getName(), " to use this command!"
          ));
        }
      }
    };
  }

  private boolean canTeleport(Player player, Island island) {
    return !island.isLocked() || island.isMember(player) || player.hasPermission(Permissions.COMMAND_SPAWN_OTHERS);
  }

  private void teleport(Player player, Island island) {
    PLUGIN.getGame().getScheduler().createTaskBuilder()
        .execute(CommandUtil.createTeleportConsumer(player, island.getSpawn().getLocation()))
        .submit(PLUGIN);
  }
}
