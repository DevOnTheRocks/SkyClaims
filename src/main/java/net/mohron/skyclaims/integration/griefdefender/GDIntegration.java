/*
 * Required Notice: Copyright (C) 2019 Mohron (https://www.mohron.dev)
 *
 * IslandDefender is licensed under the PolyForm Noncommercial License 1.0.0 (https://polyformproject.org/licenses/noncommercial/1.0.0).
 */

package net.mohron.skyclaims.integration.griefdefender;

import com.google.common.collect.Sets;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;
import com.griefdefender.api.permission.Context;
import com.griefdefender.api.permission.flag.Flag;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
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

  private ClaimEventHandler claimEventHandler;
  private GriefDefenderConfig config;

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
    GriefDefender.getEventManager().unregister(claimEventHandler);
    registerClaimEventHandler();
  }

  @Listener(order = Order.LAST)
  public void onWorldLoad(LoadWorldEvent event, @Getter(value = "getTargetWorld") World targetWorld) {
    String world = PLUGIN.getConfig().getWorldConfig().getWorldName();

    if (targetWorld.getName().equalsIgnoreCase(world)) {
      ClaimManager claimManager = GriefDefender.getCore().getClaimManager(targetWorld.getUniqueId());
      Claim wilderness = claimManager.getWildernessClaim();
      Set<Context> contexts = Sets.newHashSet();

      final Map<Flag, Boolean> wildernessFlags = config.getWildernessFlags();
      if (wildernessFlags != null) {
        wildernessFlags.forEach(setWildernessFlag(world, wilderness, contexts));
      }
    }
  }

  private BiConsumer<Flag, Boolean> setWildernessFlag(String world, Claim wilderness, Set<Context> contexts) {
    return (flag, value) -> {
      Tristate tristate = value ? Tristate.TRUE : Tristate.FALSE;
      if (wilderness.getFlagPermissionValue(flag, contexts) != tristate) {
        wilderness.setFlagPermission(flag, tristate, contexts).whenComplete((result, throwable) -> {
          if (result.successful()) {
            PLUGIN.getLogger().info("{}: Set {} flag in wilderness to {}.", world, flag, tristate.toString());
          } else {
            PLUGIN.getLogger().warn(result.getResultType().toString(), throwable);
          }
        });
      }
    };
  }

  private void registerClaimEventHandler() {
    claimEventHandler = new ClaimEventHandler();
    GriefDefender.getEventManager().register(claimEventHandler);
  }
}
