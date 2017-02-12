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

import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.user.*;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import static net.mohron.skyclaims.PluginInfo.NAME;
import static net.mohron.skyclaims.PluginInfo.VERSION;

public class CommandIsland implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	private static final String HELP_TEXT = String.format("use to run %s's subcommands or display command help info.", PluginInfo.NAME);

	private static final Text HELP = Text.of("help");

	private static CommandSpec commandSpec = CommandSpec.builder()
			.description(Text.of(HELP_TEXT))
			.child(CommandAdmin.commandSpec, "admin")
			.child(CommandCreate.commandSpec, "create")
			.child(CommandExpand.commandSpec, "expand")
			.child(CommandInfo.commandSpec, "info")
			.child(CommandList.commandSpec, "list")
			.child(CommandLock.commandSpec, "lock")
			.child(CommandReset.commandSpec, "reset")
			.child(CommandSetBiome.commandSpec, "setbiome")
			.child(CommandSetSpawn.commandSpec, "setspawn", "sethome")
			.child(CommandSpawn.commandSpec, "spawn", "tp", "home")
			.child(CommandUnlock.commandSpec, "unlock")
			.arguments(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.literal(HELP, "help"))))
			.executor(new CommandIsland())
			.build();

	public static void register() {
		try {
			Sponge.getCommandManager().register(PLUGIN, commandSpec, "skyclaims", "island", "is");
			PLUGIN.getLogger().debug("Registered command: CommandIsland");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandIsland");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		boolean hasPerms = false;

		Text helpContents = Text.of(
			TextColors.WHITE, "SkyClaims utilizes GriefPrevention for world protection and management. Go to ",
			TextColors.YELLOW, "http://bit.ly/mcgpuser",
			TextColors.WHITE, " to learn more."
		);

		if (src.hasPermission(Permissions.COMMAND_CREATE)) {
			helpContents = Text.join(helpContents, Text.of(
					Text.NEW_LINE,
					TextColors.AQUA, Text.builder("is create").onClick(TextActions.runCommand("/is create")),
					TextColors.GRAY, " [schematic]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandCreate.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_EXPAND)) {
			helpContents = Text.join(helpContents, Text.of(
					Text.NEW_LINE,
					TextColors.AQUA, Text.builder("is expand").onClick(TextActions.suggestCommand("/is expand ")),
					TextColors.GRAY, " [blocks]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandExpand.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_INFO)) {
			helpContents = Text.join(helpContents, Text.of(
					Text.NEW_LINE,
					TextColors.AQUA, Text.builder("is info").onClick(TextActions.runCommand("/is info")),
					TextColors.GRAY, " [island uuid]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandInfo.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_LOCK)) {
			helpContents = Text.join(helpContents, Text.of(
					Text.NEW_LINE,
					TextColors.AQUA, Text.builder("is lock").onClick(TextActions.runCommand("/is lock")),
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandLock.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_RESET)) {
			helpContents = Text.join(helpContents, Text.of(
					Text.NEW_LINE,
					TextColors.AQUA, Text.builder("is reset").onClick(TextActions.runCommand("/is reset")),
					TextColors.GRAY, " [schematic]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandReset.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_SET_BIOME)) {
			helpContents = Text.join(helpContents, Text.of(
					Text.NEW_LINE,
					TextColors.AQUA, Text.builder("is setbiome").onClick(TextActions.suggestCommand("/is setbiome ")),
					TextColors.GOLD, " <biome>",
					TextColors.GRAY, " [target]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandSetBiome.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_SET_SPAWN)) {
			helpContents = Text.join(helpContents, Text.of(
					Text.NEW_LINE,
					TextColors.AQUA, Text.builder("is setspawn").onClick(TextActions.runCommand("/is setspawn")),
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandSetSpawn.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_SPAWN)) {
			helpContents = Text.join(helpContents, Text.of(
					Text.NEW_LINE,
					TextColors.AQUA, Text.builder("is spawn").onClick(TextActions.runCommand("/is spawn")),
					TextColors.GRAY, " [player]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandSpawn.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_LOCK)) {
			helpContents = Text.join(helpContents, Text.of(
					Text.NEW_LINE,
					TextColors.AQUA, Text.builder("is unlock").onClick(TextActions.runCommand("/is unlock")),
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandUnlock.HELP_TEXT));
			hasPerms = true;
		}

		if (hasPerms) {
			PaginationList.Builder paginationBuilder = PaginationList.builder()
					.title(Text.of(TextColors.AQUA, NAME, " Help"))
					.padding(Text.of(TextColors.AQUA, "-"))
					.contents(helpContents);
			paginationBuilder.sendTo(src);
		} else {
			src.sendMessage(Text.of(NAME + " " + VERSION));
		}

		return CommandResult.success();
	}
}