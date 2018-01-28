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
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

public class RegenerateRegionTask implements Runnable {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  private Region region;
  private Island island;
  private String schematic;
  private boolean commands;

  public RegenerateRegionTask(Region region) {
    this.region = region;
    this.island = null;
    this.commands = false;
  }

  public RegenerateRegionTask(Island island, String schematic, boolean commands) {
    this.region = island.getRegion();
    this.island = island;
    this.schematic = schematic;
    this.commands = commands;
  }

  @Override
  public void run() {
    SkyClaimsTimings.CLEAR_ISLAND.startTimingIfSync();
    World world = PLUGIN.getConfig().getWorldConfig().getWorld();

    PLUGIN.getLogger().info("Begin clearing region ({}, {})", region.getX(), region.getZ());

    Stopwatch sw = Stopwatch.createStarted();

    for (int x = region.getLesserBoundary().getX(); x < region.getGreaterBoundary().getX(); x += 16) {
      for (int z = region.getLesserBoundary().getZ(); z < region.getGreaterBoundary().getZ(); z += 16) {
        world.getChunkAtBlock(x, 0, z).ifPresent(chunk -> {
          chunk.loadChunk(false);
          // Teleport any players to world spawn
          chunk.getEntities(e -> e instanceof Player)
              .forEach(e -> e.setLocationSafely(PLUGIN.getConfig().getWorldConfig().getSpawn()));
          // Clear the contents of an tile entity with an inventory
          chunk.getTileEntities(e -> e instanceof TileEntityCarrier)
              .forEach(e -> ((TileEntityCarrier) e).getInventory().clear());
          for (int bx = chunk.getBlockMin().getX(); bx <= chunk.getBlockMax().getX(); bx++) {
            for (int bz = chunk.getBlockMin().getZ(); bz <= chunk.getBlockMax().getZ(); bz++) {
              for (int by = chunk.getBlockMin().getY(); by <= chunk.getBlockMax().getY(); by++) {
                if (chunk.getBlockType(bx, by, bz) != BlockTypes.AIR) {
                  chunk.getLocation(bx, by, bz).setBlock(BlockTypes.AIR.getDefaultState(), PLUGIN.getCause());
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

    PLUGIN.getLogger()
        .info(String.format("Finished clearing region (%s, %s) in %dms.", region.getX(), region.getZ(), sw.elapsed(TimeUnit.MILLISECONDS)));

    if (island != null) {
      if (commands) {
        // Run reset commands
        for (String command : PLUGIN.getConfig().getMiscConfig().getResetCommands()) {
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
