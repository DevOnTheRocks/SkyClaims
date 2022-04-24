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
package net.mohron.skyclaims.listener;

import java.util.Optional;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.World;

public class RespawnHandler {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  @Listener
  public void onPlayerRespawn(RespawnPlayerEvent event, @Root Player player) {
    World world = PLUGIN.getConfig().getWorldConfig().getWorld();

    if (!event.isDeath() || !world.equals(event.getFromTransform().getExtent())) {
      return;
    }

    final Optional<Island> island = IslandManager.getByTransform(event.getFromTransform());
    if (island.isPresent() && island.get().isMember(player)) {
      Sponge.getTeleportHelper()
          .getSafeLocation(island.get().getSpawn().getLocation())
          .ifPresent(spawn -> event.setToTransform(island.get().getSpawn().setLocation(spawn)));
    }
  }
}
