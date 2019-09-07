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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import me.ryanhamshire.griefprevention.api.GriefPreventionApi;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimBlockSystem;
import me.ryanhamshire.griefprevention.api.data.PlayerData;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.config.type.EconomyConfig;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

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
  public CommandResult execute(Player player, Island island, CommandContext args) throws CommandException {
    int blocks = args.<Integer>getOne(BLOCKS).orElse(1);

    Claim claim = island.getClaim().orElseThrow(() -> new CommandException(Text.of(
        TextColors.RED, "This command can only be used on claimed islands."
    )));

    // Check if the player is a Manager
    if (!island.isManager(player)) {
      throw new CommandException(Text.of(TextColors.RED, "Only an island manager may use this command!"));
    }

    int maxSize = Options.getMaxSize(island.getOwnerUniqueId()) * 2;

    // Check if expanding would exceed the max size
    if (exceedsMaxSize(island, claim, blocks)) {
      throw new CommandException(Text.of(
          TextColors.RED, "You cannot expand ", island.getName(), TextColors.RED, " greater than ",
          TextColors.LIGHT_PURPLE, maxSize, TextColors.GRAY, "x", TextColors.LIGHT_PURPLE, maxSize,
          TextColors.RED, "."
      ));
    }

    player.sendMessage(Text.of(
        TextColors.GRAY, "It will cost ", TextColors.LIGHT_PURPLE, getCost(blocks, claim),
        TextColors.GRAY, " to expand ", island.getName(), TextColors.GRAY, " by ",
        TextColors.LIGHT_PURPLE, blocks, TextColors.GRAY, "."
    ));

    player.sendMessage(Text.of(
        TextColors.GRAY, "Do you want to continue expanding your island?",
        Text.NEW_LINE,
        TextColors.WHITE, "[",
        Text.builder("YES")
            .color(TextColors.GREEN)
            .onHover(TextActions.showText(Text.of("Click to expand")))
            .onClick(TextActions.executeCallback(expandIsland(island, claim, blocks))),
        TextColors.WHITE, "] [",
        Text.builder("NO")
            .color(TextColors.RED)
            .onHover(TextActions.showText(Text.of("Click to cancel")))
            .onClick(TextActions.executeCallback(s -> s.sendMessage(Text.of("Island expansion canceled!")))),
        TextColors.WHITE, "]"
        )
    );

    return CommandResult.success();
  }

  private Consumer<CommandSource> expandIsland(Island island, Claim claim, int blocks) {
    return src -> {
      Sponge.getCauseStackManager().pushCause(PLUGIN.getPluginContainer());

      if (exceedsMaxSize(island, claim, blocks)) {
        int maxSize = Options.getMaxSize(island.getOwnerUniqueId()) * 2;
        src.sendMessage(Text.of(
            TextColors.RED, "You cannot expand ", island.getName(), TextColors.RED, " greater than ",
            TextColors.LIGHT_PURPLE, maxSize, TextColors.GRAY, "x", TextColors.LIGHT_PURPLE, maxSize,
            TextColors.RED, "."
        ));
        return;
      }
      int cost = getCost(blocks, claim);

      // Attempt to charge the required currency to the island owner.
      if (charge(island, cost)) {
        island.expand(blocks);
        src.sendMessage(Text.of(
            TextColors.GREEN, "Your island has been expanded to ",
            TextColors.LIGHT_PURPLE, island.getWidth(),
            TextColors.GRAY, "x",
            TextColors.LIGHT_PURPLE, island.getWidth(),
            TextColors.GREEN, "."
        ));
      } else {
        src.sendMessage(Text.of(
            TextColors.RED, "You do not have the required currency (",
            TextColors.LIGHT_PURPLE, cost,
            TextColors.RED, ") to expand your island by ",
            TextColors.LIGHT_PURPLE, blocks,
            TextColors.RED, "."
        ));
      }
      Sponge.getCauseStackManager().popCause();
    };
  }

  private boolean exceedsMaxSize(Island island, Claim claim, int blocks) {
    return claim.getWidth() + blocks * 2 > Options.getMaxSize(island.getOwnerUniqueId()) * 2;
  }

  private int getCost(int blocks, Claim claim) {
    return (int) Math.pow(claim.getWidth() + blocks, 2)
        * (GP.getClaimBlockSystem() == ClaimBlockSystem.VOLUME ? 256 : 1) - claim.getClaimBlocks();
  }

  private boolean charge(Island island, int cost) {
    EconomyConfig config = PLUGIN.getConfig().getEconomyConfig();
    Optional<EconomyService> economyService = Sponge.getServiceManager().provide(EconomyService.class);

    if (!config.isUseClaimBlocks() && economyService.isPresent()) {
      Optional<UniqueAccount> account = economyService.get().getOrCreateAccount(island.getOwnerUniqueId());
      Currency currency = config.getCurrency().orElse(economyService.get().getDefaultCurrency());
      if (account.isPresent()) {
        // Charge the player's currency account
        TransactionResult transaction = account.get().withdraw(
            currency,
            BigDecimal.valueOf(cost),
            Sponge.getCauseStackManager().getCurrentCause()
        );
        return transaction.getResult() == ResultType.SUCCESS;
      } else {
        PLUGIN.getLogger().error("Could not find {} account for {}.", currency.getDisplayName().toPlain(), island.getOwnerName());
        return false;
      }
    } else {
      // Use GP claim blocks by manipulating the player's bonus claim block balance
      Optional<PlayerData> data = GP.getWorldPlayerData(island.getWorld().getProperties(), island.getOwnerUniqueId());
      if (data.isPresent()) {
        if (data.get().getRemainingClaimBlocks() >= cost) {
          // Use the Owner's claim blocks to expand the island
          data.get().setBonusClaimBlocks(data.get().getBonusClaimBlocks() - cost);
          return true;
        }
        return false;
      } else {
        PLUGIN.getLogger().error("Could not load GP player data for {}.", island.getOwnerName());
        return false;
      }
    }
  }
}
