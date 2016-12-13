package net.mohron.skyclaims.command;

import net.mohron.skyclaims.Island;
import net.mohron.skyclaims.Permissions;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandSetSpawn {

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_SET_SPAWN)
			.description(Text.of("setspawn"))
			.executor(new CommandCreate())
			.build();

	public static void register() {
		try {
			SkyClaims.getInstance().getGame().getCommandManager().register(SkyClaims.getInstance(), commandSpec /*, Str:<alias>*/);
			SkyClaims.getInstance().getLogger().info("Registered command: CommandSetSpawn");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Failed to register command: CommandSetSpawn");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of("You must be a player to run this command!"));
		}
		Player player = (Player) src;
		Island island = new Island(player.getUniqueId());

		island.setSpawn(player.getLocation());

		player.sendMessage(Text.of("Your Island spawn has been set to " + island.getSpawn()));
		return CommandResult.success();
	}
}