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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CommandDelete implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "used to delete a player's island";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_DELETE)
			.description(Text.of(helpText))
			.arguments(GenericArguments.user(Arguments.USER), GenericArguments.optional(GenericArguments.bool(Arguments.REGEN)))
			.executor(new CommandDelete())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandDelete");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandDelete");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Optional<User> user = args.getOne(Arguments.USER);
		if (!user.isPresent()) throw new CommandException(Text.of("Invalid user"));

		Optional<Island> island = Island.getByOwner(user.get().getUniqueId());
		if (!island.isPresent()) throw new CommandException(Text.of("Invalid island"));

		boolean regen = (boolean) args.getOne(Arguments.REGEN).orElse(true);
		if (regen) island.get().regen();
		island.get().delete();

		src.sendMessage(Text.of(island.get().getOwnerName(), "'s island has been deleted!"));
		return CommandResult.success();
	}
}