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
import net.mohron.skyclaims.command.argument.Argument;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
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
public class CommandLock extends CommandBase {

    public static final String HELP_TEXT = "used to prevent untrusted players from visiting to your island.";
    private static final Text ALL = Text.of("all");
    private static final Text ISLAND = Text.of("island");

    public static CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_LOCK)
        .description(Text.of(HELP_TEXT))
        .arguments(GenericArguments.firstParsing(
            GenericArguments
                .optional(GenericArguments.requiringPermission(GenericArguments.literal(ALL, "all"), Permissions.COMMAND_LOCK_OTHERS)),
            GenericArguments.optional(GenericArguments.requiringPermission(Argument.island(ISLAND), Permissions.COMMAND_LOCK_OTHERS))
        ))
        .executor(new CommandLock())
        .build();

    public static void register() {
        try {
            CommandIsland.addSubCommand(commandSpec, "lock");
            PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
            PLUGIN.getLogger().debug("Registered command: CommandLock");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            PLUGIN.getLogger().error("Failed to register command: CommandLock");
        }
    }

    @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (args.hasAny(ISLAND)) {
            return lockIslands(src, args.getAll(ISLAND));
        }
        if (args.hasAny(ALL)) {
            return lockAll(src);
        }
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("You must be a player to run this command!"));
        }
        Player player = (Player) src;
        Optional<Island> island = Island.getByOwner(player.getUniqueId());

        if (!island.isPresent()) {
            throw new CommandException(Text.of("You must have an Island to run this command!"));
        }

        island.get().setLocked(true);

        src.sendMessage(Text.of(TextColors.GREEN, "Your island is now locked!"));
        return CommandResult.success();
    }

    private CommandResult lockIslands(CommandSource src, Collection<UUID> islandsIds) {
        ArrayList<Island> islands = Lists.newArrayList();
        islandsIds.forEach(i -> Island.get(i).ifPresent(islands::add));
        islands.forEach(island -> {
            island.setLocked(true);
            src.sendMessage(Text.of(island.getName(), TextColors.GREEN, " has been locked!"));
        });
        return CommandResult.success();
    }

    private CommandResult lockAll(CommandSource src) {
        SkyClaims.islands.values().forEach(island -> island.setLocked(true));
        src.sendMessage(Text.of(TextColors.GREEN, "All islands have been locked!"));
        return CommandResult.success();
    }
}
