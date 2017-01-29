package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public class CommandInvite implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "used to invite players to your island or list your pending invites.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_INVITE)
			.arguments(GenericArguments.optional(GenericArguments.user(Arguments.USER)))
			.description(Text.of(helpText))
			.executor(new CommandLock())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandInvite");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandInvite");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of("You must be a player to run this command!"));
		}
		Player player = (Player) src;
		User user = (User) args.getOne(Arguments.USER).orElse(null);

		if (user == null) {
			//TODO List invites
		} else {
			//TODO Send invite to online player or save for login
		}

		throw new CommandException(Text.of("Command not yet implemented"));

		//return CommandResult.success();
	}
}
