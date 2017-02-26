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

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.argument.Argument;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandCreate implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final String HELP_TEXT = "create an island.";
	private static final Text SCHEMATIC = Text.of("schematic");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_CREATE)
		.description(Text.of(HELP_TEXT))
		.arguments(GenericArguments.optional(Argument.schematic(SCHEMATIC)))
		.executor(new CommandCreate())
		.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandCreate");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandCreate");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Text.of("You must be a player to use this command!"));

		Player player = (Player) src;

		if (Island.hasIsland(player.getUniqueId()))
			throw new CommandException(Text.of("You already have an island!"));

		String schematic = args.<String>getOne(SCHEMATIC).orElse(Options.getDefaultSchematic(player.getUniqueId()));

		player.sendMessage(Text.of("Your island is being created. You will be teleported shortly."));

		try {
			new Island(player, schematic);
		} catch (CreateIslandException e) {
			throw new CommandException(Text.of("Unable to create island! " + e.getMessage()));
		}

		return CommandResult.success();
	}
}