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
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class CommandLock extends CommandBase.LockCommand {

    public static final String HELP_TEXT = "used to prevent untrusted players from visiting to your island.";

    public CommandLock() {
        super(true);
    }

    public static void register() {
        CommandSpec commandSpec = CommandSpec.builder()
            .permission(Permissions.COMMAND_LOCK)
            .description(Text.of(HELP_TEXT))
            .arguments(GenericArguments.firstParsing(
                GenericArguments
                    .optional(GenericArguments.requiringPermission(GenericArguments.literal(ALL, "all"), Permissions.COMMAND_LOCK_OTHERS)),
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

    @Override public CommandResult execute(CommandSource src, Island island, CommandContext args) throws CommandException {
        if (src instanceof Player && !island.isManager((Player) src) || !src.hasPermission(Permissions.COMMAND_LOCK_OTHERS)) {
            throw new CommandPermissionException(Text.of(TextColors.RED, "You do not have permission to lock ", island.getName(), "!"));
        }

        island.setLocked(true);

        island.getPlayers().forEach(p -> {
            if (!island.isMember(p) && !p.hasPermission(Permissions.EXEMPT_KICK)) {
                p.setLocationSafely(PLUGIN.getConfig().getWorldConfig().getSpawn());
                p.sendMessage(Text.of(island.getName(), TextColors.RED, " has been locked!"));
            }
        });

        src.sendMessage(Text.of(island.getName(), TextColors.GREEN, " is now locked!"));
        return CommandResult.success();
    }

}
