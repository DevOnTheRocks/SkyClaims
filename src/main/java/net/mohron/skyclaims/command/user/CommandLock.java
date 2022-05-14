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
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandLock extends CommandBase.LockCommand {

  public static final String HELP_TEXT = "used to prevent untrusted players from visiting to your island.";

  public CommandLock() {
    super(true);
  }

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_LOCK)
        .description(LinearComponents.linear(HELP_TEXT))
        .arguments(GenericArguments.firstParsing(
            GenericArguments.optional(GenericArguments.requiringPermission(GenericArguments.literal(ALL, "all"), Permissions.COMMAND_LOCK_OTHERS)),
            GenericArguments.optional(Arguments.island(ISLAND, PrivilegeType.MANAGER))
        ))
        .executor(new CommandLock())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "lock");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandLock");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandLock", e);
    }
  }

  @Override
  public CommandResult execute(CommandSource src, Island island, CommandContext args) throws CommandException {
    island.setLocked(true);

    island.getPlayers().forEach(p -> {
      if (!island.isMember(p) && !p.hasPermission(Permissions.EXEMPT_KICK)) {
        p.setLocationSafely(PLUGIN.getConfig().getWorldConfig().getSpawn());
        p.sendMessage(LinearComponents.linear(island.getName(), NamedTextColor.RED, " has been locked!"));
      }
    });

    src.sendMessage(LinearComponents.linear(island.getName(), NamedTextColor.GREEN, " is now locked!"));
    return CommandResult.success();
  }

}
