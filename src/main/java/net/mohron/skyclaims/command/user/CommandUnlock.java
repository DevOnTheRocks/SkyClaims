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

import com.google.common.collect.Lists;
import net.mohron.skyclaims.SkyClaims;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@NonnullByDefault
public class CommandUnlock extends CommandBase.LockCommand {

    public static final String HELP_TEXT = "used to allow untrusted players to visit your island.";

    public CommandUnlock() {
        super(false);
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
            .executor(new CommandUnlock())
            .build();

        try {
            CommandIsland.addSubCommand(commandSpec, "unlock");
            PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
            PLUGIN.getLogger().debug("Registered command: CommandUnlock");
        } catch (UnsupportedOperationException e) {
            PLUGIN.getLogger().error("Failed to register command: CommandUnlock", e);
        }
    }

    @Override public CommandResult execute(CommandSource src, Island island, CommandContext args) throws CommandException {
        if (src instanceof Player && !island.isManager((Player) src) || !src.hasPermission(Permissions.COMMAND_LOCK_OTHERS)) {
            throw new CommandPermissionException(Text.of(TextColors.RED, "You do not have permission to unlock ", island.getName(), "!"));
        }

        island.setLocked(false);

        src.sendMessage(Text.of(island.getName(), TextColors.GREEN, " is now unlocked!"));
        return CommandResult.success();
    }
}
