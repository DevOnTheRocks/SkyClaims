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

package net.mohron.skyclaims.world;

import java.util.UUID;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.schematic.IslandSchematic;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.util.WorldUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ArchetypeVolume;

public class GenerateIslandTask implements Runnable {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  private UUID owner;
  private Island island;
  private IslandSchematic schematic;

  public GenerateIslandTask(UUID owner, Island island, IslandSchematic schematic) {
    this.owner = owner;
    this.island = island;
    this.schematic = schematic;
  }

  @Override
  public void run() {
    SkyClaimsTimings.GENERATE_ISLAND.startTimingIfSync();
    World world = PLUGIN.getConfig().getWorldConfig().getWorld();

    ArchetypeVolume volume = schematic.getSchematic();

    Location<World> centerBlock = island.getRegion().getCenter();
    // Loads center chunks
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        world.loadChunk(
            centerBlock.getChunkPosition().getX() + x,
            centerBlock.getChunkPosition().getY(),
            centerBlock.getChunkPosition().getZ() + z,
            true
        );
      }
    }

    int height = schematic.getHeight().orElse(PLUGIN.getConfig().getWorldConfig().getIslandHeight());
    Location<World> spawn = new Location<>(
        island.getWorld(),
        centerBlock.getX(),
        height + volume.getRelativeBlockView().getBlockMax().getY() - volume.getBlockMax().getY() - 1,
        centerBlock.getZ()
    );
    island.setSpawn(new Transform<>(spawn.getExtent(), spawn.getPosition()));

    volume.apply(spawn, BlockChangeFlags.NONE);

    // Set the region's BiomeType using the schematic default biome or player option if set
    if (schematic.getBiomeType().isPresent()) {
      WorldUtil.setRegionBiome(island, schematic.getBiomeType().get());
    } else if (Options.getDefaultBiome(owner).isPresent()) {
      WorldUtil.setRegionBiome(island, Options.getDefaultBiome(owner).get());
    }

    if (PLUGIN.getConfig().getMiscConfig().isTeleportOnCreate()) {
      Sponge.getServer().getPlayer(owner).ifPresent(p -> PLUGIN.getGame().getScheduler().createTaskBuilder()
          .delayTicks(20)
          .execute(CommandUtil.createTeleportConsumer(p, spawn))
          .submit(PLUGIN));
    }

    SkyClaimsTimings.GENERATE_ISLAND.stopTimingIfSync();
  }
}