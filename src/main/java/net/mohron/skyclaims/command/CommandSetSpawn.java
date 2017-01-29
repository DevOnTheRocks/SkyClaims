package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.extent.Extent;

import java.util.Optional;

public class CommandSetSpawn implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "set your spawn location for your island.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_SET_SPAWN)
			.description(Text.of(helpText))
			.executor(new CommandSetSpawn())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandSetSpawn");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandSetSpawn");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Text.of("You must be a player to use this command!"));

		Player player = (Player) src;
		Optional<Island> island = Island.get(player.getLocation());

		if (!island.isPresent())
			throw new CommandException(Text.of("You must be on an island to use this command!"));

		if (!island.get().getOwnerUniqueId().equals(player.getUniqueId()) && !player.hasPermission(Permissions.COMMAND_SET_SPAWN_OTHERS))
			throw new CommandException(Text.of("Only the island owner may use this command!"));

		island.get().setSpawn(player.getTransform());
		player.sendMessage(Text.of("Your island spawn has been set to ", TextColors.GRAY, "(",
				TextColors.LIGHT_PURPLE, island.get().getSpawn().getPosition().getX(), TextColors.GRAY, " ,",
				TextColors.LIGHT_PURPLE, island.get().getSpawn().getPosition().getY(), TextColors.GRAY, " ,",
				TextColors.LIGHT_PURPLE, island.get().getSpawn().getPosition().getZ(), TextColors.GRAY, ")"));

		return CommandResult.success();
	}
}