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

package net.mohron.skyclaims.integration.griefdefender;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.event.ClaimEvent;
import net.kyori.event.EventSubscription;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.integration.GriefDefenderConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.world.World;

public class GDIntegration {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  private GriefDefenderConfig config;
  private EventSubscription eventSubscription;

  @Listener(order = Order.LATE)
  public void onInitialization(GameInitializationEvent event) {
    config = PLUGIN.getConfig().getIntegrationConfig().getGriefDefender();
  }

  @Listener
  public void onAboutToStart(GameAboutToStartServerEvent event) {
    registerClaimEventHandler();
  }

  @Listener
  public void onReload(GameReloadEvent event) {
    config = PLUGIN.getConfig().getIntegrationConfig().getGriefDefender();
    eventSubscription.unsubscribe();
    registerClaimEventHandler();
  }

  @Listener(order = Order.LAST)
  public void onWorldLoad(LoadWorldEvent event, @Getter(value = "getTargetWorld") World targetWorld) {
//    String world = PLUGIN.getConfig().getWorldConfig().getWorldName();
//
//    if (targetWorld.getName().equalsIgnoreCase(world)) {
//      ClaimManager claimManager = GriefDefender.getCore().getClaimManager(targetWorld.getUniqueId());
//      Claim wilderness = claimManager.getWildernessClaim();
//      Set<Context> contexts = Sets.newHashSet();
//
//      final Map<Flag, Boolean> wildernessFlags = config.getWildernessFlags();
//      if (wildernessFlags != null) {
//        wildernessFlags.forEach(setWildernessFlag(world, wilderness, contexts));
//      }
//    }
  }

//  private BiConsumer<Flag, Boolean> setWildernessFlag(String world, Claim wilderness, Set<Context> contexts) {
//    return (flag, value) -> {
//      Tristate tristate = value ? Tristate.TRUE : Tristate.FALSE;
//      if (wilderness.getFlagPermissionValue(flag, contexts) != tristate) {
//        wilderness.setFlagPermission(flag, tristate, contexts).whenComplete((result, throwable) -> {
//          if (result.successful()) {
//            PLUGIN.getLogger().info("{}: Set {} flag in wilderness to {}.", world, flag, tristate.toString());
//          } else {
//            PLUGIN.getLogger().warn(result.getResultType().toString(), throwable);
//          }
//        });
//      }
//    };
//  }

  private void registerClaimEventHandler() {
    eventSubscription = GriefDefender.getEventManager().getBus().subscribe(ClaimEvent.class, new ClaimEventHandler());
  }
}
