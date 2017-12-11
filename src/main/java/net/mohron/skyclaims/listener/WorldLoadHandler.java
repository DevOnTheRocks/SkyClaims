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
import org.spongepowered.api.world.World;

public class WorldLoadHandler {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  @Listener(order = Order.LAST)
  public void onWorldLoad(LoadWorldEvent event,
      @Getter(value = "getTargetWorld") World targetWorld) {
    SkyClaimsTimings.WORLD_LOAD.startTimingIfSync();
    String world = PLUGIN.getConfig().getWorldConfig().getWorldName();

    ClaimManager claimManager = PLUGIN.getGriefPrevention().getClaimManager(targetWorld);
    if (targetWorld.getName().equalsIgnoreCase(world)) {
      Claim wilderness = claimManager.getWildernessClaim();
      Subject subject = Sponge.getServiceManager().provideUnchecked(PermissionService.class)
          .getDefaults();
      String target = "any:any";

      PLUGIN.getConfig().getIntegrationConfig().getGriefPrevention().getWildernessFlags()
          .forEach((flag, value) -> {
            if (wilderness.getPermissionValue(subject, flag, target) != value) {
              wilderness.setPermission(subject, flag, target, value, PLUGIN.getCause())
                  .whenComplete((result, throwable) -> {
                    if (result.successful()) {
                      PLUGIN.getLogger().info(
                          "{}: Set {} flag in wilderness to {}.",
                          world, flag, value.toString()
                      );
                    } else {
                      PLUGIN.getLogger().info(result.getResultType().toString());
                    }
                  });
            }
          });
    }

    SkyClaimsTimings.WORLD_LOAD.stopTimingIfSync();
  }
}
