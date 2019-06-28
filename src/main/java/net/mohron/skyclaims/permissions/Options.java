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

package net.mohron.skyclaims.permissions;

import java.util.Optional;
import java.util.UUID;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.argument.BiomeArgument;
import net.mohron.skyclaims.schematic.IslandSchematic;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.biome.BiomeType;

public final class Options {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();
  private static final PermissionService PERMISSION_SERVICE = SkyClaims.getInstance().getPermissionService();

  // SkyClaims Options
  public static final String DEFAULT_SCHEMATIC = "skyclaims.default-schematic";
  public static final String DEFAULT_BIOME = "skyclaims.default-biome";
  public static final String MIN_SIZE = "skyclaims.min-size";
  public static final String MAX_SIZE = "skyclaims.max-size";
  public static final String MAX_SPAWNS = "skyclaims.max-spawns";
  public static final String MAX_HOSTILE = "skyclaims.max-spawns.hostile";
  public static final String MAX_PASSIVE = "skyclaims.max-spawns.passive";
  public static final String EXPIRATION = "skyclaims.expiration";
  public static final String MAX_ISLANDS = "skyclaims.max-islands";
  public static final String MAX_TEAMMATES = "skyclaims.max-teammates";

  private Options() {
  }

  public static Optional<IslandSchematic> getDefaultSchematic(UUID playerUniqueId) {
    String name = getStringOption(playerUniqueId, DEFAULT_SCHEMATIC, "");

    if(PLUGIN.getSchematicManager().getSchematics().size() == 1) {
      return Optional.of(PLUGIN.getSchematicManager().getSchematics().get(0));
    } else if (name.equalsIgnoreCase("random")) {
      return Optional.of(PLUGIN.getSchematicManager().getRandomSchematic());
    } else {
      return PLUGIN.getSchematicManager().getSchematics().stream()
          .filter(s -> s.getName().equalsIgnoreCase(name))
          .findAny();
    }
  }

  public static Optional<BiomeType> getDefaultBiome(UUID playerUniqueId) {
    String biomeOption = getStringOption(playerUniqueId, DEFAULT_BIOME, "");
    if (StringUtils.isBlank(biomeOption)) {
      return Optional.empty();
    }
    for (BiomeType biome : BiomeArgument.BIOMES) {
      if (biome.getId().equalsIgnoreCase(biomeOption) || biome.getId().equalsIgnoreCase("minecraft:" + biomeOption)) {
        return Optional.of(biome);
      }
    }
    return Optional.empty();
  }

  public static int getMinSize(UUID playerUniqueId) {
    return getIntOption(playerUniqueId, MIN_SIZE, 48, 8, 256);
  }

  public static int getMaxSize(UUID playerUniqueId) {
    return getIntOption(playerUniqueId, MAX_SIZE, 64, 8, 256);
  }

  public static int getMaxSpawns(UUID playerUniqueId) {
    return getIntOption(playerUniqueId, MAX_SPAWNS, PLUGIN.getConfig().getEntityConfig().getMaxSpawns());
  }

  public static int getMaxHostileSpawns(UUID playerUniqueId) {
    return getIntOption(playerUniqueId, MAX_HOSTILE, PLUGIN.getConfig().getEntityConfig().getMaxHostile());
  }

  public static int getMaxPassiveSpawns(UUID playerUniqueId) {
    return getIntOption(playerUniqueId, MAX_PASSIVE, PLUGIN.getConfig().getEntityConfig().getMaxPassive());
  }

  public static int getExpiration(UUID playerUniqueId) {
    return getIntOption(playerUniqueId, EXPIRATION, PLUGIN.getConfig().getExpirationConfig().getThreshold());
  }

  public static int getMaxIslands(UUID playerUniqueId) {
    return getIntOption(playerUniqueId, MAX_ISLANDS, 0);
  }

  public static int getMaxTeammates(UUID playerUniqueId) {
    return getIntOption(playerUniqueId, MAX_TEAMMATES, 0);
  }

  private static String getStringOption(UUID uuid, String option, String defaultValue) {
    Optional<Subject> subject = PERMISSION_SERVICE.getUserSubjects().getSubject(uuid.toString());
    return !subject.isPresent()
        ? defaultValue
        : subject.get().getOption(option).orElse(defaultValue);
  }

  private static int getIntOption(UUID uuid, String option, int defaultValue, int min, int max) {
    int value = getIntOption(uuid, option, defaultValue);
    return (value < min || value > max) ? defaultValue : value;
  }

  private static int getIntOption(UUID uuid, String option, int defaultValue) {
    Optional<Subject> subject = PERMISSION_SERVICE.getUserSubjects().getSubject(uuid.toString());
    if (!subject.isPresent()) {
      return defaultValue;
    }
    String value = subject.get().getOption(option).orElse(String.valueOf(defaultValue));
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
