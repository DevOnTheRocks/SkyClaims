package net.mohron.skyclaims.command;

import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.lib.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandAdmin {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = String.format("use to run %s's admin commands", PluginInfo.NAME);

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_ADMIN)
			.description(Text.of("SkyClaims Admin Command"))
			.child(CommandHelp.commandSpec, "help")
			.executor(new CommandHelp())
			.build();

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		// Not used, CommandAdmin is used exclusively as a parent command. Runs /is admin help when not supplied with a subcommand.
		return CommandResult.success();
	}
}
