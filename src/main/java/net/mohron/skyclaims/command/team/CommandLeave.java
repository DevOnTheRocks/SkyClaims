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
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.function.Consumer;

@NonnullByDefault
public class CommandLeave extends CommandBase.IslandCommand {

    public static final String HELP_TEXT = "used to leave an island.";

    public static CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_LEAVE)
        .description(Text.of(HELP_TEXT))
        .executor(new CommandLeave())
        .build();

    public static void register() {
        try {
            CommandIsland.addSubCommand(commandSpec, "leave");
            PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
            PLUGIN.getLogger().debug("Registered command: CommandLeave");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            PLUGIN.getLogger().error("Failed to register command: CommandLeave");
        }
    }

    @Override public CommandResult execute(Player player, Island island, CommandContext args) throws CommandException {

        if (island.isOwner(player)) {
            throw new CommandException(Text.of(TextColors.RED, "You must transfer island ownership before leaving."));
        } else if (!island.isMember(player)) {
            throw new CommandException(Text.of(TextColors.RED, "You are not a member of ", island.getName(), TextColors.RED, "!"));
        }

        player.sendMessage(Text.of(
            "Are you sure you want to leave ",
            island.getName(),
            TextColors.RESET, "?", Text.NEW_LINE,
            TextColors.WHITE, "[",
            Text.builder("YES")
                .color(TextColors.GREEN)
                .onClick(TextActions.executeCallback(leaveIsland(player, island))),
            TextColors.WHITE, "] [",
            Text.builder("NO")
                .color(TextColors.RED)
                .onClick(TextActions.executeCallback(s -> s.sendMessage(Text.of("Leave island canceled!")))),
            TextColors.WHITE, "]"
        ));

        return CommandResult.success();
    }

    private Consumer<CommandSource> leaveIsland(Player player, Island island) {
        return src -> {
            if (island.getPlayers().contains(player)) {
                player.setLocationSafely(PLUGIN.getConfig().getWorldConfig().getSpawn());
            }

            island.removeMember(player);
            player.sendMessage(Text.of(TextColors.RED, "You have been removed from ", island.getName(), TextColors.RED, "!"));
        };
    }
}
