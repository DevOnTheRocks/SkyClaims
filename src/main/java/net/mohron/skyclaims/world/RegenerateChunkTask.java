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

import com.flowpowered.math.vector.Vector3i;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class RegenerateChunkTask implements Runnable {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  private final World world;
  private final Vector3i position;
  private final BlockState[] blocks;
  private final Location<World> spawn;

  public RegenerateChunkTask(World world, Vector3i position, BlockState[] blocks, Location<World> spawn) {
    this.world = world;
    this.position = position;
    this.blocks = blocks;
    this.spawn = spawn;
  }


  @Override
  public void run() {
    SkyClaimsTimings.CLEAR_ISLAND.startTimingIfSync();

    final Chunk chunk = world.loadChunk(position, true).orElse(null);

    if (chunk == null) {
      PLUGIN.getLogger().error("Failed to load chunk {}", position.toString());
      return;
    }

    PLUGIN.getLogger().debug("Began regenerating chunk {}", position.toString());
    // Teleport any players to world spawn
    chunk.getEntities(e -> e instanceof Player).forEach(e -> e.setLocationSafely(spawn));
    // Clear the contents of an tile entity with an inventory
    chunk.getTileEntities(e -> e instanceof TileEntityCarrier).forEach(e -> ((TileEntityCarrier) e).getInventory().clear());
    // Set the blocks
    for (int by = chunk.getBlockMin().getY(); by <= chunk.getBlockMax().getY(); by++) {
      for (int bx = chunk.getBlockMin().getX(); bx <= chunk.getBlockMax().getX(); bx++) {
        for (int bz = chunk.getBlockMin().getZ(); bz <= chunk.getBlockMax().getZ(); bz++) {
          if (!chunk.getBlock(bx, by, bz).equals(blocks[by])) {
            chunk.getLocation(bx, by, bz).setBlock(blocks[by], BlockChangeFlags.NONE);
          }
        }
      }
    }
    // Remove any remaining entities.
    chunk.getEntities(e -> !(e instanceof Player)).forEach(Entity::remove);
    chunk.unloadChunk();
    PLUGIN.getLogger().debug("Finished regenerating chunk {}", position.toString());

    SkyClaimsTimings.CLEAR_ISLAND.stopTimingIfSync();
  }
}
