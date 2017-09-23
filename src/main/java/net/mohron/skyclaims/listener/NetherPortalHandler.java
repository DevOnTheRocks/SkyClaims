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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class NetherPortalHandler {

    @Listener
    public void onPortalUse(MoveEntityEvent.Teleport.Portal event, @Getter("getTargetEntity") Player player) {
        SkyClaimsTimings.PORTAL_HANDLER.startTimingIfSync();

        World from = event.getFromTransform().getExtent();
        World to = event.getToTransform().getExtent();

        if (from.getDimension().getType().equals(DimensionTypes.NETHER)) {
            Optional<Island> island = Island.get(event.getToTransform().getLocation().setExtent(to));
            event.setUsePortalAgent(false);

            if (island.isPresent() && (!island.get().isLocked() || island.get().isMember(player))) {
                // 1. Intended island's spawn
                event.setToTransform(island.get().getSpawn());
            } else {
                // 2. Your island's spawn
                island = Island.getByOwner(player.getUniqueId());

                if (island.isPresent()) {
                    event.setToTransform(island.get().getSpawn());
                } else {
                    // 3. World Spawn
                    event.getToTransform().setLocation(to.getSpawnLocation());
                }
            }
        } /*else if (to.getDimension().getType().equals(DimensionTypes.NETHER)) {
            // TODO: Create 1 to 1 Nether portals
            Optional<Island> island = Island.get(event.getFromTransform().getLocation());
            if (island.isPresent()) {

            }
        }*/

        SkyClaimsTimings.PORTAL_HANDLER.stopTimingIfSync();
    }
}