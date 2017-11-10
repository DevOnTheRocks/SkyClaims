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

package net.mohron.skyclaims.command;

import com.google.common.collect.Lists;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.List;
import java.util.UUID;

@NonnullByDefault
public abstract class CommandBase implements CommandExecutor {

    protected static final SkyClaims PLUGIN = SkyClaims.getInstance();

    public static abstract class IslandCommand extends CommandBase implements CommandRequirement.RequiresIsland {

        protected static final Text ISLAND = Text.of("island");

        @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (!(src instanceof Player)) {
                throw new CommandException(Text.of(TextColors.RED, "This command can only be used by a player!"));
            }
            if (!args.hasAny(ISLAND)) {
                return execute(
                    (Player) src,
                    IslandManager.get(((Player) src).getLocation())
                        .orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must be on an island to use this command."))),
                    args);
            } else {
                List<Island> islands = Lists.newArrayList();
                args.<UUID>getAll(ISLAND).forEach(i -> IslandManager.get(i).ifPresent(islands::add));
                if (islands.size() == 1) {
                    return execute((Player) src, islands.get(0), args);
                } else {
                    throw new CommandException(Text.of(TextColors.RED, "Multiple island support not yet implemented!"));
                }
            }
        }
    }

    public static abstract class PlayerCommand extends CommandBase implements CommandRequirement.RequiresPlayer {

        @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (src instanceof Player) {
                return execute((Player) src, args);
            }
            throw new CommandException(Text.of(TextColors.RED, "This command can only be used by a player!"));
        }
    }

}
