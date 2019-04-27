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

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import net.mohron.skyclaims.config.type.WorldConfig;
import net.mohron.skyclaims.schematic.IslandSchematic;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

public class RegenerateRegionTask implements Runnable {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  private Region region;
  private Island island;
  private IslandSchematic schematic;
  private boolean commands;

  public RegenerateRegionTask(Region region) {
    this.region = region;
    this.island = null;
    this.commands = false;
  }

  public RegenerateRegionTask(Island island, IslandSchematic schematic, boolean commands) {
    this.region = island.getRegion();
    this.island = island;
    this.schematic = schematic;
    this.commands = commands;
  }

  @Override
  public void run() {
    SkyClaimsTimings.CLEAR_ISLAND.startTimingIfSync();
    WorldConfig config = PLUGIN.getConfig().getWorldConfig();
    World world = config.getWorld();

    PLUGIN.getLogger().info("Begin regenerating region ({}, {})", region.getX(), region.getZ());

    Stopwatch sw = Stopwatch.createStarted();

    PLUGIN.getLogger().info("Using preset code '{}' to regenerate region.", config.getPresetCode());
    final BlockState[] blocks = FlatWorldUtil.getBlocksSafely(config.getPresetCode());

    for (int x = region.getLesserBoundary().getX(); x < region.getGreaterBoundary().getX(); x += 16) {
      for (int z = region.getLesserBoundary().getZ(); z < region.getGreaterBoundary().getZ(); z += 16) {
        world.getChunkAtBlock(x, 0, z).ifPresent(chunk -> {
          chunk.loadChunk(false);
          // Teleport any players to world spawn
          chunk.getEntities(e -> e instanceof Player).forEach(e -> e.setLocationSafely(config.getSpawn()));
          // Clear the contents of an tile entity with an inventory
          chunk.getTileEntities(e -> e instanceof TileEntityCarrier).forEach(e -> ((TileEntityCarrier) e).getInventory().clear());
          for (int bx = chunk.getBlockMin().getX(); bx <= chunk.getBlockMax().getX(); bx++) {
            for (int bz = chunk.getBlockMin().getZ(); bz <= chunk.getBlockMax().getZ(); bz++) {
              for (int by = chunk.getBlockMin().getY(); by <= chunk.getBlockMax().getY(); by++) {
                BlockState block = blocks[by];
                if (chunk.getBlockType(bx, by, bz) != block.getType()) {
                  chunk.getLocation(bx, by, bz).setBlock(block);
                }
              }
            }
          }
          // Remove any remaining entities.
          chunk.getEntities(e -> !(e instanceof Player)).forEach(Entity::remove);
          chunk.unloadChunk();
        });
      }
    }

    sw.stop();

    PLUGIN.getLogger().info("Finished regenerating region ({}, {}) in {}ms.", region.getX(), region.getZ(), sw.elapsed(TimeUnit.MILLISECONDS));

    if (island != null) {
      if (commands) {
        // Run reset commands
        for (String command : PLUGIN.getConfig().getMiscConfig().getResetCommands()) {
          PLUGIN.getGame().getCommandManager()
              .process(PLUGIN.getGame().getServer().getConsole(), command.replace("@p", island.getOwnerName()));
        }
        for (String command : schematic.getCommands()) {
          PLUGIN.getGame().getCommandManager()
              .process(PLUGIN.getGame().getServer().getConsole(), command.replace("@p", island.getOwnerName()));
        }
      }

      PLUGIN.getGame().getScheduler().createTaskBuilder()
          .delayTicks(1)
          .execute(new GenerateIslandTask(island.getOwnerUniqueId(), island, schematic))
          .submit(PLUGIN);
    }

    SkyClaimsTimings.CLEAR_ISLAND.stopTimingIfSync();
  }
}
