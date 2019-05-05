package net.mohron.skyclaims.world;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import org.spongepowered.api.Sponge;
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
  private final int x;
  private final int z;
  private final BlockState[] blocks;
  private final Location<World> spawn;

  public RegenerateChunkTask(World world, int x, int z, BlockState[] blocks, Location<World> spawn) {
    this.world = world;
    this.x = x;
    this.z = z;
    this.blocks = blocks;
    this.spawn = spawn;
  }


  @Override
  public void run() {
    SkyClaimsTimings.CLEAR_ISLAND.startTimingIfSync();

    final Chunk chunk = world.loadChunk(Sponge.getServer().getChunkLayout().forceToChunk(x, 0, z), false)
        .orElse(null);

    if (chunk == null) {
      PLUGIN.getLogger().error("Failed to load chunk at block {}, 0, {}", x, z);
      return;
    }

    PLUGIN.getLogger().debug("Began regenerating chunk ({}, {})", x, z);
    // Teleport any players to world spawn
    chunk.getEntities(e -> e instanceof Player).forEach(e -> e.setLocationSafely(spawn));
    // Clear the contents of an tile entity with an inventory
    chunk.getTileEntities(e -> e instanceof TileEntityCarrier).forEach(e -> ((TileEntityCarrier) e).getInventory().clear());
    for (int bx = chunk.getBlockMin().getX(); bx <= chunk.getBlockMax().getX(); bx++) {
      for (int bz = chunk.getBlockMin().getZ(); bz <= chunk.getBlockMax().getZ(); bz++) {
        for (int by = chunk.getBlockMin().getY(); by <= chunk.getBlockMax().getY(); by++) {
          if (!chunk.getBlock(bx, by, bz).equals(blocks[by])) {
            chunk.getLocation(bx, by, bz).setBlock(blocks[by], BlockChangeFlags.NONE);
          }
        }
      }
    }
    // Remove any remaining entities.
    chunk.getEntities(e -> !(e instanceof Player)).forEach(Entity::remove);
    chunk.unloadChunk();
    PLUGIN.getLogger().debug("Finished regenerating chunk ({}, {})", x, z);

    SkyClaimsTimings.CLEAR_ISLAND.stopTimingIfSync();
  }
}
