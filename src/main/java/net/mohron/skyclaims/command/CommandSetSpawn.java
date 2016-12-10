package net.mohron.skyclaims.command;

import net.mohron.skyclaims.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandSetSpawn {
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)) {
			throw new CommandException(Text.of("You must be a player to run this command!"));
		}
		Player player = (Player) src;
		Island island = new Island(player.getUniqueId());

		island.setSpawn(player.getLocation());

		player.sendMessage(Text.of("Your Island spawn has been set to " + island.getSpawn()));
		return CommandResult.success();
	}
}