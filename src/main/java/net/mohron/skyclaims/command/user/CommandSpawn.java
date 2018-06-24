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

import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class CommandSpawn extends CommandBase.PlayerCommand {

  public static final String HELP_TEXT = "teleport to an island's spawn point.";
  private static final Text USER = Text.of("user");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_SPAWN)
        .description(Text.of(HELP_TEXT))
        .arguments(GenericArguments.optional(GenericArguments.user(USER)))
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
    User user = args.<User>getOne(USER).orElse(player);
    Island island = Island.getByOwner(user.getUniqueId())
        .orElseThrow(() -> new CommandException(
            Text.of(TextColors.RED, user.getName(), " must have an Island to use this command!")));

    if (island.isLocked() && !island.isMember(player) && !player
        .hasPermission(Permissions.COMMAND_SPAWN_OTHERS)) {
      throw new CommandException(Text.of(TextColors.RED, "You must be trusted on ", user.getName(),
          "'s island to use this command!"));
    }
    if (PLUGIN.getConfig().getMiscConfig().isClearOnTeleports()) {
      player.getEnderChestInventory().clear();
      player.getInventory().clear();
    }
    PLUGIN.getGame().getScheduler().createTaskBuilder()
        .execute(CommandUtil.createTeleportConsumer(player, island.getSpawn().getLocation()))
        .submit(PLUGIN);

    return CommandResult.success();
  }
}
