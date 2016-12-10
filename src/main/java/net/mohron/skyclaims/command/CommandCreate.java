package net.mohron.skyclaims.command;

import net.mohron.skyclaims.DataStore;
import net.mohron.skyclaims.Island;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandCreate implements CommandExecutor {
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)) {
			throw new CommandException(Text.of("You must be a player to run this command!"));
		}
		Player player = (Player) src;

		if (SkyClaims.instance.dataStore.hasIsland(player.getUniqueId())) {
			throw new CommandException(Text.of("You already have an island!"));
		}

		Island island = SkyClaims.instance.dataStore.createIsland(player.getUniqueId());
		player.sendMessage(Text.of("Your Island is being created. You will be teleported shortly."));
		while (!island.isReady())
		player.setLocation(island.getSpawn());

		return CommandResult.success();
	}
}