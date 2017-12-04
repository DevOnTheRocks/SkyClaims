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
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class CommandDelete extends CommandBase.IslandCommand {

  public static final String HELP_TEXT = "used to permanently delete an island.";
  private static final Text CLEAR = Text.of("clear");
  private static final Text ISLAND = Text.of("island");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_DELETE)
        .description(Text.of(HELP_TEXT))
        .arguments(
            Arguments.island(ISLAND, PrivilegeType.OWNER),
            GenericArguments.optional(GenericArguments
                .requiringPermission(GenericArguments.bool(CLEAR),
                    Permissions.COMMAND_DELETE_OTHERS))
        )
        .executor(new CommandDelete())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "delete");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandDelete");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandDelete", e);
    }
  }

  @Override
  public CommandResult execute(Player player, Island island, CommandContext args)
      throws CommandException {
    if (!island.isOwner(player) && !player.hasPermission(Permissions.COMMAND_DELETE_OTHERS)) {
      throw new CommandPermissionException(
          Text.of(TextColors.RED, "You do not have permission to delete ", island.getName(), "!"));
    }

    boolean clear = args.<Boolean>getOne(CLEAR).orElse(true);
    if (clear) {
      island.clear();
    }
    island.getPlayers()
        .forEach(p -> p.setLocationSafely(PLUGIN.getConfig().getWorldConfig().getSpawn()));
    island.delete();

    player.sendMessage(Text.of(island.getName(), TextColors.GREEN, " has been deleted!"));
    return CommandResult.success();
  }
}
