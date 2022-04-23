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
import net.mohron.skyclaims.command.CommandBase.ListSchematicCommand;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.schematic.IslandSchematic;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandCreate extends ListSchematicCommand {

  public static final String HELP_TEXT = "create an island.";

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_CREATE)
        .description(Text.of(HELP_TEXT))
        .arguments(GenericArguments.optional(Arguments.schematic(SCHEMATIC)))
        .executor(new CommandCreate())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "create");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandCreate");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandCreate", e);
    }
  }

  @Override
  public CommandResult execute(Player player, CommandContext args) throws CommandException {
    if (hasPlayerReachedMaxIslands(player)) {
      throw new CommandException(Text.of(TextColors.RED, "You have reached your maximum number of islands!"));
    }

    Optional<IslandSchematic> schematic = args.getOne(SCHEMATIC);
    Optional<IslandSchematic> defaultSchematic = Options.getDefaultSchematic(player.getUniqueId());
    if (schematic.isPresent()) {
      return createIsland(player, schematic.get());
    } else if (!defaultSchematic.isPresent()) {
      listSchematics(player, this::createIsland);
      return CommandResult.empty();
    } else {
      return createIsland(player, defaultSchematic.get());
    }
  }

  private CommandResult createIsland(Player player, IslandSchematic schematic) throws CommandException {
    if (hasPlayerReachedMaxIslands(player)) {
      throw new CommandException(Text.of(TextColors.RED, "You have reached your maximum number of islands!"));
    }

    player.sendMessage(Text.of(
        TextColors.GREEN, "Your island is being created.",
        PLUGIN.getConfig().getMiscConfig().isTeleportOnCreate() ? " You will be teleported shortly." : Text.EMPTY
    ));

    try {
      Island island = new Island(player, schematic);
      clearIslandMemberInventories(island, Permissions.KEEP_INV_PLAYER_CREATE, Permissions.KEEP_INV_ENDERCHEST_CREATE);
      return CommandResult.success();
    } catch (CreateIslandException e) {
      throw new CommandException(Text.of(TextColors.RED, "Unable to create island!", Text.NEW_LINE, TextColors.RESET, e.getText()));
    }
  }

  private boolean hasPlayerReachedMaxIslands(Player player) {
    int maxIslands = Options.getMaxIslands(player.getUniqueId());
    return maxIslands >= 1 && maxIslands - IslandManager.countByMember(player) <= 0;
  }

  private Consumer<CommandSource> createIsland(IslandSchematic schematic) {
    return src -> {
      if (src instanceof Player) {
        Player player = (Player) src;
        if (hasPlayerReachedMaxIslands(player)) {
          player.sendMessage(Text.of(TextColors.RED, "You have reached your maximum number of islands!"));
        }

        player.sendMessage(Text.of(
            TextColors.GREEN, "Your island is being created.",
            PLUGIN.getConfig().getMiscConfig().isTeleportOnCreate() ? " You will be teleported shortly." : Text.EMPTY
        ));

        try {
          Island island = new Island(player, schematic);
          clearIslandMemberInventories(island, Permissions.KEEP_INV_PLAYER_CREATE, Permissions.KEEP_INV_ENDERCHEST_CREATE);
        } catch (CreateIslandException e) {
          player.sendMessage(Text.of(TextColors.RED, "Unable to create island!", Text.NEW_LINE, TextColors.RESET, e.getText()));
        }
      }
    };
  }
}
