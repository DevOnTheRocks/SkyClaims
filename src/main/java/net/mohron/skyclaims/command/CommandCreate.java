package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.lib.Arguments;
import net.mohron.skyclaims.lib.Permissions;
import net.mohron.skyclaims.util.IslandUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
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
			.arguments(GenericArguments.optional(GenericArguments.choices(Arguments.SCHEMATIC, Arguments.SCHEMATICS)))
			.executor(new CommandCreate())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandCreate");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandCreate");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Text.of("You must be a player to use this command!"));

		Player player = (Player) src;
		String schematic = "island";

		if (IslandUtil.hasIsland(player.getUniqueId()))
			throw new CommandException(Text.of("You already have an island!"));

		if (args.getOne(Arguments.SCHEMATIC).isPresent())
			schematic = (String) args.getOne(Arguments.SCHEMATIC).get();

		player.sendMessage(Text.of("Your Island is being created. You will be teleported shortly."));
		IslandUtil.createIsland(player, schematic);

		return CommandResult.success();
	}
}