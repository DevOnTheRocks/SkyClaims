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

package net.mohron.skyclaims.command.team;

import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.PrivilegeType;
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

public class CommandKick extends CommandBase.IslandCommand {

  public static final String HELP_TEXT = "used to remove players from an island.";
  private static final Text USER = Text.of("user");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_KICK)
        .arguments(GenericArguments.user(USER))
        .description(Text.of(HELP_TEXT))
        .executor(new CommandKick())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "kick");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandKick");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandKick", e);
    }
  }

  @Override
  public CommandResult execute(Player player, Island island, CommandContext args)
      throws CommandException {
    User user = args.<User>getOne(USER).orElse(null);

    if (user == null) {
      throw new CommandException(Text.of(TextColors.RED, "A user argument must be provided."));
    } else if (player.equals(user)) {
      throw new CommandException(Text.of(TextColors.RED, "You cannot kick yourself!"));
    } else if (island.getPrivilegeType(user) == PrivilegeType.NONE) {
      throw new CommandException(Text.of(
          PrivilegeType.NONE.format(user.getName()), TextColors.RED, " is not a member of ",
          island.getName(), TextColors.RED, "!"
      ));
    } else if (island.getPrivilegeType(player).ordinal() >= island.getPrivilegeType(user)
        .ordinal()) {
      throw new CommandException(Text.of(
          TextColors.RED, "You do not have permission to kick ",
          island.getPrivilegeType(user).format(user.getName()),
          " from ", island.getName(), TextColors.RED, "."
      ));
    }

    PrivilegeType type = island.getPrivilegeType(user);
    user.getPlayer().ifPresent(p -> {
      if (island.getPlayers().contains(p) && !p.hasPermission(Permissions.EXEMPT_KICK)) {
        p.setLocationSafely(PLUGIN.getConfig().getWorldConfig().getSpawn());
        p.sendMessage(
            Text.of(TextColors.RED, "You have been removed from ", island.getName(), TextColors.RED,
                "!"));
      }
    });
    island.removeMember(user);

    player.sendMessage(Text.of(
        type.format(user.getName()), TextColors.RED, " has successfully been removed from ",
        island.getName(), TextColors.RED, "!"
    ));

    return CommandResult.success();
  }
}
