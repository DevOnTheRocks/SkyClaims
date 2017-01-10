package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.lib.Arguments;
import net.mohron.skyclaims.lib.Permissions;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.util.IslandUtil;
import net.mohron.skyclaims.util.WorldUtil;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.RegenerateRegionTask;
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

import java.util.Optional;
import java.util.Set;

public class CommandReset implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "delete your island and inventory so you can start over";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_RESET)
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(GenericArguments.seq(
					GenericArguments.onlyOne(GenericArguments.literal(Arguments.CONFIRM, "confirm")),
					GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.string(Arguments.SCHEMATIC)))
			)))
			.executor(new CommandReset())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "reset");
			PLUGIN.getLogger().debug("Registered command: CommandReset");
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
		Optional<Island> island = IslandUtil.getIslandByOwner(player.getUniqueId());

		if (!island.isPresent())
			throw new CommandException(Text.of("You must have an island to run this command!"));

		if (!args.hasAny(Arguments.CONFIRM)) {
			player.sendMessage(Text.of("Are you sure you want to reset your island? This cannot be undone!"));
			player.sendMessage(Text.of("To continue, run ", "/is reset confirm", (args.hasAny(Arguments.SCHEMATIC)) ? " [schematic]" : ""));
		} else {
			String schematic = "island";
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

			player.getEnderChestInventory().clear();
			player.getInventory().clear();

			// Teleport any players located in the island's region to spawn
			Set<Player> players = island.get().getPlayers();
			if (!players.isEmpty())
				for (Player p : players)
					CommandUtil.createForceTeleportConsumer(p, WorldUtil.getDefaultWorld().getSpawnLocation());

			src.sendMessage(Text.of("Please be patient while your island is reset."));

			RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(island.get(), schematic);
			PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask).submit(PLUGIN);
		}

		return CommandResult.success();
	}
}