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
import net.mohron.skyclaims.team.Invite;
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


public class CommandPromote extends CommandBase.IslandCommand {

  public static final String HELP_TEXT = "used to promote a player on an island.";
  private static final Text USER = Text.of("user");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_PROMOTE)
        .arguments(GenericArguments.user(USER))
        .description(Text.of(HELP_TEXT))
        .executor(new CommandPromote())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "promote");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandPromote");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandPromote", e);
    }
  }

  @Override
  public CommandResult execute(Player player, Island island, CommandContext args)
      throws CommandException {
    User user = args.<User>getOne(USER).orElse(null);

    if (user == null) {
      throw new CommandException(Text.of(TextColors.RED, "A user argument must be provided."));
    } else if (player.equals(user)) {
      throw new CommandException(Text.of(TextColors.RED, "You cannot promote yourself!"));
    } else if (!island.isOwner(player)) {
      throw new CommandException(Text.of(TextColors.RED, "You do not have permission to promote players on this island!"));
    } else {
      PrivilegeType type = island.getPrivilegeType(user);
      if (type == PrivilegeType.MANAGER) {
        Invite.builder()
            .island(island)
            .sender(player)
            .receiver(user)
            .privilegeType(type)
            .build()
            .send();
        player.sendMessage(Text.of(
            TextColors.GREEN, "Island ownership transfer request sent to ", type.format(user.getName()), TextColors.GREEN, "."
        ));
      } else {
        island.promote(user);
        player.sendMessage(Text.of(
            type.format(user.getName()), TextColors.GREEN, " has been promoted from a ", type.toText(),
            TextColors.GREEN, " to a ", island.getPrivilegeType(user).toText(), TextColors.GREEN, "."
        ));
      }
    }

    return CommandResult.success();
  }
}
