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

package net.mohron.skyclaims.config.type;

import com.google.common.collect.Lists;
import java.util.List;
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

  @Setting(value = "SkyClaims-World-UUID", comment = "Sponge UUID of the world to manage islands in")
  private UUID worldUuid = null;
  @Setting(value = "SkyClaims-World", comment = "Name of the world to manage islands in. Ignored if the UUID is provided Default: world")
  private String worldName = "world";
  @Setting(value = "Spawn-World", comment = "Use to override the world used when sending players to spawn.")
  private String spawnWorld = "";
  @Setting(value = "Void-Dimensions", comment = "A list of world names to generate as void. Default: world, DIM-1, DIM1")
  private List<String> voidDimensions = Lists.newArrayList("world", "DIM-1", "DIM1");
  @Setting(value = "Island-Height", comment = "Height to build islands at (1-255). Default: 72")
  private int islandHeight = 72;
  @Setting(value = "Spawn-Regions", comment = "The height & width of regions to reserve for spawn (min 1). Default: 1")
  private int spawnRegions = 1;
  @Setting(value = "Preset-Code", comment = "A flat world preset code to use when regenerating a region. Only the block ID list is used.\n"
      + "See https://minecraft.gamepedia.com/Superflat#Preset_code_format for more details.")
  private String presetCode = "";
  @Setting(value = "Regen-On-Create", comment = "If enabled, SkyClaims will regen the target region before an island is created.")
  private boolean regenOnCreate = false;

  public UUID getWorldUuid() {
    return worldUuid;
  }

  public String getWorldName() {
    return worldName;
  }

  private Optional<World> getWorldOptional() {
    return worldUuid != null ? Sponge.getServer().getWorld(worldUuid) : Sponge.getServer().getWorld(worldName);
  }

  public Optional<World> loadWorld() {
    return worldUuid != null ? Sponge.getServer().loadWorld(worldUuid) : Sponge.getServer().loadWorld(worldName);
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

  public Location<World> getSpawn() {
    World world = getWorld();
    SkyClaims.getInstance().getLogger().debug("Spawn World: {}", world.getName());
    return world.isLoaded() ? world.getSpawnLocation() : getWorld().getSpawnLocation();
  }

  public List<String> getVoidDimensions() {
    return voidDimensions;
  }

  public boolean isSeparateSpawn() {
    return !StringUtils.isEmpty(spawnWorld) && Sponge.getServer().getWorld(spawnWorld).isPresent();
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