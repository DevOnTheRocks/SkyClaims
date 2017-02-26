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

import me.ryanhamshire.griefprevention.api.GriefPreventionApi;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.data.PlayerData;
import net.mohron.skyclaims.SkyClaims;
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

public class CommandExpand implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final GriefPreventionApi GP = PLUGIN.getGriefPrevention();
	public static final String HELP_TEXT = "used to expand your island.";
	private static final Text BLOCKS = Text.of("size");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_EXPAND)
		.description(Text.of(HELP_TEXT))
		.arguments(GenericArguments.optional(GenericArguments.integer(BLOCKS)))
		.executor(new CommandExpand())
		.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandExpand");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandExpand");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Text.of("You must be a player to use this command!"));

		Player player = (Player) src;
		int blocks = args.<Integer>getOne(BLOCKS).orElse(0);

		Island island = Island.get(player.getLocation())
			.orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must be on an island to use this command.")));
		Claim claim = island.getClaim()
			.orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "The expand command can only be used on protected islands.")));

		// Check if the command source owns the island
		if (!island.getOwnerUniqueId().equals(player.getUniqueId()))
			throw new CommandException(Text.of("Only the island owner may use this command!"));

		int width = claim.getWidth();
		int maxSize = Options.getMaxSize(player.getUniqueId()) * 2;

		// Check if expanding would exceed the max size
		if (width >= maxSize || width + blocks * 2 > maxSize)
			throw new CommandException(Text.of(TextColors.RED, "You cannot expand your island greater than ", TextColors.LIGHT_PURPLE, maxSize, TextColors.GRAY, "x", TextColors.LIGHT_PURPLE, maxSize, TextColors.RED, "."));

		// Check if a non-positive block amount was provided
		if (blocks < 1) {
			player.sendMessage(Text.of(TextColors.GRAY, "It will cost ", TextColors.LIGHT_PURPLE, (Math.pow(width + 1, 2) - claim.getArea()), TextColors.GRAY, " claim blocks to expand your island by ", TextColors.LIGHT_PURPLE, "1", TextColors.GRAY, "."));
			player.sendMessage(Text.builder("Do you want to continue expanding your island?").color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click here to expand."))).onClick(TextActions.runCommand("/is expand 1")).build());
			return CommandResult.success();
		}

		PlayerData playerData = GP.getWorldPlayerData(island.getWorld().getProperties(), island.getOwnerUniqueId())
			.orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "Unable to load GriefPrevention player data.")));
		int bal = playerData.getRemainingClaimBlocks();
		int cost = (int) (Math.pow(width + blocks, 2) - claim.getArea());

		// Check if the player has enough claim blocks to expand
		if (bal < cost)
			throw new CommandException(Text.of(TextColors.RED, "You need ", TextColors.LIGHT_PURPLE, cost, " claim blocks (", TextColors.LIGHT_PURPLE, bal, TextColors.RED, ") to expand your island by ", TextColors.LIGHT_PURPLE, blocks, TextColors.RED, "."));

		// Use the player's claim blocks to expand the island
		playerData.setBonusClaimBlocks(playerData.getBonusClaimBlocks() - cost);
		island.expand(blocks);
		player.sendMessage(Text.of(TextColors.GREEN, "Your island has been expanded to ", TextColors.LIGHT_PURPLE, island.getWidth(), TextColors.GRAY, "x", TextColors.LIGHT_PURPLE, island.getWidth(), TextColors.GREEN, "."));

		return CommandResult.success();
	}
}
