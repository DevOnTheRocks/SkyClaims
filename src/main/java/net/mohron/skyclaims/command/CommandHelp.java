package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.lib.Arguments;
import net.mohron.skyclaims.lib.Permissions;
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

public class CommandHelp implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static String helpText = String.format("display info on %s's commands and their uses.", NAME);
	public static CommandSpec commandSpec = CommandSpec.builder()
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(GenericArguments.literal(Arguments.SUBCOMMAND, "admin")))
			.executor(new CommandHelp())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandHelp");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandHelp");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Text helpContents = Text.EMPTY;
		boolean hasPerms = false;

		if (args.hasAny(Arguments.SUBCOMMAND)) {
//			if (src.hasPermission(Permissions.COMMAND_ADMIN)) {
//				helpContents = Text.join(helpContents, Text.builder("is admin").append(Text.of(TextColors.DARK_GREEN, " - ", CommandAdmin.helpText, "\n")).build());
//				hasPerms = true;
//			}

			if (src.hasPermission(Permissions.COMMAND_CREATE_SCHEMATIC)) {
				helpContents = Text.join(helpContents, Text.of(
						TextColors.AQUA, Text.builder("is admin cs").onClick(TextActions.runCommand("/is admin cs")),
						TextColors.DARK_GRAY, " - ",
						TextColors.DARK_GREEN, CommandCreateSchematic.helpText));
				hasPerms = true;
			}

		} else {

			if (src.hasPermission(Permissions.COMMAND_CREATE)) {
				helpContents = Text.join(helpContents, Text.of(
						TextColors.AQUA, Text.builder("is create").onClick(TextActions.runCommand("/is create")),
						TextColors.GRAY, " [schematic]",
						TextColors.DARK_GRAY, " - ",
						TextColors.DARK_GREEN, CommandCreate.helpText));
				hasPerms = true;
			}

			if (src.hasPermission(Permissions.COMMAND_INFO)) {
				helpContents = Text.join(helpContents, Text.of(
						(hasPerms) ? "\n" : "",
						TextColors.AQUA, Text.builder("is info").onClick(TextActions.runCommand("/is info")),
						TextColors.GRAY, " [player]",
						TextColors.DARK_GRAY, " - ",
						TextColors.DARK_GREEN, CommandInfo.helpText));
				hasPerms = true;
			}

			if (src.hasPermission(Permissions.COMMAND_RESET)) {
				helpContents = Text.join(helpContents, Text.of(
						(hasPerms) ? "\n" : "",
						TextColors.AQUA, Text.builder("is reset").onClick(TextActions.runCommand("/is reset")),
						TextColors.GRAY, " [schematic]",
						TextColors.DARK_GRAY, " - ",
						TextColors.DARK_GREEN, CommandReset.helpText));
				hasPerms = true;
			}

			if (src.hasPermission(Permissions.COMMAND_SET_BIOME)) {
				helpContents = Text.join(helpContents, Text.of(
						(hasPerms) ? "\n" : "",
						TextColors.AQUA, "is setbiome",
						TextColors.GOLD, " <biome>",
						TextColors.GRAY, " [target]",
						TextColors.DARK_GRAY, " - ",
						TextColors.DARK_GREEN, CommandSetBiome.helpText));
				hasPerms = true;
			}

			if (src.hasPermission(Permissions.COMMAND_SPAWN)) {
				helpContents = Text.join(helpContents,  Text.of(
						(hasPerms) ? "\n" : "",
						TextColors.AQUA, Text.builder("is spawn").onClick(TextActions.runCommand("/is spawn")),
						TextColors.GRAY, " [player]",
						TextColors.DARK_GRAY, " - ",
						TextColors.DARK_GREEN, CommandSpawn.helpText));
				hasPerms = true;
			}
		}

		if (hasPerms) {
			PaginationList.Builder paginationBuilder = PaginationList.builder().title(Text.of(TextColors.AQUA, NAME, " Help")).padding(Text.of("-")).contents(helpContents);
			paginationBuilder.sendTo(src);
		} else {
			src.sendMessage(Text.of(NAME + " " + VERSION));
		}


		return CommandResult.success();
	}
}