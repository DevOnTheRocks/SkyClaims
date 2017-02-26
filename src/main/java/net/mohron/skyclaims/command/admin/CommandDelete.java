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

package net.mohron.skyclaims.command.admin;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public class CommandDelete implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final String HELP_TEXT = "used to delete a player's island";
	private static final Text CLEAR = Text.of("clear");
	private static final Text USER = Text.of("user");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_DELETE)
		.description(Text.of(HELP_TEXT))
		.arguments(GenericArguments.user(USER), GenericArguments.optional(GenericArguments.bool(CLEAR)))
		.executor(new CommandDelete())
		.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandDelete");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandDelete");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		User user = args.<User>getOne(USER)
			.orElseThrow(() -> new CommandException(Text.of("Invalid user")));
		Island island = Island.getByOwner(user.getUniqueId())
			.orElseThrow(() -> new CommandException(Text.of("Invalid island")));

		boolean clear = args.<Boolean>getOne(CLEAR).orElse(true);
		if (clear) island.clear();
		island.delete();

		src.sendMessage(Text.of(island.getOwnerName(), "'s island has been deleted!"));
		return CommandResult.success();
	}
}