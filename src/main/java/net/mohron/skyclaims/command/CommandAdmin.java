package net.mohron.skyclaims.command;

import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.lib.Permissions;
import org.spongepowered.api.Sponge;
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
			.description(Text.of(helpText))
			.child(CommandDelete.commandSpec, "delete")
			.child(CommandSetup.commandSpec, "setup")
			.child(CommandCreateSchematic.commandSpec, "createschematic", "cs")
			.executor(new CommandHelp())
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
		// Not used, CommandAdmin is used exclusively as a parent command. Runs /is help when not supplied with a subcommand.
		return CommandResult.success();
	}
}