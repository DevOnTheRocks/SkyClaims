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

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimFlag;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.World;

public class WorldLoadHandler {

    private static final SkyClaims PLUGIN = SkyClaims.getInstance();

    @Listener(order = Order.LAST)
    public void onWorldLoad(LoadWorldEvent event, @Getter(value = "getTargetWorld") World targetWorld) {
        SkyClaimsTimings.WORLD_LOAD.startTimingIfSync();
        World world = PLUGIN.getConfig().getWorldConfig().getWorld();

        if (targetWorld.equals(world)) {
            ClaimManager claimManager = PLUGIN.getGriefPrevention().getClaimManager(world);
            Claim wilderness = claimManager.getWildernessClaim();
            Subject subject = Sponge.getServiceManager().provideUnchecked(PermissionService.class).getDefaults();
            String target = "any:any";

            if (wilderness.getPermissionValue(subject, ClaimFlag.BLOCK_BREAK, target) != Tristate.FALSE) {
                wilderness.setPermission(subject, ClaimFlag.BLOCK_BREAK, target, Tristate.FALSE, PLUGIN.getCause())
                    .whenComplete((result, throwable) -> {
                        if (result.successful()) {
                            PLUGIN.getLogger().info(String.format("Set %s flag in wilderness (%s) to false.", ClaimFlag.BLOCK_BREAK, world.getName
                                ()));
                        } else {
                            PLUGIN.getLogger().info(result.getResultType().toString());
                        }
                    });
            }

            if (wilderness.getPermissionValue(subject, ClaimFlag.BLOCK_PLACE, target) != Tristate.FALSE) {
                wilderness.setPermission(subject, ClaimFlag.BLOCK_PLACE, target, Tristate.FALSE, PLUGIN.getCause())
                    .whenComplete((result, throwable) -> {
                        if (result.successful()) {
                            PLUGIN.getLogger().info(String.format("Set %s flag in wilderness (%s) to false.", ClaimFlag.BLOCK_PLACE, world.getName
                                ()));
                        } else {
                            PLUGIN.getLogger().info(result.getResultType().toString());
                        }
                    });
            }
        }
        SkyClaimsTimings.WORLD_LOAD.stopTimingIfSync();
    }
}
