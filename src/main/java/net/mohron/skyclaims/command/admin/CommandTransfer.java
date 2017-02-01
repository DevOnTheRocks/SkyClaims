package net.mohron.skyclaims.command.admin;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.Arguments;
import net.mohron.skyclaims.permissions.Permissions;
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
import org.spongepowered.api.text.format.TextColors;

public class CommandTransfer implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "used to transfer island ownership to another player.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_TRANSFER)
			.description(Text.of(helpText))
			.arguments(GenericArguments.seq(
					//GenericArguments.optional(GenericArguments.user(Arguments.OWNER)),
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
		if (!(src instanceof Player) /*&& !args.hasAny(Arguments.OWNER)*/)
			throw new CommandException(Text.of(TextColors.RED, "You must supply a user to use this command."));

		User user = (User) args.getOne(Arguments.USER).orElse(null);
		if (user == null || Island.hasIsland(user.getUniqueId()))
			throw new CommandException(Text.of(TextColors.RED, "Unable to complete island transfer to ", user.getName(), ": player has reached the max islands owned!"));

		Player player = (src instanceof Player) ? (Player) src : null;
		Island island = (false) ? Island.getByOwner(user.getUniqueId()).orElse(null) : Island.get(player.getLocation()).orElse(null);

		if (island == null)
			throw new CommandException(Text.of(TextColors.RED, "This command must be run on the island you wish to transfer!"));

		src.sendMessage(Text.of(TextColors.GREEN, "Completed transfer of ", TextColors.GOLD, island.getOwnerName(), TextColors.GREEN, "'s island to ", TextColors.GOLD, user.getName(), TextColors.GREEN, "."));
		island.transfer(user);

		return CommandResult.success();
	}
}