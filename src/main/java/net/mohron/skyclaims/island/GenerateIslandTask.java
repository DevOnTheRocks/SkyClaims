package net.mohron.skyclaims.island;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.util.ConfigUtil;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ArchetypeVolume;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class GenerateIslandTask implements Runnable {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final World WORLD = ConfigUtil.getWorld();
	private static final File CONFIG_DIR = new File(PLUGIN.getConfigDir().toString());

	private User player;
	private Island island;
	private String schematic;

	public GenerateIslandTask(User player, Island island, String schematic) {
		this.player = player;
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

		ArchetypeVolume volume = DataTranslators.SCHEMATIC.translate(schematicData);

		// Loads 9 chunks
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				WORLD.loadChunk(
						island.getCenter().getChunkPosition().getX() + x,
						island.getCenter().getChunkPosition().getY(),
						island.getCenter().getChunkPosition().getZ() + z,
						true
				);
			}
		}
		// Get the chunk to apply the volume to
		Optional<Chunk> chunkOptional = WORLD.loadChunk(island.getCenter().getChunkPosition(), true);
		Location<World> center = new Location<>(island.getWorld(), island.getCenter().getX(), island.getCenter().getY() + volume.getBlockSize().getY() - 1, island.getCenter().getZ());
		island.setSpawn(center);
		chunkOptional.ifPresent(chunk -> {
			volume.apply(center, BlockChangeFlag.NONE, Cause.of(NamedCause.of("plugin", PLUGIN.getPluginContainer()), NamedCause.source(player)));
			chunk.unloadChunk();
		});

		player.getPlayer().ifPresent(p1 -> {
			PLUGIN.getGame().getScheduler().createTaskBuilder().execute(
					CommandUtil.createTeleportConsumer(p1, island.getSpawn())
			).submit(PLUGIN);
		});
	}
}