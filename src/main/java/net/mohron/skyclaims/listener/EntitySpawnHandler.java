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
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.entity.living.Ambient;
import org.spongepowered.api.entity.living.Aquatic;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;

public class EntitySpawnHandler {

    private static final SkyClaims PLUGIN = SkyClaims.getInstance();

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        SkyClaimsTimings.ENTITY_SPAWN.startTimingIfSync();
        if (event instanceof DropItemEvent) {
            SkyClaimsTimings.ENTITY_SPAWN.abort();
            return;
        }

        event.filterEntityLocations(l -> l.getExtent().equals(PLUGIN.getConfig().getWorldConfig().getWorld()));

        event.filterEntities(entity -> {
            Island island = SkyClaims.islands.values().stream()
                .filter(i -> i.contains(entity.getLocation())).findAny().orElse(null);
            if (island == null) {
                return true;
            }
            int limit;
            int hostile = island.getHostileEntities().size();
            int passive = island.getPassiveEntities().size();
            if (entity instanceof Monster) {
                limit = Options.getMaxHostileSpawns(island.getOwnerUniqueId());
                if (limit > 0 && hostile >= limit) {
                    return false;
                }
            } else if (entity instanceof Animal || entity instanceof Aquatic || entity instanceof Ambient) {
                limit = Options.getMaxPassiveSpawns(island.getOwnerUniqueId());
                if (limit > 0 && passive >= limit) {
                    return false;
                }
            } else {
                return true;
            }
            limit = Options.getMaxSpawns(island.getOwnerUniqueId());
            return (limit <= 0 || limit >= hostile + passive);
        });
        SkyClaimsTimings.ENTITY_SPAWN.stopTimingIfSync();;
    }

}
