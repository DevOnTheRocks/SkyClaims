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
import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.admin.CommandConfig;
import net.mohron.skyclaims.command.admin.CommandCreateSchematic;
import net.mohron.skyclaims.command.admin.CommandDelete;
import net.mohron.skyclaims.command.admin.CommandReload;
import net.mohron.skyclaims.command.admin.CommandSetup;
import net.mohron.skyclaims.command.admin.CommandTransfer;
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
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;

import static net.mohron.skyclaims.PluginInfo.NAME;
import static net.mohron.skyclaims.PluginInfo.VERSION;

public class CommandAdmin implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final String HELP_TEXT = String.format("use to run %s's admin commands or display help info", PluginInfo.NAME);
	private static final Text HELP = Text.of("help");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_ADMIN)
		.description(Text.of(HELP_TEXT))
		.child(CommandConfig.commandSpec, "config")
		.child(CommandDelete.commandSpec, "delete")
		.child(CommandReload.commandSpec, "reload")
		.child(CommandSetup.commandSpec, "setup")
		.child(CommandCreateSchematic.commandSpec, "createschematic", "cs")
		.child(CommandTransfer.commandSpec, "transfer")
		.arguments(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.literal(HELP, "help"))))
		.executor(new CommandAdmin())
		.build();

	public static void register() {
		try {
			Sponge.getCommandManager().register(PLUGIN, commandSpec, "isa");
			PLUGIN.getLogger().debug("Registered command: CommandAdmin");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandAdmin");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> helpText = Lists.newArrayList();
		boolean hasPerms = false;

		if (src.hasPermission(Permissions.COMMAND_CONFIG)) {
			helpText.add(Text.of(
				TextColors.AQUA, Text.builder("isa config").onClick(TextActions.runCommand("/isa config")),
				TextColors.DARK_GRAY, " - ",
				TextColors.DARK_GREEN, CommandConfig.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_CREATE_SCHEMATIC)) {
			helpText.add(Text.of(
				TextColors.AQUA, "isa cs",
				TextColors.GOLD, " <name>",
				TextColors.DARK_GRAY, " - ",
				TextColors.DARK_GREEN, CommandCreateSchematic.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_DELETE)) {
			helpText.add(Text.of(
				TextColors.AQUA, "isa delete",
				TextColors.GOLD, " <player>",
				TextColors.GRAY, " [clear]",
				TextColors.DARK_GRAY, " - ",
				TextColors.DARK_GREEN, CommandDelete.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_RELOAD)) {
			helpText.add(Text.of(
				TextColors.AQUA, Text.builder("isa reload").onClick(TextActions.runCommand("/isa reload")),
				TextColors.DARK_GRAY, " - ",
				TextColors.DARK_GREEN, CommandReload.HELP_TEXT));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_TRANSFER)) {
			helpText.add(Text.of(
				TextColors.AQUA, "isa transfer",
				TextColors.GRAY, " [owner]",
				TextColors.GOLD, " <player>",
				TextColors.DARK_GRAY, " - ",
				TextColors.DARK_GREEN, CommandDelete.HELP_TEXT));
			hasPerms = true;
		}

		if (hasPerms) {
			PaginationList.Builder paginationBuilder = PaginationList.builder()
				.title(Text.of(TextColors.AQUA, NAME, " Admin Help"))
				.padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
				.contents(helpText);
			paginationBuilder.sendTo(src);
		} else {
			src.sendMessage(Text.of(NAME + " " + VERSION));
		}

		return CommandResult.success();
	}
}