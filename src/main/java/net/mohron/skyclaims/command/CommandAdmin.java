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

public class CommandAdmin implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = String.format("use to run %s's admin commands or display help info", PluginInfo.NAME);

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_ADMIN)
			.description(Text.of(helpText))
			.child(CommandDelete.commandSpec, "delete")
			.child(CommandReload.commandSpec, "reload")
			.child(CommandSetup.commandSpec, "setup")
			.child(CommandCreateSchematic.commandSpec, "createschematic", "cs")
			.arguments(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.literal(Arguments.HELP))))
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
		Text helpContents = Text.EMPTY;
		boolean hasPerms = false;

		if (src.hasPermission(Permissions.COMMAND_CREATE_SCHEMATIC)) {
			helpContents = Text.join(helpContents, Text.of(
					TextColors.AQUA, "isa cs",
					TextColors.GOLD, " <name>",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandCreateSchematic.helpText));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_DELETE)) {
			helpContents = Text.join(helpContents, Text.of(
					TextColors.AQUA, "isa delete",
					TextColors.GOLD, " <player>",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandDelete.helpText));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_RELOAD)) {
			helpContents = Text.join(helpContents, Text.of(
					TextColors.AQUA, Text.builder("isa reload").onClick(TextActions.runCommand("/isa reload")),
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandReload.helpText));
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