/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2022 Mohron
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

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimBlockSystem;
import com.griefdefender.api.data.PlayerData;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import net.mohron.skyclaims.command.CommandBase.IslandCommand;
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

public class CommandExpand extends IslandCommand {

  public static final String HELP_TEXT = "used to expand your island.";
  private static final Text BLOCKS = LinearComponents.linear("blocks");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_EXPAND)
        .description(LinearComponents.linear(HELP_TEXT))
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

    Claim claim = island.getClaim().orElseThrow(() -> new CommandException(LinearComponents.linear(
        NamedTextColor.RED, "This command can only be used on claimed islands."
    )));

    // Check if the player is a Manager
    if (!island.isManager(player)) {
      throw new CommandException(LinearComponents.linear(NamedTextColor.RED, "Only an island manager may use this command!"));
    }

    int maxSize = Options.getMaxSize(island.getOwnerUniqueId()) * 2;

    // Check if expanding would exceed the max size
    if (exceedsMaxSize(island, claim, blocks)) {
      throw new CommandException(LinearComponents.linear(
          NamedTextColor.RED, "You cannot expand ", island.getName(), NamedTextColor.RED, " greater than ",
          NamedTextColor.LIGHT_PURPLE, maxSize, NamedTextColor.GRAY, "x", NamedTextColor.LIGHT_PURPLE, maxSize,
          NamedTextColor.RED, "."
      ));
    }

    player.sendMessage(LinearComponents.linear(
        NamedTextColor.GRAY, "It will cost ", NamedTextColor.LIGHT_PURPLE, getCost(blocks, claim),
        NamedTextColor.GRAY, " to expand ", island.getName(), NamedTextColor.GRAY, " by ",
        NamedTextColor.LIGHT_PURPLE, blocks, NamedTextColor.GRAY, "."
    ));

    player.sendMessage(LinearComponents.linear(
        NamedTextColor.GRAY, "Do you want to continue expanding your island?",
        Component.newline(),
        NamedTextColor.WHITE, "[",
        Text.builder("YES")
            .color(NamedTextColor.GREEN)
            .onHover(TextActions.showText(LinearComponents.linear("Click to expand")))
            .onClick(TextActions.executeCallback(expandIsland(island, claim, blocks))),
        NamedTextColor.WHITE, "] [",
        Text.builder("NO")
            .color(NamedTextColor.RED)
            .onHover(TextActions.showText(LinearComponents.linear("Click to cancel")))
            .onClick(TextActions.executeCallback(s -> s.sendMessage(LinearComponents.linear("Island expansion canceled!")))),
        NamedTextColor.WHITE, "]"
        )
    );

    return CommandResult.success();
  }

  private Consumer<CommandSource> expandIsland(Island island, Claim claim, int blocks) {
    return src -> {
      Sponge.getCauseStackManager().pushCause(PLUGIN.getPluginContainer());

      if (exceedsMaxSize(island, claim, blocks)) {
        int maxSize = Options.getMaxSize(island.getOwnerUniqueId()) * 2;
        src.sendMessage(LinearComponents.linear(
            NamedTextColor.RED, "You cannot expand ", island.getName(), NamedTextColor.RED, " greater than ",
            NamedTextColor.LIGHT_PURPLE, maxSize, NamedTextColor.GRAY, "x", NamedTextColor.LIGHT_PURPLE, maxSize,
            NamedTextColor.RED, "."
        ));
        return;
      }
      double cost = getCost(blocks, claim);

      // Attempt to charge the required currency to the island owner.
      if (charge(island, cost)) {
        if (island.expand(blocks)) {
          src.sendMessage(LinearComponents.linear(
              NamedTextColor.GREEN, "Your island has been expanded to ",
              NamedTextColor.LIGHT_PURPLE, island.getWidth(),
              NamedTextColor.GRAY, "x",
              NamedTextColor.LIGHT_PURPLE, island.getWidth(),
              NamedTextColor.GREEN, "."
          ));
        } else {
          src.sendMessage(LinearComponents.linear(NamedTextColor.RED, "An error occurred while expanding your island!"));
        }
      } else {
        src.sendMessage(LinearComponents.linear(
            NamedTextColor.RED, "You do not have the required currency (",
            NamedTextColor.LIGHT_PURPLE, cost,
            NamedTextColor.RED, ") to expand your island by ",
            NamedTextColor.LIGHT_PURPLE, blocks,
            NamedTextColor.RED, "."
        ));
      }
      Sponge.getCauseStackManager().popCause();
    };
  }

  private boolean exceedsMaxSize(Island island, Claim claim, int blocks) {
    return claim.getWidth() + blocks * 2 > Options.getMaxSize(island.getOwnerUniqueId()) * 2;
  }

  private double getCost(int blocks, Claim claim) {
    final int claimSystemModifier = GriefDefender.getCore().getClaimBlockSystem() == ClaimBlockSystem.VOLUME ? 256 : 1;
    final double newBlocks = Math.pow(claim.getWidth() + blocks, 2) * claimSystemModifier - claim.getClaimBlocks();

    return Math.round(newBlocks * PLUGIN.getConfig().getEconomyConfig().getCostModifier() * 100) / 100.0;
  }

  private boolean charge(Island island, double cost) {
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
      Optional<PlayerData> data = Optional.ofNullable(GriefDefender.getCore().getPlayerData(island.getWorld().getUniqueId(), island.getOwnerUniqueId()));
      if (data.isPresent()) {
        if (data.get().getRemainingClaimBlocks() >= cost) {
          // Use the Owner's claim blocks to expand the island
          data.get().setBonusClaimBlocks(data.get().getBonusClaimBlocks() - (int) Math.round(cost));
          return true;
        }
      } else {
        PLUGIN.getLogger().error("Could not load GP player data for {}.", island.getOwnerName());
      }
      return false;
    }
  }
}
