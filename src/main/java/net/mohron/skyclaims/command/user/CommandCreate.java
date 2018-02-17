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
import org.spongepowered.api.text.action.TextActions;
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
    if (IslandManager.hasIsland(player.getUniqueId())) {
      throw new CommandException(Text.of(TextColors.RED, "You already have an island!"));
    }
    boolean canCreate = Options.getMaxIslands(player.getUniqueId()) < 1
        || Options.getMaxIslands(player.getUniqueId()) - IslandManager.getTotalIslands(player) > 0;
    if (!canCreate) {
      throw new CommandException(Text.of(TextColors.RED, "You have reached your maximum number of islands!"));
    }

    Optional<IslandSchematic> schematic = args.getOne(SCHEMATIC);
    if (schematic.isPresent()) {
      return createIsland(player, schematic.get());
    } else if (PLUGIN.getConfig().getMiscConfig().isListSchematics() && PLUGIN.getSchematicManager().getSchematics().size() > 1) {
      return listSchematics(player, s -> s.getText().toBuilder().onClick(TextActions.executeCallback(createIsland(s))).build());
    } else {
      return createIsland(
          player,
          Options.getDefaultSchematic(player.getUniqueId())
              .orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "Unable to load default schematic!")))
      );
    }
  }

  private CommandResult createIsland(Player player, IslandSchematic schematic) throws CommandException {
    player.sendMessage(Text.of(
        TextColors.GREEN, "Your island is being created.",
        PLUGIN.getConfig().getMiscConfig().isTeleportOnCreate() ? " You will be teleported shortly." : Text.EMPTY
    ));

    try {
      new Island(player, schematic);
      return CommandResult.success();
    } catch (CreateIslandException e) {
      throw new CommandException(Text.of(TextColors.RED, "Unable to create island!", Text.NEW_LINE, TextColors.RESET, e.getText()));
    }
  }

  private Consumer<CommandSource> createIsland(IslandSchematic schematic) {
    return src -> {
      if (src instanceof Player) {
        Player player = (Player) src;
        if (IslandManager.hasIsland(player.getUniqueId())) {
          player.sendMessage(Text.of(TextColors.RED, "You already have an island!"));
          return;
        }
        try {
          player.sendMessage(Text.of(
              TextColors.GREEN, "Your island is being created.",
              PLUGIN.getConfig().getMiscConfig().isTeleportOnCreate() ? " You will be teleported shortly." : Text.EMPTY
          ));
          new Island(player, schematic);
        } catch (CreateIslandException e) {
          player.sendMessage(Text.of(TextColors.RED, "Unable to create island!", Text.NEW_LINE, TextColors.RESET, e.getText()));
        }
      }
    };
  }
}
