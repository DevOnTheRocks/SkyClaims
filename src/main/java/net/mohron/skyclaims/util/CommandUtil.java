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

package net.mohron.skyclaims.util;

import java.util.function.Consumer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CommandUtil {

  public static Consumer<CommandSource> createTeleportConsumer(CommandSource src, ServerLocation location) {
    return teleport -> {
      if (src instanceof Player) {
        Player player = (Player) src;
        ServerLocation safeLocation = Sponge.getGame().getTeleportHelper().getSafeLocation(location).orElse(null);
        if (safeLocation == null) {
          player.sendMessage(LinearComponents.linear(

                  NamedTextColor.RED, "Location is not safe. ",
              NamedTextColor.WHITE, "Are you sure you want to teleport here?", Component.newline(),
              Text.builder("[YES]")
                  .onHover(TextActions.showText(LinearComponents.linear("Click to teleport")))
                  .onClick(TextActions.executeCallback(createForceTeleportConsumer(player, location)))
                  .color(NamedTextColor.GREEN)
          ));
        } else {
          player.setLocation(safeLocation);
        }
      }
    };
  }

  public static Consumer<Task> createTeleportConsumer(Player player, ServerLocation location) {
    return teleport -> {
      ServerLocation safeLocation = Sponge.getGame().getTeleportHelper().getSafeLocation(location).orElse(null);
      if (safeLocation == null) {
        player.sendMessage(LinearComponents.linear(
            NamedTextColor.RED, "Location is not safe. ",
            NamedTextColor.WHITE, "Are you sure you want to teleport here?", Component.newline(),
            NamedTextColor.WHITE, "[",
            Text.builder("YES")
                .color(NamedTextColor.GREEN)
                .onHover(TextActions.showText(LinearComponents.linear("Click to teleport")))
                .onClick(TextActions.executeCallback(createForceTeleportConsumer(player, location))),
            NamedTextColor.WHITE, "]"
        ));
      } else {
        player.setLocation(safeLocation);
      }
    };
  }

  private static Consumer<CommandSource> createForceTeleportConsumer(Player player, ServerLocation location) {
    return teleport -> player.setLocation(location);
  }

  public static Consumer<CommandSource> createCommandConsumer(CommandSource src, String command, String arguments,
      Consumer<CommandSource> postConsumerTask) {
    return consumer -> {
      try {
        Sponge.getCommandManager().get(command).get().getCallable().process(src, arguments);
      } catch (CommandException e) {
        src.sendMessage(e.getText());
      }
      if (postConsumerTask != null) {
        postConsumerTask.accept(src);
      }
    };
  }
}