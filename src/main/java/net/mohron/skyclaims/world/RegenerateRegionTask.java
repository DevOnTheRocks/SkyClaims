package net.mohron.skyclaims.world;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class RegenerateRegionTask implements Runnable {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final World WORLD = ConfigUtil.getWorld();

	private Region region;

	private Island island;
	private String schematic;

	public RegenerateRegionTask(Region region) {
		this.region = region;
	}

	public RegenerateRegionTask(Island island, String schematic) {
		this.region = island.getRegion();
		this.island = island;
		this.schematic = schematic;
	}

	@Override
	public void run() {
		PLUGIN.getLogger().info(String.format("Begin clearing region (%s, %s)", region.getX(), region.getZ()));
		for (int x = region.getLesserBoundary().getX(); x < region.getGreaterBoundary().getX(); x += 16) {
			for (int z = region.getLesserBoundary().getZ(); z < region.getGreaterBoundary().getZ(); z += 16) {
				WORLD.getChunkAtBlock(x, 0, z).ifPresent(chunk -> {
					chunk.loadChunk(false);
					chunk.getEntities().clear();
					for (int bx = chunk.getBlockMin().getX(); bx <= chunk.getBlockMax().getX(); bx++) {
						for (int bz = chunk.getBlockMin().getZ(); bz <= chunk.getBlockMax().getZ(); bz++) {
							for (int by = chunk.getBlockMin().getY(); by <= chunk.getBlockMax().getY(); by++) {
								if (chunk.getBlockType(bx, by, bz) != BlockTypes.AIR) {
//									PLUGIN.getLogger().info(String.format("Found %s at (%s, %s, %s), replacing with air.", chunk.getBlock(bx, by, bz).getType().getName(), bx, by, bz));
									chunk.getLocation(bx, by, bz).setBlock(BlockTypes.AIR.getDefaultState(), Cause.source(PLUGIN).build());
								}
							}
						}
					}
					chunk.unloadChunk();
				});
			}
		}

		if (schematic != null) {
			// Run reset commands
			ConfigUtil.getResetCommands().ifPresent(commands -> {
				for (String command : commands) {
					PLUGIN.getGame().getCommandManager().process(PLUGIN.getGame().getServer().getConsole(), command.replace("@p", island.getOwnerName()));
				}
			});

			GenerateIslandTask generateIsland = new GenerateIslandTask(island.getOwnerUniqueId(), island, schematic);
			PLUGIN.getGame().getScheduler().createTaskBuilder().execute(generateIsland).submit(PLUGIN);
		}
	}
}
