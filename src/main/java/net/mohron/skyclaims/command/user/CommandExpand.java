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

import java.util.function.Consumer;
import me.ryanhamshire.griefprevention.api.GriefPreventionApi;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimBlockSystem;
import me.ryanhamshire.griefprevention.api.data.PlayerData;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class CommandExpand extends CommandBase.IslandCommand {

  public static final String HELP_TEXT = "used to expand your island.";
  private static final GriefPreventionApi GP = PLUGIN.getGriefPrevention();
  private static final Text BLOCKS = Text.of("blocks");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_EXPAND)
        .description(Text.of(HELP_TEXT))
        .arguments(
            GenericArguments.optionalWeak(Arguments.island(ISLAND, PrivilegeType.MANAGER)),
            Arguments.positiveInteger(BLOCKS)
        )
        .executor(new CommandExpand())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "expand");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandExpand");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandExpand", e);
    }
  }

  @Override
  public CommandResult execute(Player player, Island island, CommandContext args)
      throws CommandException {
    int blocks = args.<Integer>getOne(BLOCKS).orElse(1);

    Claim claim = island.getClaim()
        .orElseThrow(() -> new CommandException(
            Text.of(TextColors.RED, "This command can only be used on claimed islands.")));

    // Check if the player is a Manager
    if (!island.isManager(player)) {
      throw new CommandException(
          Text.of(TextColors.RED, "Only an island manager may use this command!"));
    }

    int width = claim.getWidth();
    int maxSize = Options.getMaxSize(island.getOwnerUniqueId()) * 2;

    // Check if expanding would exceed the max size
    if (width >= maxSize || width + blocks * 2 > maxSize) {
      throw new CommandException(Text.of(
          TextColors.RED, "You cannot expand ", island.getName(), TextColors.RED, " greater than ",
          TextColors.LIGHT_PURPLE, maxSize, TextColors.GRAY, "x", TextColors.LIGHT_PURPLE, maxSize,
          TextColors.RED, "."
      ));
    }

    player.sendMessage(Text.of(
        TextColors.GRAY, "It will cost ", TextColors.LIGHT_PURPLE,
        (int) Math.pow(width + blocks, 2) * (GP.getClaimBlockSystem() == ClaimBlockSystem.VOLUME
            ? 256 : 1) - claim.getClaimBlocks(),
        TextColors.GRAY, " claim blocks to expand ", island.getName(), TextColors.GRAY, " by ",
        TextColors.LIGHT_PURPLE, blocks, TextColors.GRAY, "."
    ));
    player.sendMessage(Text.of(
        TextColors.GRAY, "Do you want to continue expanding your island?",
        Text.NEW_LINE,
        TextColors.WHITE, "[",
        Text.builder("YES")
            .color(TextColors.GREEN)
            .onHover(TextActions.showText(Text.of("Click to expand")))
            .onClick(TextActions.executeCallback(expandIsland(island, blocks))),
        TextColors.WHITE, "] [",
        Text.builder("NO")
            .color(TextColors.RED)
            .onHover(TextActions.showText(Text.of("Click to cancel")))
            .onClick(TextActions
                .executeCallback(s -> s.sendMessage(Text.of("Island expansion canceled!")))),
        TextColors.WHITE, "]"
        )
    );

    return CommandResult.success();
  }

  private Consumer<CommandSource> expandIsland(Island island, int blocks) {
    return src -> {
      Claim claim = island.getClaim().orElse(null);
      PlayerData playerData = GP
          .getWorldPlayerData(island.getWorld().getProperties(), island.getOwnerUniqueId())
          .orElse(null);

      if (claim == null || playerData == null) {
        src.sendMessage(Text.of(TextColors.RED, "An error occurred while attempting to expand ",
            island.getName(), TextColors.RED, "!"));
        PLUGIN.getLogger()
            .error("Expansion Failed: {} - claim: {}, player-data: {}", island.getSortableName(),
                claim != null, playerData != null);
        return;
      }

      int bal = playerData.getRemainingClaimBlocks();
      int cost = (int) Math.pow(claim.getWidth() + blocks, 2) * (
          GP.getClaimBlockSystem() == ClaimBlockSystem.VOLUME ? 256 : 1) - claim
          .getClaimBlocks();

      // Check if the Owner, has enough claim blocks to expand
      if (bal < cost) {
        src.sendMessage(Text.of(
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
      src.sendMessage(Text.of(
          TextColors.GREEN, "Your island has been expanded to ",
          TextColors.LIGHT_PURPLE, island.getWidth(),
          TextColors.GRAY, "x",
          TextColors.LIGHT_PURPLE, island.getWidth(),
          TextColors.GREEN, "."
      ));
    };
  }
}
