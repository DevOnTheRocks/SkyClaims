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

package net.mohron.skyclaims.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.exceptions.NucleusException;
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
import org.spongepowered.api.text.format.TextColors;

public class CommandSetHome extends CommandBase {

    public static final String HELP_TEXT = "set your island home.";

    public static CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_SET_HOME)
        .description(Text.of(HELP_TEXT))
        .executor(new CommandSetHome())
        .build();

    public static void register() {
        try {
            CommandIsland.addSubCommand(commandSpec, "sethome");
            PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
            PLUGIN.getLogger().debug("Registered command: CommandSetHome");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            PLUGIN.getLogger().error("Failed to register command: CommandSetHome");
        }
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("You must be a player to use this command!"));
        }

        Player player = (Player) src;
        Island island = Island.get(player.getLocation())
            .orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must be on an island to set a home!")));

        if (!island.isMember(player)) {
            throw new CommandException(Text.of(TextColors.RED, "You must have permission to set home on this island!"));
        }

        boolean success = modifyOrCreateHome(player);
        if (!success) {
            throw new CommandException(Text.of(TextColors.RED, "An error was encountered while attempting to set your home!"));
        }

        return CommandResult.success();
    }


    private boolean modifyOrCreateHome(Player player) {
        try {
            NucleusIntegration.getHomeService().modifyOrCreateHome(PLUGIN.getCause(), player, "Island", player.getLocation(), player.getRotation());
            player.sendMessage(Text.of(TextColors.GREEN, "Your home has been set!"));
            return true;
        } catch (NucleusException e) {
            player.sendMessage(Text.of(TextColors.RED, "An error was encountered while attempting to set your home!"));
            PLUGIN.getGame().getServer().getConsole().sendMessage(e.getText());
            return false;
        }
    }
}
