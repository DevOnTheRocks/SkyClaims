package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.lib.Arguments;
import net.mohron.skyclaims.lib.Permissions;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.util.IslandUtil;
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

import java.util.Optional;

public class CommandSpawn implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "teleport to an island's spawn point";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_SPAWN)
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(GenericArguments.user(Arguments.USER)))
			.executor(new CommandSpawn())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandSpawn");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandSpawn");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of("You must be a player to use this command!"));
		}
		Player player = (Player) src;
		User user = (args.getOne(Arguments.USER).isPresent()) ? (User) args.getOne(Arguments.USER).get() : (User) src;
		Optional<Island> island = IslandUtil.getIsland(user.getUniqueId());

		if (!island.isPresent())
			throw new CommandException(Text.of(TextColors.RED, user.getName(), " must have an Island to use this command!"));

		if (!island.get().hasPermissions(player) && !src.hasPermission(Permissions.COMMAND_SPAWN_OTHERS))
			throw new CommandException(Text.of(TextColors.RED, "You must be trusted on ", user.getName(), "'s island to use this command!"));

		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(CommandUtil.createTeleportConsumer((Player) src, island.get().getSpawn())).submit(PLUGIN);

		return CommandResult.success();
	}
}