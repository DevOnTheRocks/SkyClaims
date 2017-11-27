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

package net.mohron.skyclaims.listener;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class ClientJoinHandler {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  @Listener
  public void onClientJoin(ClientConnectionEvent.Join event, @Root Player player) {
    if (PLUGIN.getConfig().getMiscConfig().createIslandOnJoin() && !Island
        .hasIsland(player.getUniqueId())) {
      createIslandOnJoin(player);
    }
    deliverInvites(player);
  }

  private void createIslandOnJoin(Player player) {
    SkyClaimsTimings.CREATE_ISLAND_ON_JOIN.startTimingIfSync();

    Sponge.getScheduler().createTaskBuilder()
        .execute(src -> {
          try {
            new Island(player, Options.getDefaultSchematic(player.getUniqueId()));
            PLUGIN.getLogger().info("Automatically created an island for {}.", player.getName());
          } catch (CreateIslandException e) {
            // Oh well, we tried!
            PLUGIN.getLogger()
                .warn(String.format("Failed to create an island on join for %s.", player.getName()),
                    e);
          }
        })
        .delayTicks(40)
        .submit(PLUGIN);

    SkyClaimsTimings.CREATE_ISLAND_ON_JOIN.stopTimingIfSync();
  }

  private void deliverInvites(Player player) {
    SkyClaimsTimings.DELIVER_INVITES.startTimingIfSync();

    int invites = PLUGIN.getInviteService().getInviteCount(player);
    if (invites > 0) {
      player.sendMessage(Text.of(
          TextColors.GRAY, "You have ",
          TextColors.LIGHT_PURPLE, invites,
          TextColors.GRAY, " waiting for your response! ",
          TextColors.WHITE, "[",
          Text.builder("OPEN")
              .color(TextColors.GREEN)
              .onClick(
                  TextActions.executeCallback(PLUGIN.getInviteService().listIncomingInvites())),
          TextColors.WHITE, "]"
      ));
    }

    SkyClaimsTimings.DELIVER_INVITES.stopTimingIfSync();
  }
}
