package net.mohron.skyclaims.command;

import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.lib.Arguments;
import net.mohron.skyclaims.lib.Permissions;
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

	public static String helpText = String.format("use to run %s's subcommands or display command help info.", PluginInfo.NAME);

	private static CommandSpec commandSpec = CommandSpec.builder()
			.description(Text.of(helpText))
			.child(CommandAdmin.commandSpec, "admin")
			.child(CommandCreate.commandSpec, "create")
			.child(CommandInfo.commandSpec, "info")
			.child(CommandList.commandSpec, "list")
			.child(CommandLock.commandSpec, "lock")
			.child(CommandReset.commandSpec, "reset")
			.child(CommandSetBiome.commandSpec, "setbiome")
			.child(CommandSetSpawn.commandSpec, "setspawn", "sethome")
			.child(CommandSpawn.commandSpec, "spawn", "tp", "home")
			.child(CommandUnlock.commandSpec, "unlock")
			.arguments(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.literal(Arguments.HELP))))
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
		Text helpContents = Text.EMPTY;
		boolean hasPerms = false;

		helpContents = Text.join(helpContents, Text.of(
				TextColors.WHITE, "SkyClaims utilizes GriefPrevention for world protection and management. Go to ", TextColors.YELLOW,
				"http://bit.ly/mcgpuser", TextColors.WHITE, " to learn more."));

		if (src.hasPermission(Permissions.COMMAND_CREATE)) {
			helpContents = Text.join(helpContents, Text.of(
					"\n",
					TextColors.AQUA, Text.builder("is create").onClick(TextActions.runCommand("/is create")),
					TextColors.GRAY, " [schematic]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandCreate.helpText));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_INFO)) {
			helpContents = Text.join(helpContents, Text.of(
					"\n",
					TextColors.AQUA, Text.builder("is info").onClick(TextActions.runCommand("/is info")),
					TextColors.GRAY, " [player]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandInfo.helpText));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_LOCK)) {
			helpContents = Text.join(helpContents, Text.of(
					"\n",
					TextColors.AQUA, Text.builder("is lock").onClick(TextActions.runCommand("/is lock")),
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandLock.helpText));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_RESET)) {
			helpContents = Text.join(helpContents, Text.of(
					"\n",
					TextColors.AQUA, Text.builder("is reset").onClick(TextActions.runCommand("/is reset")),
					TextColors.GRAY, " [schematic]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandReset.helpText));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_SET_BIOME)) {
			helpContents = Text.join(helpContents, Text.of(
					"\n",
					TextColors.AQUA, "is setbiome",
					TextColors.GOLD, " <biome>",
					TextColors.GRAY, " [target]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandSetBiome.helpText));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_SET_SPAWN)) {
			helpContents = Text.join(helpContents, Text.of(
					"\n",
					TextColors.AQUA, Text.builder("is setspawn").onClick(TextActions.runCommand("/is setspawn")),
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandSetSpawn.helpText));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_SPAWN)) {
			helpContents = Text.join(helpContents, Text.of(
					"\n",
					TextColors.AQUA, Text.builder("is spawn").onClick(TextActions.runCommand("/is spawn")),
					TextColors.GRAY, " [player]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandSpawn.helpText));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_LOCK)) {
			helpContents = Text.join(helpContents, Text.of(
					"\n",
					TextColors.AQUA, Text.builder("is unlock").onClick(TextActions.runCommand("/is unlock")),
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandUnlock.helpText));
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