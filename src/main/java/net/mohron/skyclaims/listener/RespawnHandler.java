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

import net.mohron.skyclaims.SkyClaimsTimings;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class RespawnHandler {

    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event, @Root Player player) {
        SkyClaimsTimings.PLAYER_RESPAWN.startTimingIfSync();
        if (event.isBedSpawn() || !Island.hasIsland(player.getUniqueId())) {
            SkyClaimsTimings.PLAYER_RESPAWN.abort();
            return;
        }

        Island.getByOwner(player.getUniqueId())
            .ifPresent(island -> Sponge.getGame().getTeleportHelper().getSafeLocation(island.getSpawn().getLocation())
                .ifPresent(spawn -> event.setToTransform(new Transform<>(spawn))));
        SkyClaimsTimings.PLAYER_RESPAWN.stopTimingIfSync();
    }
}
