package net.mohron.skyclaims.world;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.util.WorldUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ArchetypeVolume;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

public class GenerateIslandTask implements Runnable {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final World WORLD = ConfigUtil.getWorld();
	private static final File CONFIG_DIR = new File(PLUGIN.getConfigDir().toString());

	private UUID owner;
	private Island island;
	private String schematic;

	public GenerateIslandTask(UUID owner, Island island, String schematic) {
		this.owner = owner;
		this.island = island;
		this.schematic = schematic;
	}

	@Override
	public void run() {
		File inputFile = new File(CONFIG_DIR, String.format("schematics%s%s.schematic", File.separator, schematic));

		DataContainer schematicData;
		try {
			schematicData = DataFormats.NBT.readFrom(new GZIPInputStream(new FileInputStream(inputFile)));
		} catch (Exception e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Error loading schematic: " + e.getMessage());
			return;
		}

		ArchetypeVolume volume;
		try {
			volume = DataTranslators.SCHEMATIC.translate(schematicData);
		} catch (InvalidDataException e) {
			volume = DataTranslators.LEGACY_SCHEMATIC.translate(schematicData);
			PLUGIN.getLogger().warn("Loaded legacy schematic: " + e.getMessage());
			return;
		}

		Location<World> centerBlock = island.getRegion().getCenter();
		// Loads center chunks
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				WORLD.loadChunk(
						centerBlock.getChunkPosition().getX() + x,
						centerBlock.getChunkPosition().getY(),
						centerBlock.getChunkPosition().getZ() + z,
						true
				);
			}
		}

		Location<World> spawn = new Location<>(island.getWorld(), centerBlock.getX(), centerBlock.getY() + volume.getBlockSize().getY() - 1, centerBlock.getZ());
		island.setSpawn(spawn);
		volume.apply(spawn, BlockChangeFlag.NONE, Cause.source(PLUGIN.getPluginContainer()).build());

		// Set the region's BiomeType using the default biome option if set
		Options.getDefaultBiome(owner).ifPresent(biomeType -> {
			WorldUtil.setRegionBiome(island.getRegion(), biomeType);
		});

		Sponge.getServer().getPlayer(owner).ifPresent(p1 -> {
			PLUGIN.getGame().getScheduler().createTaskBuilder().execute(
					CommandUtil.createTeleportConsumer(p1, spawn)
			).submit(PLUGIN);
		});
	}
}