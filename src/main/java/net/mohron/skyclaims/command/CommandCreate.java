package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.lib.Arguments;
import net.mohron.skyclaims.lib.Options;
import net.mohron.skyclaims.lib.Permissions;
import net.mohron.skyclaims.util.IslandUtil;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandCreate implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "create an island.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_CREATE)
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.string(Arguments.SCHEMATIC))))
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
		String schematic = Options.getStringOption(player.getUniqueId(), Options.DEFAULT_SCHEMATIC);

		if (IslandUtil.hasIsland(player.getUniqueId()))
			throw new CommandException(Text.of("You already have an island!"));

		if (Arguments.SCHEMATICS.isEmpty())
			throw new CommandException(Text.of("There are no valid schematics to create an island with!"));

		if (args.getOne(Arguments.SCHEMATIC).isPresent()) {
			schematic = (String) args.getOne(Arguments.SCHEMATIC).get();
			if (!player.hasPermission(Permissions.COMMAND_ARGUMENTS_SCHEMATICS + "." + schematic.toLowerCase()))
				throw new CommandPermissionException(Text.of(TextColors.RED, "You do not have permission to use the ", TextColors.YELLOW, schematic, TextColors.RED, " schematic!"));
		}

		if (!Arguments.SCHEMATICS.containsKey(schematic.toLowerCase())) {
			String schems = "";
			for (String s : Arguments.SCHEMATICS.values()) {
				schems += s + ", ";
			}
			schems = schems.substring(0, schems.length() - 2);
			throw new CommandException(Text.of("The value supplied is not a valid schematic. Choose from: ", TextColors.AQUA, schems));
		}

		player.sendMessage(Text.of("Your island is being created. You will be teleported shortly."));

		try {
			new Island(player, schematic);
		} catch (CreateIslandException e) {
			throw new CommandException(Text.of("Unable to create island! " + e.getMessage()));
		}

		return CommandResult.success();
	}
}