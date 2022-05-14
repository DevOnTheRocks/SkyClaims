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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.command.argument.IslandSortType;
import net.mohron.skyclaims.command.argument.IslandSortType.Order;
import net.mohron.skyclaims.config.type.MiscConfig;
import net.mohron.skyclaims.permissions.Permissions;
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

public class CommandList extends CommandBase {

  public static final String HELP_TEXT = "display a list of the current islands.";
  private static final Text ISLAND = LinearComponents.linear("island");
  private static final Text SORT_TYPE = LinearComponents.linear("sort type");
  private static final Text SORT_ORDER = LinearComponents.linear("sort order");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_LIST)
        .description(LinearComponents.linear(HELP_TEXT))
        .arguments(
            GenericArguments.optionalWeak(Arguments.island(ISLAND)),
            GenericArguments.optional(GenericArguments.requiringPermission(GenericArguments.enumValue(SORT_TYPE, IslandSortType.class), Permissions.COMMAND_LIST_SORT)),
            GenericArguments.optional(GenericArguments.enumValue(SORT_ORDER, Order.class))
        )
        .executor(new CommandList())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "list");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "islandlist");
      PLUGIN.getLogger().debug("Registered command: CommandList");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandList", e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (IslandManager.ISLANDS.isEmpty()) {
      src.sendMessage(LinearComponents.linear(NamedTextColor.RED, "There are currently no islands!"));
      return CommandResult.empty();
    }
    Player player = (src instanceof Player) ? (Player) src : null;
    Collection<Island> islands = args.<UUID>getAll(ISLAND).stream()
        .map(uuid -> IslandManager.ISLANDS.get(uuid)).collect(Collectors.toList());

    MiscConfig config = PLUGIN.getConfig().getMiscConfig();
    IslandSortType primaryListSort = config.getPrimaryListSort();
    IslandSortType sortType = args.<IslandSortType>getOne(SORT_TYPE).orElse(primaryListSort);
    Order order = args.<Order>getOne(SORT_ORDER).orElse(sortType.getOrder());
    Comparator<Island> sortFunction;
    Text sortText;
    if (primaryListSort != IslandSortType.NONE && sortType != primaryListSort) {
      sortFunction = Comparator.comparing(primaryListSort.getSortFunction(), primaryListSort.getOrder().getComparator())
          .thenComparing(sortType.getSortFunction(), order.getComparator());
      sortText = LinearComponents.linear(
          NamedTextColor.GRAY, primaryListSort.name(), primaryListSort.getOrder().toText(),
          NamedTextColor.AQUA, ", ",
          NamedTextColor.GRAY, sortType.name(), order.toText()
      );
    } else {
      sortFunction = Comparator.comparing(sortType.getSortFunction());
      sortText = LinearComponents.linear(NamedTextColor.GRAY, sortType.name(), order.toText());
    }

    boolean showUnlocked = src.hasPermission(Permissions.COMMAND_LIST_UNLOCKED);
    boolean showAll = src.hasPermission(Permissions.COMMAND_LIST_ALL);

    if (islands.isEmpty()) {
      islands = IslandManager.ISLANDS.values();
    }

    List<Text> listText = islands.stream()
        .filter(i -> player == null || i.isMember(player) || !i.isLocked() && showUnlocked || showAll)
        .sorted(sortFunction)
        .map(island -> LinearComponents.linear(
            getAccess(island, src),
            island.getName().toBuilder()
                .onHover(TextActions.showText(LinearComponents.linear("Click here to view island info")))
                .onClick(TextActions.executeCallback(
                    CommandUtil.createCommandConsumer(src, "islandinfo", island.getUniqueId().toString(), createReturnConsumer())
                )),
            getCoords(island, src)
        ))
        .collect(Collectors.toList());

    if (listText.isEmpty()) {
      src.sendMessage(LinearComponents.linear(NamedTextColor.RED, "There are no islands to display!"));
    } else if (src instanceof Player) {
      PaginationList.builder()
          .title(LinearComponents.linear(NamedTextColor.AQUA, "Island List | ", NamedTextColor.LIGHT_PURPLE, listText.size(), NamedTextColor.AQUA, " | ", sortText))
          .padding(LinearComponents.linear(NamedTextColor.AQUA, TextStyles.STRIKETHROUGH, "-"))
          .contents(listText)
          .sendTo(src);
    } else {
      listText.forEach(src::sendMessage);
    }

    return CommandResult.success();
  }

  private static Consumer<CommandSource> createReturnConsumer() {
    return src -> src.sendMessage(LinearComponents.linear(
        TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "islandlist", "", null)),
        Component.newline(), NamedTextColor.WHITE, "[",
        NamedTextColor.AQUA, "Return to Island List",
        NamedTextColor.WHITE, "]", Component.newline()
    ));
  }

  private Text getAccess(Island island, CommandSource src) {
    Text access;
    Text hover = Text.EMPTY;
    if (src instanceof Player && island.getOwnerUniqueId().equals(((Player) src).getUniqueId())) {
      access = LinearComponents.linear(NamedTextColor.BLUE, "O");
      hover = LinearComponents.linear(NamedTextColor.BLUE, "You own this Island", Component.newline());
    } else if (src instanceof Player && island.isManager((Player) src)) {
      access = LinearComponents.linear(NamedTextColor.GOLD, "M");
      hover = LinearComponents.linear(NamedTextColor.GOLD, "You are a Manager on this Island", Component.newline());
    } else if (src instanceof Player && island.isMember((Player) src)) {
      access = LinearComponents.linear(NamedTextColor.YELLOW, "T");
      hover = LinearComponents.linear(NamedTextColor.YELLOW, "You are Trusted on this Island", Component.newline());
    } else {
      access = island.isLocked() ? LinearComponents.linear(NamedTextColor.RED, "L") : LinearComponents.linear(NamedTextColor.GREEN, "U");
    }

    hover = hover.concat(LinearComponents.linear(island.getName(), NamedTextColor.WHITE, " is ",
        island.isLocked() ? LinearComponents.linear(NamedTextColor.RED, "Locked") : LinearComponents.linear(NamedTextColor.GREEN, "Unlocked")
    ));

    return LinearComponents.linear(
        NamedTextColor.WHITE, " [",
        access.toBuilder().onHover(TextActions.showText(hover)).onClick(TextActions.executeCallback(toggleLock(island))),
        NamedTextColor.WHITE, "] "
    );
  }

  private Consumer<CommandSource> toggleLock(Island island) {
    return src -> {
      if (src instanceof Player
          && island.isManager((Player) src)
          && src.hasPermission(Permissions.COMMAND_LOCK)
          || src.hasPermission(Permissions.COMMAND_LOCK_OTHERS)) {
        island.setLocked(!island.isLocked());
        src.sendMessage(LinearComponents.linear(island.getName(), NamedTextColor.GRAY, " is now ",
            Text.builder((island.isLocked()) ? "Locked" : "Unlocked")
                .color((island.isLocked()) ? NamedTextColor.RED : NamedTextColor.GREEN)
                .onClick(TextActions.executeCallback(toggleLock(island))),
            NamedTextColor.GRAY, "!"
        ));
      }
    };
  }

  private Text getCoords(Island island, CommandSource src) {
    Text coords = LinearComponents.linear(NamedTextColor.GRAY, " (",
        NamedTextColor.LIGHT_PURPLE, island.getRegion().getX(),
        NamedTextColor.GRAY, ", ",
        NamedTextColor.LIGHT_PURPLE, island.getRegion().getZ(),
        NamedTextColor.GRAY, ")");
    return !island.isLocked() || src instanceof Player && island.isMember((Player) src) || src
        .hasPermission(Permissions.COMMAND_SPAWN_OTHERS)
        ? coords.toBuilder()
        .onHover(TextActions.showText(LinearComponents.linear("Click here to teleport to this island")))
        .onClick(TextActions.executeCallback(CommandUtil.createTeleportConsumer(src, island.getSpawn().getLocation())))
        .build()
        : coords;
  }
}
