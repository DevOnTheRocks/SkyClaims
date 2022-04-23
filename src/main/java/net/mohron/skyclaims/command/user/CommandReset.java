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

import java.util.Optional;
import java.util.function.Consumer;
import net.mohron.skyclaims.command.CommandBase.ListIslandCommand;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.schematic.IslandSchematic;
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

public class CommandReset extends ListIslandCommand {

  public static final String HELP_TEXT = "reset your island and inventory so you can start over.";

  private static final Text SCHEMATIC = Text.of("schematic");
  private static final Text KEEP_INV = Text.of("keepinv");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_RESET)
        .description(Text.of(HELP_TEXT))
        .arguments(
            GenericArguments.optional(Arguments.schematic(SCHEMATIC)),
            GenericArguments.optional(GenericArguments.requiringPermission(GenericArguments.bool(KEEP_INV), Permissions.COMMAND_RESET_KEEP_INV))
        )
        .executor(new CommandReset())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "reset");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandReset");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandReset", e);
    }
  }

  @Override
  public CommandResult execute(Player player, CommandContext args) throws CommandException {
    return listIslands(player, PrivilegeType.OWNER, island -> processIslandReset(args, island));
  }

  private Consumer<CommandSource> processIslandReset(CommandContext args, Island island) {
    return src -> {
      Player player = (Player) src;
      boolean keepInv = args.<Boolean>getOne(KEEP_INV).orElse(false);
      Optional<IslandSchematic> schematic = args.getOne(SCHEMATIC);
      Optional<IslandSchematic> defaultSchematic = Options.getDefaultSchematic(player.getUniqueId());
      if (schematic.isPresent()) {
        getConfirmation(island, schematic.get(), keepInv).accept(player);
      } else if (defaultSchematic.isPresent()) {
        getConfirmation(island, defaultSchematic.get(), keepInv).accept(player);
      } else {
        try {
          listSchematics(player, s -> getConfirmation(island, s, keepInv));
        } catch (CommandException e) {
          player.sendMessage(e.getText());
        }
      }
    };
  }

  private Consumer<CommandSource> getConfirmation(Island island, IslandSchematic schematic, boolean keepInv) {
    return src -> {
      if (src instanceof Player) {
        Player player = (Player) src;
        if (island.isOwner(player)) {
          player.sendMessage(Text.of(
              "Are you sure you want to reset your island",
              !keepInv ? " and inventory" : Text.EMPTY,
              "? This cannot be undone!", Text.NEW_LINE,
              TextColors.GOLD, "Do you want to continue?", Text.NEW_LINE,
              TextColors.WHITE, "[",
              Text.builder("YES")
                  .color(TextColors.GREEN)
                  .onHover(TextActions.showText(Text.of("Click to reset")))
                  .onClick(TextActions.executeCallback(resetIsland(player, island, schematic, keepInv))),
              TextColors.WHITE, "] [",
              Text.builder("NO")
                  .color(TextColors.RED)
                  .onHover(TextActions.showText(Text.of("Click to cancel")))
                  .onClick(TextActions.executeCallback(s -> s.sendMessage(Text.of("Island reset canceled!")))),
              TextColors.WHITE, "]"
          ));
        }
      }
    };
  }

  private Consumer<CommandSource> resetIsland(Player player, Island island, IslandSchematic schematic, boolean keepInv) {
    return src -> {
      if (island.isOwner(player)) {
        // The keep inv argument will skip individual permission checks.
        if (!keepInv) {
          clearIslandMemberInventories(island, Permissions.KEEP_INV_PLAYER_RESET,
              Permissions.KEEP_INV_ENDERCHEST_RESET);
        }

        // Teleport any players located in the island's region to spawn
        island.getPlayers().forEach(p -> p.setLocationSafely(PLUGIN.getConfig().getWorldConfig().getSpawn()));

        player.sendMessage(Text.of("Please be patient while your island is reset."));
        island.reset(schematic, !keepInv);
      }
    };
  }
}
