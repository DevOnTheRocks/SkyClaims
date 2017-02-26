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
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;

public class CommandSetup implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final String HELP_TEXT = "used to assist in setting up the plugin";

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_SETUP)
		.description(Text.of(HELP_TEXT))
		.executor(new CommandSetup())
		.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandSetup");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandSetup");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		// TODO Help the src set up required permissions and options

		throw new CommandException(Text.of("Command is not yet implemented."));
		//return CommandResult.empty();
	}
}