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

import com.google.common.collect.Lists;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class CommandInfo extends CommandBase {

  public static final String HELP_TEXT = "display detailed information on your island.";
  private static final Text ISLAND = LinearComponents.linear("island");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_INFO)
        .description(LinearComponents.linear(HELP_TEXT))
        .arguments(GenericArguments.optional(Arguments.island(ISLAND)))
        .executor(new CommandInfo())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "info");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "islandinfo");
      PLUGIN.getLogger().debug("Registered command: CommandInfo");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandInfo", e);
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    List<Island> islands = Lists.newArrayList();

    if (src instanceof Player && !args.hasAny(ISLAND)) {
      islands.add(IslandManager.getByLocation(((Player) src).getLocation())
          .orElseThrow(() -> new CommandException(
              LinearComponents.linear(NamedTextColor.RED, "You must be on an island to use this command.")))
      );
    } else {
      Collection<UUID> islandIds = args.getAll(ISLAND);
      islandIds.forEach(i -> IslandManager.get(i).ifPresent(islands::add));
      if (islands.size() > 1) {
        return listIslands();
      }
    }

    SimpleDateFormat sdf = PLUGIN.getConfig().getMiscConfig().getDateFormat();

    List<Text> infoText = islands.stream()
        .map(island -> LinearComponents.linear(
            (src instanceof Player) ? getAdminShortcuts(src, island) : Text.EMPTY,
            NamedTextColor.YELLOW, "Name", NamedTextColor.WHITE, " : ", island.getName(),
            getLocked(island), Component.newline(),
            NamedTextColor.YELLOW, "Members", NamedTextColor.WHITE, " : ", getMembers(island), Component.newline(),
            NamedTextColor.YELLOW, "Size", NamedTextColor.WHITE, " : ", NamedTextColor.LIGHT_PURPLE, island.getWidth(),
            NamedTextColor.GRAY, "x", NamedTextColor.LIGHT_PURPLE, island.getWidth(), Component.newline(),
            NamedTextColor.YELLOW, "Entities", NamedTextColor.WHITE, " : ", getEntities(island), Component.newline(),
            NamedTextColor.YELLOW, "Spawn", NamedTextColor.WHITE, " : ", getSpawn(island), Component.newline(),
            NamedTextColor.YELLOW, "Created", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY, sdf.format(island.getDateCreated()), Component.newline(),
            NamedTextColor.YELLOW, "Last Active", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY, sdf.format(island.getDateLastActive()), Component.newline(),
            NamedTextColor.YELLOW, "UUID", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY,
            island.getUniqueId(), Component.newline(),
            island.getClaim().isPresent() ? LinearComponents.linear(
                NamedTextColor.YELLOW, "Claim", NamedTextColor.WHITE, " : ", NamedTextColor.GRAY, Text.builder(
                    island.getClaimUniqueId().toString())
                    .onClick(TextActions
                        .executeCallback(CommandUtil.createCommandConsumer(src, "claiminfo",
                            island.getClaimUniqueId().toString(),
                            createReturnConsumer(src, island.getUniqueId().toString())
                        )))
                    .onHover(TextActions.showText(LinearComponents.linear("Click to view claim info")))
            ) : Text.EMPTY
        ))
        .collect(Collectors.toList());

    PaginationList.builder()
        .title(LinearComponents.linear(NamedTextColor.AQUA, "Island Info"))
        .padding(LinearComponents.linear(NamedTextColor.AQUA, TextStyles.STRIKETHROUGH, "-"))
        .contents(infoText)
        .sendTo(src);

    return CommandResult.success();
  }

  private static CommandResult listIslands() throws CommandException {
    throw new CommandException(
        LinearComponents.linear(NamedTextColor.RED, "Command does not support players with multiple islands yet!"));
  }

  private static Text getAdminShortcuts(CommandSource src, Island island) {
    Text teleport = src.hasPermission(Permissions.COMMAND_SPAWN_OTHERS) ? LinearComponents.linear(
        NamedTextColor.WHITE, "[",
        NamedTextColor.GOLD, Text.builder("Teleport")
            .onHover(TextActions.showText(LinearComponents.linear("Click to teleport to this island")))
            .onClick(TextActions.executeCallback(
                CommandUtil.createTeleportConsumer(src, island.getSpawn().getLocation()))),
        NamedTextColor.WHITE, "] "
    ) : Text.EMPTY;

    Text transfer = src.hasPermission(Permissions.COMMAND_TRANSFER) ? LinearComponents.linear(
        NamedTextColor.WHITE, "[",
        NamedTextColor.GOLD, Text.builder("Transfer")
            .onHover(TextActions.showText(LinearComponents.linear("Click to transfer this island")))
            .onClick(TextActions.suggestCommand("/isa transfer " + island.getOwnerName() + " ?")),
        NamedTextColor.WHITE, "] "
    ) : Text.EMPTY;

    Text delete = src.hasPermission(Permissions.COMMAND_DELETE_OTHERS) ? LinearComponents.linear(
        NamedTextColor.WHITE, "[",
        NamedTextColor.GOLD, Text.builder("Delete")
            .onHover(TextActions.showText(LinearComponents.linear("Click to delete this island")))
            .onClick(TextActions.executeCallback(consumer -> {
              src.sendMessage(LinearComponents.linear(
                  NamedTextColor.GREEN, "Are you sure you want to delete ",
                  island.getName(), NamedTextColor.GREEN, " island?",
                  Component.newline(),
                  NamedTextColor.WHITE, "[",
                  Text.builder("YES").color(NamedTextColor.GREEN)
                      .onClick(TextActions.executeCallback(s -> {
                        island.clear();
                        island.delete();
                        src.sendMessage(LinearComponents.linear(island.getName(), " has been deleted!"));
                      })),
                  NamedTextColor.WHITE, "] [",
                  Text.builder("NO")
                      .color(NamedTextColor.RED)
                      .onClick(TextActions.executeCallback(
                          s -> s.sendMessage(LinearComponents.linear("Island deletion canceled!")))),
                  NamedTextColor.WHITE, "]"
              ));
            })),
        NamedTextColor.WHITE, "] "
    ) : Text.EMPTY;

    Text expand = src.hasPermission(Permissions.COMMAND_EXPAND_OTHERS) ? LinearComponents.linear(
        NamedTextColor.WHITE, "[",
        NamedTextColor.GOLD, Text.builder("Expand")
            .onHover(TextActions.showText(LinearComponents.linear("Click to expand this island's width by ", NamedTextColor.LIGHT_PURPLE, 2)))
            .onClick(TextActions.executeCallback(consumer -> {
              if (island.getWidth() < 512) {
                if (island.expand(1)) {
                  src.sendMessage(LinearComponents.linear(
                      island.getName(),
                      NamedTextColor.GREEN, " has been expanded to ",
                      NamedTextColor.LIGHT_PURPLE, island.getWidth(),
                      NamedTextColor.GRAY, "x",
                      NamedTextColor.LIGHT_PURPLE, island.getWidth(),
                      NamedTextColor.GREEN, "."
                  ));
                } else {
                  src.sendMessage(LinearComponents.linear(
                      NamedTextColor.RED, "An error occurred while expanding ",
                      island.getName(),
                      NamedTextColor.RED, "!"
                  ));
                }
              } else {
                src.sendMessage(LinearComponents.linear(island.getOwnerName(), NamedTextColor.RED, "'s island cannot be expanded further!"));
              }
            })),
        NamedTextColor.WHITE, "] "
    ) : Text.EMPTY;

    Text shrink = src.hasPermission(Permissions.COMMAND_EXPAND_OTHERS) ? LinearComponents.linear(
        NamedTextColor.WHITE, "[",
        NamedTextColor.GOLD, Text.builder("Shrink")
            .onHover(TextActions.showText(LinearComponents.linear("Click to shrink this island's width by ", NamedTextColor.LIGHT_PURPLE, 2)))
            .onClick(TextActions.executeCallback(consumer -> {
              if (island.shrink(1)) {
                src.sendMessage(LinearComponents.linear(
                    island.getName(),
                    NamedTextColor.GREEN, " has been shrunk to ",
                    NamedTextColor.LIGHT_PURPLE, island.getWidth(),
                    NamedTextColor.GRAY, "x",
                    NamedTextColor.LIGHT_PURPLE, island.getWidth(),
                    NamedTextColor.GREEN, "."
                ));
              } else {
                src.sendMessage(LinearComponents.linear(
                    NamedTextColor.RED, "An error occurred while shrinking ",
                    island.getName(),
                    NamedTextColor.RED, "!"
                ));
              }
            })),
        NamedTextColor.WHITE, "] "
    ) : Text.EMPTY;

    return (teleport.isEmpty() && transfer.isEmpty() && delete.isEmpty() && expand.isEmpty() && shrink.isEmpty()) ? Text.EMPTY : LinearComponents.linear(
        NamedTextColor.GOLD, "Admin", NamedTextColor.WHITE, " : ",
        teleport, transfer, delete, expand, shrink, Component.newline()
    );
  }

  private static Text getMembers(Island island) {
    List<Text> members = Lists.newArrayList();
    members.add(PrivilegeType.OWNER.format(island.getOwnerName()));
    for (String manager : island.getManagerNames()) {
      members.add(PrivilegeType.MANAGER.format(manager));
    }
    for (String member : island.getMemberNames()) {
      members.add(PrivilegeType.MEMBER.format(member));
    }
    return Text.joinWith(LinearComponents.linear(NamedTextColor.GRAY, ", "), members);

  }

  private static Consumer<CommandSource> createReturnConsumer(CommandSource src, String arguments) {
    return consumer -> {
      Text returnCommand = Text.builder()
          .append(LinearComponents.linear(
              NamedTextColor.WHITE, Component.newline(), "[", NamedTextColor.AQUA, "Return to Island Info",
              NamedTextColor.WHITE,
              "]", Component.newline()
          ))
          .onClick(TextActions.executeCallback(
              CommandUtil.createCommandConsumer(src, "islandinfo", arguments, null)))
          .build();
      src.sendMessage(returnCommand);
    };
  }

  private Text getLocked(Island island) {
    return LinearComponents.linear(NamedTextColor.WHITE, " [",
        Text.builder(island.isLocked() ? "L" : "U")
            .color(island.isLocked() ? NamedTextColor.RED : NamedTextColor.GREEN)
            .onHover(TextActions.showText(island.isLocked()
                ? LinearComponents.linear(NamedTextColor.RED, "LOCKED")
                : LinearComponents.linear(NamedTextColor.GREEN, "UNLOCKED")
            ))
            .onClick(TextActions.executeCallback(toggleLock(island))),
        NamedTextColor.WHITE, "] ");
  }

  private Consumer<CommandSource> toggleLock(Island island) {
    return src -> {
      if (src instanceof Player
          && ((Player) src).getUniqueId().equals(island.getOwnerUniqueId())
          && src.hasPermission(Permissions.COMMAND_LOCK)
          || src.hasPermission(Permissions.COMMAND_LOCK_OTHERS)) {
        island.setLocked(!island.isLocked());
        src.sendMessage(LinearComponents.linear(island.getName(), NamedTextColor.GREEN, " is now ",
            Text.builder((island.isLocked()) ? "LOCKED" : "UNLOCKED")
                .color((island.isLocked()) ? NamedTextColor.RED : NamedTextColor.GREEN)
                .onClick(TextActions.executeCallback(toggleLock(island))),
            NamedTextColor.GREEN, "!"
        ));
      }
    };
  }

  private Text getSpawn(Island island) {
    return LinearComponents.linear(
        NamedTextColor.LIGHT_PURPLE, island.getSpawn().getLocation().getBlockX(), NamedTextColor.GRAY, "x ",
        NamedTextColor.LIGHT_PURPLE, island.getSpawn().getLocation().getBlockY(), NamedTextColor.GRAY, "y ",
        NamedTextColor.LIGHT_PURPLE, island.getSpawn().getLocation().getBlockZ(), NamedTextColor.GRAY, "z"
    );
  }

  private Text getEntities(Island island) {
    boolean limitSpawning = PLUGIN.getConfig().getEntityConfig().isLimitSpawning();
    int max = Options.getMaxSpawns(island.getOwnerUniqueId());
    int maxHostile = Options.getMaxHostileSpawns(island.getOwnerUniqueId());
    int hostile = island.getHostileEntities().size();
    int maxPassive = Options.getMaxPassiveSpawns(island.getOwnerUniqueId());
    int passive = island.getPassiveEntities().size();

    return LinearComponents.linear(
        LinearComponents.linear(TextActions.showText(LinearComponents.linear(
            NamedTextColor.GRAY, "Hostile", NamedTextColor.WHITE, " : ", NamedTextColor.LIGHT_PURPLE, hostile,
            (limitSpawning && maxHostile > 0)
                ? LinearComponents.linear(NamedTextColor.GRAY, " (", NamedTextColor.RED, maxHostile, NamedTextColor.GRAY, ")")
                : Text.EMPTY,
            Component.newline(),
            NamedTextColor.GRAY, "Passive", NamedTextColor.WHITE, " : ", NamedTextColor.LIGHT_PURPLE, passive,
            (limitSpawning && maxPassive > 0)
                ? LinearComponents.linear(NamedTextColor.GRAY, " (", NamedTextColor.RED, maxPassive, NamedTextColor.GRAY, ")")
                : Text.EMPTY
            )),
            NamedTextColor.GRAY, "Living", NamedTextColor.WHITE, " : ", NamedTextColor.LIGHT_PURPLE,
            hostile + passive, NamedTextColor.WHITE,
            (limitSpawning && max > 0)
                ? LinearComponents.linear(NamedTextColor.GRAY, " (", NamedTextColor.RED, max, NamedTextColor.GRAY, ")")
                : Text.EMPTY, ", "
        ),
        NamedTextColor.GRAY, "Item", NamedTextColor.WHITE, " : ", NamedTextColor.LIGHT_PURPLE,
        island.getItemEntities().size(), NamedTextColor.WHITE, ", ",
        NamedTextColor.GRAY, "Tile", NamedTextColor.WHITE, " : ", NamedTextColor.LIGHT_PURPLE,
        island.getTileEntities().size());
  }
}
