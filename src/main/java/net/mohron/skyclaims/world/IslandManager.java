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

package net.mohron.skyclaims.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.griefdefender.api.claim.Claim;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.schematic.IslandSchematic;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.world.region.Region;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.server.ServerLocation;

public class IslandManager {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  public static Map<UUID, Island> ISLANDS = Maps.newHashMap();
  public static Set<Island> saveQueue = Sets.newHashSet();

  public static Optional<Island> get(UUID islandUniqueId) {
    return Optional.ofNullable(ISLANDS.get(islandUniqueId));
  }

  public static Optional<Island> getByLocation(ServerLocation location) {
    return getByRegion(Region.get(location));
  }

  public static Optional<Island> getByTransform(Transform<World> transform) {
    return getByRegion(Region.get(transform.getLocation()));
  }

  public static Optional<Island> getByRegion(Region region) {
    return ISLANDS.values().stream()
        .filter(i -> i.getRegion().equals(region))
        .findAny();
  }

  public static Optional<Island> getByClaim(Claim claim) {
    for (Island island : ISLANDS.values()) {
      if (island.getClaim().isPresent() && island.getClaim().get().equals(claim)) {
        return Optional.of(island);
      }
    }
    return Optional.empty();
  }

  public static List<Island> getByUser(User user) {
    return ISLANDS.values().stream()
        .filter(i -> i.isMember(user))
        .collect(Collectors.toList());
  }

  public static List<Island> getUserIslandsByPrivilege(User user, PrivilegeType privilege) {
    return ISLANDS.values().stream()
        .filter(i -> i.getPrivilegeType(user).greaterThanOrEqualTo(privilege))
        .collect(Collectors.toList());
  }

  @Deprecated
  public static Optional<Island> getByOwner(UUID owner) {
    for (Island island : ISLANDS.values()) {
      if (island.getOwnerUniqueId().equals(owner)) {
        return Optional.of(island);
      }
    }
    return Optional.empty();
  }

  public static boolean hasIsland(UUID owner) {
    if (ISLANDS.isEmpty()) {
      return false;
    }
    for (Island island : ISLANDS.values()) {
      if (island.isMember(owner)) {
        return true;
      }
    }
    return false;
  }

  public static int countByOwner(User owner) {
    return (int) ISLANDS.values().stream()
        .filter(i -> i.getOwnerUniqueId().equals(owner.uniqueId()))
        .count();
  }

  public static int countByMember(User member) {
    return (int) ISLANDS.values().stream()
        .filter(i -> i.isMember(member))
        .count();
  }

  public Map<UUID, Island> getIslands() {
    return ISLANDS;
  }

  public static Consumer<Task> processCommands(String playerName, @Nullable IslandSchematic schematic) {
    return task -> {
      // Run island commands defined in config
      for (String command : PLUGIN.getConfig().getMiscConfig().getIslandCommands()) {
        command = command.replace("@p", playerName);
        Sponge.getCommandManager().process(Sponge.server().getConsole(), command);
        PLUGIN.getLogger().debug("Ran island command: {}", command);
      }
      // Run schematic commands
      if (schematic != null) {
        for (String command : schematic.getCommands()) {
          command = command.replace("@p", playerName);
          Sponge.getCommandManager().process(Sponge.server().getConsole(), command);
          PLUGIN.getLogger().debug("Ran schematic command: {}", command);
        }
      }
    };
  }
}
