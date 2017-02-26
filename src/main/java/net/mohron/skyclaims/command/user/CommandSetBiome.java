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
import net.mohron.skyclaims.command.argument.Targets;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.util.WorldUtil;
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
import org.spongepowered.api.world.biome.BiomeType;

public class CommandSetBiome implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final String HELP_TEXT = "set the biome of a block, chunk or island";
	private static final Text BIOME = Text.of("biome");
	private static final Text TARGET = Text.of("target");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_SET_BIOME)
		.description(Text.of(HELP_TEXT))
		.arguments(GenericArguments.seq(
			Argument.biome(BIOME),
			GenericArguments.optional(Argument.target(TARGET))
		))
		.executor(new CommandSetBiome())
		.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "setbiome");
			PLUGIN.getLogger().debug("Registered command: CommandSetBiome");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandSetBiome");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of("You must be a player to run this command!"));
		}

		Player player = (Player) src;
		BiomeType biome = args.<BiomeType>getOne(BIOME)
			.orElseThrow(() -> new CommandException(Text.of("You must supply a biome to use this command")));
		Island island = Island.get(player.getLocation())
			.orElseThrow(() -> new CommandException(Text.of("You must be on an island to use this command")));

		if (!player.getUniqueId().equals(island.getOwnerUniqueId()) && !player.hasPermission(Permissions.COMMAND_SET_BIOME_OTHERS))
			throw new CommandPermissionException(Text.of("You do not have permission to use setbiome on this island"));

		Targets target = args.<Targets>getOne(TARGET).orElse(Targets.ISLAND);

		switch (target) {
			case BLOCK:
				WorldUtil.setBlockBiome(player.getLocation(), biome);
				src.sendMessage(Text.of(TextColors.GREEN, String.format("Successfully changed the biome at %s,%s to %s.", player.getLocation().getBlockX(), player.getLocation().getBlockZ(), biome.getName())));
				break;
			case CHUNK:
				WorldUtil.setChunkBiome(player.getLocation(), biome);
				src.sendMessage(Text.of(TextColors.GREEN, String.format("Successfully changed the biome in this chunk to %s.", biome.getName())));
				break;
			case ISLAND:
				WorldUtil.setIslandBiome(island, biome);
				src.sendMessage(Text.of(TextColors.GREEN, String.format("Successfully changed the biome on this island to %s.", biome.getName())));
				break;
		}

		return CommandResult.success();
	}
}