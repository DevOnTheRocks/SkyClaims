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

package net.mohron.skyclaims.command.schematic;

import java.util.Optional;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.argument.Arguments;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.schematic.IslandSchematic;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class CommandSchematicSetIcon extends CommandBase {

  public static final String HELP_TEXT = "used to set the menu icon for a schematic";
  private static final Text SCHEMATIC = Text.of("schematic");
  private static final Text ICON = Text.of("icon");

  public static final CommandSpec commandSpec = CommandSpec.builder()
      .permission(Permissions.COMMAND_SCHEMATIC_SET_ICON)
      .description(Text.of(HELP_TEXT))
      .arguments(
          Arguments.schematic(SCHEMATIC),
          GenericArguments.optional(GenericArguments.catalogedElement(ICON, ItemType.class))
      )
      .executor(new CommandSchematicSetIcon())
      .build();

  public static void register() {
    try {
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandSchematicSetIcon");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandSchematicSetIcon", e);
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    IslandSchematic schematic = args.<IslandSchematic>getOne(SCHEMATIC)
        .orElseThrow(
            () -> new CommandException(Text.of(TextColors.RED, "You must provide a schematic to use this command!")));
    Optional<ItemType> icon = args.getOne(ICON);

    if (icon.isPresent() || (src instanceof Player
        && ((Player) src).getItemInHand(HandTypes.MAIN_HAND).isPresent()
        && !((Player) src).getItemInHand(HandTypes.MAIN_HAND).get().isEmpty())) {
      ItemType itemType = icon.orElse(((Player) src).getItemInHand(HandTypes.MAIN_HAND).get().getType());
      schematic.setIcon(itemType);
      ItemStackSnapshot snapshot = ItemStack.of(itemType).createSnapshot();
      src.sendMessage(Text.of(
          TextColors.GREEN, "Successfully updated schematic icon to ",
          snapshot.get(Keys.DISPLAY_NAME).orElse(Text.of(itemType.getTranslation())).toBuilder()
              .color(TextColors.WHITE)
              .onHover(TextActions.showItem(snapshot)),
          TextColors.GREEN, "."
      ));
    } else {
      schematic.setIcon(null);
      src.sendMessage(Text.of(TextColors.GREEN, "Successfully removed schematic icon."));
    }

    if (PLUGIN.getSchematicManager().save(schematic)) {
      return CommandResult.success();
    } else {
      throw new CommandException(Text.of(TextColors.RED, "Failed to update schematic."));
    }
  }
}
