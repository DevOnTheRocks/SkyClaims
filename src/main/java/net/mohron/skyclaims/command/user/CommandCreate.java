package net.mohron.skyclaims.command.user;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.argument.SchematicArgument;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.world.Island;
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

	public static final String HELP_TEXT = "create an island.";

	private static final Text SCHEMATIC = Text.of("schematic");

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_CREATE)
			.description(Text.of(HELP_TEXT))
			.arguments(GenericArguments.optional(new SchematicArgument(SCHEMATIC)))
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

		if (Island.hasIsland(player.getUniqueId()))
			throw new CommandException(Text.of("You already have an island!"));

		String schematic = (String) args.getOne(SCHEMATIC).orElse(Options.getStringOption(player.getUniqueId(), Options.DEFAULT_SCHEMATIC));

		player.sendMessage(Text.of("Your island is being created. You will be teleported shortly."));

		try {
			new Island(player, schematic);
		} catch (CreateIslandException e) {
			throw new CommandException(Text.of("Unable to create island! " + e.getMessage()));
		}

		return CommandResult.success();
	}
}