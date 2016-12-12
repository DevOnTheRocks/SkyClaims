package net.mohron.skyclaims.command;

import net.mohron.skyclaims.DataStore;
import net.mohron.skyclaims.Island;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CommandCreate implements CommandExecutor {

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_CREATE)
			.description(Text.of("create"))
			.executor(new CommandCreate())
			.build();

	public static void register() {
		try {
			SkyClaims.getInstance().getGame().getCommandManager().register(SkyClaims.getInstance(), commandSpec /*, Str:<alias>*/);
			SkyClaims.getInstance().getLogger().info("Registered command: CommandCreate");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Failed to register command: CommandCreate");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player))
			throw new CommandException(Text.of("You must be a player to run this command!"));

		Player player = (Player) src;
		if (SkyClaims.getInstance().dataStore.hasIsland(player.getUniqueId()))
			throw new CommandException(Text.of("You already have an island!"));

		player.sendMessage(Text.of("Your Island is being created. You will be teleported shortly."));
		Island island = SkyClaims.getInstance().dataStore.createIsland(player.getUniqueId());

		while (!island.isReady())
			player.setLocation(island.getSpawn());

		return CommandResult.success();
	}
}