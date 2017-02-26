/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims.command.user;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.argument.Argument;
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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CommandReset implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final String HELP_TEXT = "delete your island and inventory so you can start over";
	private static final Text CONFIRM = Text.of("confirm");
	private static final Text SCHEMATIC = Text.of("schematic");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_RESET)
		.description(Text.of(HELP_TEXT))
		.arguments(GenericArguments.seq(
			GenericArguments.optional(GenericArguments.literal(CONFIRM, "confirm")),
			GenericArguments.optional(Argument.schematic(SCHEMATIC))
		))
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
		Island island = Island.getByOwner(player.getUniqueId())
			.orElseThrow(() -> new CommandException(Text.of("You must have an island to run this command!")));
		String schematic = args.<String>getOne(SCHEMATIC).orElse(Options.getDefaultSchematic(player.getUniqueId()));

		if (!args.hasAny(CONFIRM)) {
			player.sendMessage(Text.of("Are you sure you want to reset your island and inventory? This cannot be undone!"));
			player.sendMessage(Text.of(
				TextColors.GOLD, "Do you want to continue?", TextColors.WHITE, "[",
				Text.builder("YES").color(TextColors.GREEN).onClick(TextActions.runCommand("/is reset confirm " + schematic)),
				TextColors.WHITE, "] [",
				Text.builder("NO").color(TextColors.RED).onClick(TextActions.executeCallback(s -> s.sendMessage(Text.of("Island reset canceled!")))),
				TextColors.WHITE, "]"
			));
		} else {
			player.getEnderChestInventory().clear();
			player.getInventory().clear();

			// Teleport any players located in the island's region to spawn
			Location<World> spawn = PLUGIN.getConfig().getWorldConfig().getWorld().getSpawnLocation();
			island.getPlayers().forEach(p -> p.setLocation(spawn));

			src.sendMessage(Text.of("Please be patient while your island is reset."));
			island.regen(schematic);
		}

		return CommandResult.success();
	}
}