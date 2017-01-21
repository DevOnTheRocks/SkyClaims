package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.util.IslandUtil;
import net.mohron.skyclaims.world.Island;
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

public class CommandTransfer implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "used to transfer an island to another user.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_SETUP)
			.description(Text.of(helpText))
			.arguments(GenericArguments.seq(
					//GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.user(Arguments.OWNER))),
					GenericArguments.user(Arguments.USER)
			))
			.executor(new CommandTransfer())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandTransfer");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandTransfer");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		//if (!(src instanceof Player) && !args.hasAny(Arguments.OWNER))
		//	throw new CommandException(Text.of(TextColors.RED, "You must supply a user to use this command."));
		User user = (User) args.getOne(Arguments.USER).orElse(null);
		Player player = (src instanceof Player) ? (Player) src : null;
		Island island = (false) ? IslandUtil.getIslandByOwner(user.getUniqueId()).orElse(null) : IslandUtil.getIslandByLocation(player.getLocation()).orElse(null);

		throw new CommandException(Text.of("Command is not yet implemented."));

		//island.transfer(user);

		//return CommandResult.empty();
	}
}