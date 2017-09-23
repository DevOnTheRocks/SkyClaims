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
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Argument;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandExpand extends CommandBase.IslandCommand {

    public static final String HELP_TEXT = "used to expand your island.";
    private static final GriefPreventionApi GP = PLUGIN.getGriefPrevention();
    private static final Text BLOCKS = Text.of("size");

    public static CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_EXPAND)
        .description(Text.of(HELP_TEXT))
        .arguments(GenericArguments.seq(
            GenericArguments.optional(Argument.island(ISLAND)),
            GenericArguments.optional(GenericArguments.integer(BLOCKS))
        ))
        .executor(new CommandExpand())
        .build();

    public static void register() {
        try {
            CommandIsland.addSubCommand(commandSpec, "expand");
            PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
            PLUGIN.getLogger().debug("Registered command: CommandExpand");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            PLUGIN.getLogger().error("Failed to register command: CommandExpand");
        }
    }

    @Override public CommandResult execute(@Nonnull Player player, @Nonnull Island island, @Nonnull CommandContext args) throws CommandException {
        int blocks = args.<Integer>getOne(BLOCKS).orElse(0);

        Claim claim = island.getClaim()
            .orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "This command can only be used on claimed islands.")));

        // Check if the player is a Manager
        if (!island.isManager(player)) {
            throw new CommandException(Text.of(TextColors.RED, "Only an island manager may use this command!"));
        }

        int width = claim.getWidth();
        int maxSize = Options.getMaxSize(island.getOwnerUniqueId()) * 2;

        // Check if expanding would exceed the max size
        if (width >= maxSize || width + blocks * 2 > maxSize) {
            throw new CommandException(Text.of(
                TextColors.RED, "You cannot expand your island greater than ",
                TextColors.LIGHT_PURPLE, maxSize, TextColors.GRAY, "x", TextColors.LIGHT_PURPLE, maxSize, TextColors.RED, "."
            ));
        }

        // Check if a non-positive block amount was provided
        if (blocks < 1) {
            player.sendMessage(Text.of(
                TextColors.GRAY, "It will cost ",
                TextColors.LIGHT_PURPLE, (int) Math.pow(width + 1, 2) * (GP.isWildernessCuboidsEnabled() ? 256 : 1) - claim.getClaimBlocks(),
                TextColors.GRAY, " claim blocks to expand your island by ",
                TextColors.LIGHT_PURPLE, "1",
                TextColors.GRAY, "."
            ));
            player.sendMessage(Text.of(
                TextColors.GRAY, "Do you want to continue expanding your island?",
                Text.NEW_LINE,
                TextColors.WHITE, "[",
                Text.builder("YES")
                    .color(TextColors.GREEN)
                    .onClick(TextActions.runCommand("/is expand 1")),
                TextColors.WHITE, "] [",
                Text.builder("NO")
                    .color(TextColors.RED)
                    .onClick(TextActions.executeCallback(s -> s.sendMessage(Text.of("Island expansion canceled!")))),
                TextColors.WHITE, "]"
                )
            );
            return CommandResult.success();
        }

        PlayerData playerData = GP.getWorldPlayerData(island.getWorld().getProperties(), island.getOwnerUniqueId())
            .orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "Unable to load GriefPrevention player data.")));
        int bal = playerData.getRemainingClaimBlocks();
        int cost = (int) Math.pow(width + blocks, 2) * (GP.isWildernessCuboidsEnabled() ? 256 : 1) - claim.getClaimBlocks();

        // Check if the Owner, has enough claim blocks to expand
        if (bal < cost) {
            throw new CommandException(Text.of(
                TextColors.RED, "You need ",
                TextColors.LIGHT_PURPLE, cost,
                TextColors.RED, " claim blocks (",
                TextColors.LIGHT_PURPLE, bal,
                TextColors.RED, ") to expand your island by ",
                TextColors.LIGHT_PURPLE, blocks,
                TextColors.RED, "."
            ));
        }

        // Use the Owner's claim blocks to expand the island
        playerData.setBonusClaimBlocks(playerData.getBonusClaimBlocks() - cost);
        island.expand(blocks);
        player.sendMessage(Text.of(
            TextColors.GREEN, "Your island has been expanded to ",
            TextColors.LIGHT_PURPLE, island.getWidth(),
            TextColors.GRAY, "x",
            TextColors.LIGHT_PURPLE, island.getWidth(),
            TextColors.GREEN, "."
        ));

        return CommandResult.success();
    }
}
