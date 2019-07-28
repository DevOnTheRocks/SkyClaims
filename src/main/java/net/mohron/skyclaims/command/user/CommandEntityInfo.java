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

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.mohron.skyclaims.command.CommandBase.IslandCommand;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class CommandEntityInfo extends IslandCommand {

  public static final String HELP_TEXT = "display details about the entities on an island.";

  private enum Category {SUMMARY, PASSIVE, HOSTILE, TILE}

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_ENTITY_INFO)
        .description(Text.of(HELP_TEXT))
        .arguments(GenericArguments.optional(Arguments.island(ISLAND)))
        .executor(new CommandEntityInfo())
        .build();

    try {
      CommandIsland.addSubCommand(commandSpec, "entity");
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandEntityInfo");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandEntityInfo", e);
    }
  }

  @Override
  public CommandResult execute(Player player, Island island, CommandContext args) throws CommandException {

    sendEntityInfo(island, Category.SUMMARY).accept(player);

    return CommandResult.success();
  }

  private Consumer<CommandSource> sendEntityInfo(Island island, Category category) {
    return src -> {
      List<Text> info = Lists.newArrayList();

      switch (category) {
        case SUMMARY:
          info.add(Text.of(
              TextColors.YELLOW, "Passive",
              TextColors.WHITE, " : ",
              TextColors.LIGHT_PURPLE, island.getPassiveEntities().size()
          ));
          info.add(Text.of(
              TextColors.YELLOW, "Hostile",
              TextColors.WHITE, " : ",
              TextColors.LIGHT_PURPLE, island.getHostileEntities().size()
          ));
          info.add(Text.of(
              TextColors.YELLOW, "Item",
              TextColors.WHITE, " : ",
              TextColors.LIGHT_PURPLE, island.getItemEntities().size()
          ));
          info.add(Text.of(
              TextColors.YELLOW, "Tile",
              TextColors.WHITE, " : ",
              TextColors.LIGHT_PURPLE, island.getTileEntities().size()
          ));
          info.add(Text.of(
              TextColors.YELLOW, "Total",
              TextColors.WHITE, " : ",
              TextColors.LIGHT_PURPLE, island.getEntities().size()
          ));
          break;
        case PASSIVE:
          island.getPassiveEntities().stream()
              .collect(Collectors.groupingBy(Entity::getType, Collectors.counting()))
              .forEach((e, c) -> info.add(getEntityDetails(e, c)));
          break;
        case HOSTILE:
          island.getHostileEntities().stream()
              .collect(Collectors.groupingBy(Entity::getType, Collectors.counting()))
              .forEach((e, c) -> info.add(getEntityDetails(e, c)));
          break;
        case TILE:
          island.getTileEntities().stream()
              .collect(Collectors.groupingBy(TileEntity::getType, Collectors.counting()))
              .forEach((e, c) -> info.add(getEntityDetails(e, c)));
          break;
      }

      PaginationList.builder()
          .title(Text.of(TextColors.AQUA, "Island Entity Info"))
          .padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
          .header(getMenuText(island, category))
          .contents(info)
          .sendTo(src);
    };
  }

  private Text getEntityDetails(CatalogType e, Long c) {
    return Text.of(
        TextColors.YELLOW, e.getName(),
        TextColors.WHITE, " : ",
        TextColors.LIGHT_PURPLE, c
    );
  }

  private Text getMenuText(Island island, Category category) {
    return Text.of(
        category == Category.SUMMARY ? TextColors.AQUA : TextColors.GRAY, "[",
        Text.builder("Summary")
            .color(category == Category.SUMMARY ? TextColors.GREEN : TextColors.GRAY)
            .onHover(TextActions.showText(Text.of("Click here to show entity summary")))
            .onClick(TextActions.executeCallback(sendEntityInfo(island, Category.SUMMARY))),
        category == Category.SUMMARY ? TextColors.AQUA : TextColors.GRAY, "] ",
        category == Category.PASSIVE ? TextColors.AQUA : TextColors.GRAY, "[",
        Text.builder("Passive")
            .color(category == Category.PASSIVE ? TextColors.GREEN : TextColors.GRAY)
            .onHover(TextActions.showText(Text.of("Click here to show passive entity details")))
            .onClick(TextActions.executeCallback(sendEntityInfo(island, Category.PASSIVE))),
        category == Category.PASSIVE ? TextColors.AQUA : TextColors.GRAY, "] ",
        category == Category.HOSTILE ? TextColors.AQUA : TextColors.GRAY, "[",
        Text.builder("Hostile")
            .color(category == Category.HOSTILE ? TextColors.GREEN : TextColors.GRAY)
            .onHover(TextActions.showText(Text.of("Click here to show hostile entity details")))
            .onClick(TextActions.executeCallback(sendEntityInfo(island, Category.HOSTILE))),
        category == Category.HOSTILE ? TextColors.AQUA : TextColors.GRAY, "] ",
        category == Category.TILE ? TextColors.AQUA : TextColors.GRAY, "[",
        Text.builder("Tile")
            .color(category == Category.TILE ? TextColors.GREEN : TextColors.GRAY)
            .onHover(TextActions.showText(Text.of("Click here to show tile entity details")))
            .onClick(TextActions.executeCallback(sendEntityInfo(island, Category.TILE))),
        category == Category.TILE ? TextColors.AQUA : TextColors.GRAY, "]"
    );
  }
}
