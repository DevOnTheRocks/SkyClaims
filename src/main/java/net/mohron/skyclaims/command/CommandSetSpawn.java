package net.mohron.skyclaims.command;

import net.mohron.skyclaims.Permissions;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandSetSpawn {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "set your spawn location for your island.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_SET_SPAWN)
			.description(Text.of(helpText))
			.executor(new CommandCreate())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().info("Registered command: CommandSetSpawn");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandSetSpawn");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of("You must be a player to run this command!"));
		}
		Player player = (Player) src;
		if (!PLUGIN.dataStore.hasIsland(player.getUniqueId())) throw new CommandException(Text.of("You must have an island to use this command!"));
		Island island = PLUGIN.dataStore.getIsland(player.getUniqueId());

		//island.setSpawn(player.getLocation());

		player.sendMessage(Text.of("Your Island spawn has been set to " + island.getSpawn()));
		return CommandResult.success();
	}
}