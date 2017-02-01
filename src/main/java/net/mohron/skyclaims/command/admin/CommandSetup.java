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

	private static final PermissionService PERMS = PLUGIN.getPermissionService();

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