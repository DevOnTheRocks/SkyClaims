package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.lib.Permissions;
import net.mohron.skyclaims.util.IslandUtil;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CommandUnlock implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "used to allow untrusted players from visiting to your island.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_LOCK)
			.description(Text.of(helpText))
			.executor(new CommandUnlock())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandUnlock");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandUnlock");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of("You must be a player to run this command!"));
		}
		Player player = (Player) src;
		Optional<Island> island = IslandUtil.getIslandByOwner(player.getUniqueId());

		if (!island.isPresent())
			throw new CommandException(Text.of("You must have an Island to run this command!"));

		island.get().setLocked(false);

		src.sendMessage(Text.of(TextColors.GREEN, "Your island is now unlocked!"));
		return CommandResult.success();
	}
}