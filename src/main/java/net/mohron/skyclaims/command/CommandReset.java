package net.mohron.skyclaims.command;

import net.mohron.skyclaims.IslandTasks;
import net.mohron.skyclaims.Permissions;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandReset implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "delete your island and inventory so you can start over.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_RESET)
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(GenericArguments.literal(Text.of("confirm"), "confirm")))
			.executor(new CommandReset())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "reset");
			PLUGIN.getLogger().info("Registered command: CommandReset");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandReset");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of("You must be a player to run this command!"));
		}
		Player player = (Player) src;

		if (!PLUGIN.dataStore.hasIsland(player.getUniqueId())) {
			throw new CommandException(Text.of("You do not have an island!"));
		}

		if (!args.hasAny("confirm")) {
			player.sendMessage(Text.of("Are you sure you want to reset your island? This cannot be undone!"));
			player.sendMessage(Text.of("To continue, run ", "/is reset", " confirm"));
		} else {
			IslandTasks.resetIsland(player.getUniqueId());
		}

		return CommandResult.success();
	}
}