package net.mohron.skyclaims.command;

import net.mohron.skyclaims.Island;
import net.mohron.skyclaims.IslandTasks;
import net.mohron.skyclaims.Permissions;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandCreate implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "create an island.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_CREATE)
			.description(Text.of(helpText))
			.executor(new CommandCreate())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().info("Registered command: CommandCreate");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandCreate");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Text.of("You must be a player to run this command!"));

		Player player = (Player) src;
		if (SkyClaims.getInstance().dataStore.hasIsland(player.getUniqueId()))
			throw new CommandException(Text.of("You already have an island!"));

		player.sendMessage(Text.of("Your Island is being created. You will be teleported shortly."));
		Island island = IslandTasks.createIsland(player.getUniqueId());
		PLUGIN.dataStore.saveIsland(island);

//		while (!island.isReady())
//			player.setLocation(island.getSpawn());

		return CommandResult.success();
	}
}