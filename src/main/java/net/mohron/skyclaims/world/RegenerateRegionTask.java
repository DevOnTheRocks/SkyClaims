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

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.World;

public class RegenerateRegionTask implements Runnable {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	private Region region;
	private Island island;
	private String schematic;

	public RegenerateRegionTask(Region region) {
		this.region = region;
		this.island = null;
	}

	public RegenerateRegionTask(Island island, String schematic) {
		this.region = island.getRegion();
		this.island = island;
		this.schematic = schematic;
	}

	@Override
	public void run() {
		World world = PLUGIN.getConfig().getWorldConfig().getWorld();

		PLUGIN.getLogger().info(String.format("Begin clearing region (%s, %s)", region.getX(), region.getZ()));
		for (int x = region.getLesserBoundary().getX(); x < region.getGreaterBoundary().getX(); x += 16) {
			for (int z = region.getLesserBoundary().getZ(); z < region.getGreaterBoundary().getZ(); z += 16) {
				world.getChunkAtBlock(x, 0, z).ifPresent(chunk -> {
					chunk.loadChunk(false);
					chunk.getEntities().clear();
					for (int bx = chunk.getBlockMin().getX(); bx <= chunk.getBlockMax().getX(); bx++) {
						for (int bz = chunk.getBlockMin().getZ(); bz <= chunk.getBlockMax().getZ(); bz++) {
							for (int by = chunk.getBlockMin().getY(); by <= chunk.getBlockMax().getY(); by++) {
								if (chunk.getBlockType(bx, by, bz) != BlockTypes.AIR) {
//									PLUGIN.getLogger().info(String.format("Found %s at (%s, %s, %s), replacing with air.", chunk.getBlock(bx, by, bz).getType().getName(), bx, by, bz));
									chunk.getLocation(bx, by, bz)
										.setBlock(BlockTypes.AIR.getDefaultState(), PLUGIN.getCause());
								}
							}
						}
					}
					chunk.unloadChunk();
				});
			}
		}
		PLUGIN.getLogger().info(String.format("Finished clearing region (%s, %s)", region.getX(), region.getZ()));

		if (island != null) {
			// Run reset commands
			for (String command : PLUGIN.getConfig().getMiscConfig().getResetCommands()) {
				PLUGIN.getGame().getCommandManager()
					.process(PLUGIN.getGame().getServer().getConsole(), command.replace("@p", island.getOwnerName()));
			}

			GenerateIslandTask generateIsland = new GenerateIslandTask(island.getOwnerUniqueId(), island, schematic);
			PLUGIN.getGame().getScheduler().createTaskBuilder().execute(generateIsland).submit(PLUGIN);
		}
	}
}
