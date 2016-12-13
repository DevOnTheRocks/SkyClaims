package net.mohron.skyclaims.command;

import net.mohron.skyclaims.Permissions;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import static net.mohron.skyclaims.PluginInfo.NAME;
import static net.mohron.skyclaims.PluginInfo.VERSION;

public class CommandHelp implements CommandExecutor {

	public static CommandSpec commandSpec = CommandSpec.builder()
			.description(Text.of("Help"))
			.executor(new CommandHelp())
			.build();

	public static void register() {
		try {


			SkyClaims.getInstance().getGame().getCommandManager().register(SkyClaims.getInstance(), commandSpec /*, Str:<alias>*/);
			SkyClaims.getInstance().getLogger().info("Registered command: CommandHelp");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Failed to register command: CommandHelp");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		boolean hasPerms = false;

		if (src.hasPermission(Permissions.COMMAND_CREATE)) {
			src.sendMessage(Text.builder("is create").onClick(TextActions.runCommand("is create")).append(Text.builder(" - used to create your personal island").color(TextColors.DARK_GREEN).build()).build());
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_RESET)) {
			src.sendMessage(Text.builder("is reset").onClick(TextActions.runCommand("is reset")).append(Text.builder(" - used to restart your island").color(TextColors.DARK_GREEN).build()).build());
			hasPerms = true;
		}

		if (!hasPerms) src.sendMessage(Text.of(NAME + " " + VERSION));
		return CommandResult.success();
	}
}
