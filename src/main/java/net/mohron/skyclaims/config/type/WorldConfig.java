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

package net.mohron.skyclaims.config.type;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import net.mohron.skyclaims.SkyClaims;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@ConfigSerializable
public class WorldConfig {

  private static final UUID NIL_UUID = new UUID(0, 0);

  @Setting(value = "SkyClaims-World-UUID", comment = "Sponge UUID of the world to manage islands in")
  private UUID worldUuid = NIL_UUID;
  @Setting(value = "SkyClaims-World", comment = "Name of the world to manage islands in. Ignored if the UUID is provided Default: world")
  private String worldName = "world";
  @Setting(value = "Spawn-World", comment = "Use to override the world used when sending players to spawn.")
  private String spawnWorld = "";
  @Setting(value = "Island-Height", comment = "Height to build islands at (1-255). Default: 72")
  private int islandHeight = 72;
  @Setting(value = "Spawn-Regions", comment = "The height & width of regions to reserve for spawn (min 1). Default: 1")
  private int spawnRegions = 1;
  @Setting(value = "Preset-Code", comment = "A flat world preset code to use when regenerating a region. Only the block ID list is used.\n"
      + "See https://minecraft.gamepedia.com/Superflat#Preset_code_format for more details.")
  private String presetCode = "";
  @Setting(value = "Regen-On-Create", comment = "If enabled, SkyClaims will regen the target region before an island is created.")
  private boolean regenOnCreate = false;

  public Optional<UUID> getWorldUuid() {
    return worldUuid.equals(NIL_UUID) ? Optional.empty() : Optional.of(worldUuid);
  }

  public String getWorldName() {
    return worldName;
  }

  private Optional<World> getWorldOptional() {
    return getWorldUuid().map(uuid -> Sponge.server().getWorld(uuid)).orElseGet(() -> Sponge.server().getWorld(worldName));
  }

  public Optional<World> loadWorld() {
    return getWorldUuid().map(uuid -> Sponge.server().loadWorld(uuid)).orElseGet(() -> Sponge.server().loadWorld(worldName));
  }

  public World getWorld() {
    final Optional<World> world = getWorldOptional();
    if (world.isPresent()) {
      return world.get();
    } else {
      String message = "World \"" + worldName + "\" cannot be found.";
      SkyClaims.getInstance().getLogger().error(message);
      throw new NoSuchElementException(message);
    }
  }

  public World getSpawnWorld() {
    if (!isSeparateSpawn()) {
      return getWorld();
    }
    final Optional<World> world = Sponge.server().getWorld(spawnWorld);
    if (world.isPresent()) {
      return world.get();
    } else {
      SkyClaims.getInstance().getLogger().error("World \"" + spawnWorld + "\" cannot be found.");
      return getWorld();
    }
  }

  public ServerLocation getSpawn() {
    World world = getSpawnWorld();
    SkyClaims.getInstance().getLogger().debug("Spawn World: {}", world.getName());
    if (!world.isLoaded()) {
      SkyClaims.getInstance().getLogger().error("World \"" + spawnWorld + "\" is not loaded.");
      return getWorld().getSpawnLocation();
    }
    return world.getSpawnLocation();
  }

  public boolean isSeparateSpawn() {
    return !StringUtils.isEmpty(spawnWorld) && Sponge.server().getWorld(spawnWorld).isPresent();
  }

  public int getIslandHeight() {
    return Math.max(1, Math.min(255, islandHeight));
  }

  public int getSpawnRegions() {
    return Math.max(1, spawnRegions);
  }

  public String getPresetCode() {
    return presetCode;
  }

  public boolean isRegenOnCreate() {
    return regenOnCreate;
  }
}