package net.mohron.skyclaims.command.admin;

import net.mohron.skyclaims.SkyClaims;
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

public class CommandDelete implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static final String HELP_TEXT = "used to delete a player's island";

	private static final Text REGEN = Text.of("regen");
	private static final Text USER = Text.of("user");

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_DELETE)
			.description(Text.of(HELP_TEXT))
			.arguments(GenericArguments.user(USER), GenericArguments.optional(GenericArguments.bool(REGEN)))
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
		User user = (User) args.getOne(USER)
				.orElseThrow(() -> new CommandException(Text.of("Invalid user")));
		Island island = Island.getByOwner(user.getUniqueId())
				.orElseThrow(() -> new CommandException(Text.of("Invalid island")));

		boolean regen = (boolean) args.getOne(REGEN).orElse(true);
		if (regen) island.regen();
		island.delete();

		src.sendMessage(Text.of(island.getOwnerName(), "'s island has been deleted!"));
		return CommandResult.success();
	}
}