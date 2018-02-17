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

package net.mohron.skyclaims.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.schematic.IslandSchematic;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public abstract class CommandBase implements CommandExecutor {

  protected static final SkyClaims PLUGIN = SkyClaims.getInstance();

  public static abstract class IslandCommand extends CommandBase implements CommandRequirement.RequiresPlayerIsland {

    protected static final Text ISLAND = Text.of("island");

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (!(src instanceof Player)) {
        throw new CommandException(
            Text.of(TextColors.RED, "This command can only be used by a player!"));
      }
      if (!args.hasAny(ISLAND)) {
        return execute(
            (Player) src,
            IslandManager.get(((Player) src).getLocation())
                .orElseThrow(() -> new CommandException(
                    Text.of(TextColors.RED, "You must be on an island to use this command!"))),
            args);
      } else {
        List<Island> islands = Lists.newArrayList();
        args.<UUID>getAll(ISLAND).forEach(i -> IslandManager.get(i).ifPresent(islands::add));
        if (islands.size() == 1) {
          return execute((Player) src, islands.get(0), args);
        } else {
          throw new CommandException(
              Text.of(TextColors.RED, "Multiple island support not yet implemented!")
          );
        }
      }
    }
  }

  public static abstract class PlayerCommand extends CommandBase implements CommandRequirement.RequiresPlayer {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        return execute((Player) src, args);
      }
      throw new CommandException(
          Text.of(TextColors.RED, "This command can only be used by a player!"));
    }
  }

  public static abstract class LockCommand extends CommandBase implements CommandRequirement.RequiresIsland {

    private boolean lock;

    public LockCommand(boolean lock) {
      this.lock = lock;
    }

    protected static final Text ALL = Text.of("all");
    protected static final Text ISLAND = Text.of("island");

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (args.hasAny(ALL)) {
        return lockAll(src);
      }
      if (!args.hasAny(ISLAND)) {
        if (src instanceof Player) {
          Island island = IslandManager.get(((Player) src).getLocation()).orElseThrow(() -> new CommandException(Text.of(
                  TextColors.RED, "You must either provide an island argument or be on an island to use this command!"
          )));
          checkPerms(src, island);
          return execute(src, island, args);
        } else {
          throw new CommandException(Text.of(TextColors.RED, "An island argument is required when executed by a non-player!"));
        }
      } else {
        List<Island> islands = Lists.newArrayList();
        args.<UUID>getAll(ISLAND).forEach(i -> IslandManager.get(i).ifPresent(islands::add));
        if (islands.size() == 1) {
          checkPerms(src, islands.get(0));
          return execute(src, islands.get(0), args);
        } else {
          return lockIslands(src, args.getAll(ISLAND));
        }
      }
    }

    private void checkPerms(CommandSource src, Island island) throws CommandPermissionException {
      if (!(src instanceof Player && island.isManager((Player) src) || src
          .hasPermission(Permissions.COMMAND_LOCK_OTHERS))) {
        throw new CommandPermissionException(Text.of(
            TextColors.RED, "You do not have permission to ",
            lock ? "lock " : "unlock ",
            island.getName(), TextColors.RED, "!"
        ));
      }
    }

    private CommandResult lockIslands(CommandSource src, Collection<UUID> islandsIds) {
      ArrayList<Island> islands = Lists.newArrayList();
      islandsIds.forEach(i -> IslandManager.get(i).ifPresent(islands::add));
      islands.forEach(island -> {
        island.setLocked(lock);
        src.sendMessage(Text.of(
            island.getName(),
            TextColors.GREEN, " has been ",
            lock ? "locked" : "unlocked", "!"
        ));
      });
      return CommandResult.success();
    }

    private CommandResult lockAll(CommandSource src) {
      IslandManager.ISLANDS.values().forEach(i -> i.setLocked(lock));
      src.sendMessage(Text.of(
          TextColors.DARK_PURPLE, IslandManager.ISLANDS.size(),
          TextColors.GREEN, " islands have been ",
          lock ? "locked" : "unlocked", "!"
      ));
      return CommandResult.success();
    }
  }

  public static abstract class ListSchematicCommand extends PlayerCommand implements CommandRequirement.RequiresPlayer {

    protected static final Text SCHEMATIC = Text.of("schematic");

    protected CommandResult listSchematics(Player player, Function<IslandSchematic, Text> mapper) {
      boolean checkPerms = PLUGIN.getConfig().getPermissionConfig().isSeparateSchematicPerms();

      List<Text> schematics = PLUGIN.getSchematicManager().getSchematics().stream()
          .filter(s -> !checkPerms || player.hasPermission(Permissions.COMMAND_ARGUMENTS_SCHEMATICS + "." + s.getName().toLowerCase()))
          .map(mapper)
          .collect(Collectors.toList());

      PaginationList.builder()
          .title(Text.of(TextColors.AQUA, "Starter Islands"))
          .padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
          .contents(schematics)
          .sendTo(player);

      return CommandResult.empty();
    }
  }
}
