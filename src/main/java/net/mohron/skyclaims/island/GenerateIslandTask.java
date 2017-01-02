package net.mohron.skyclaims.island;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.util.ConfigUtil;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Chunk;
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

	private Player player;
	private Island island;
	private String schematic;

	public GenerateIslandTask(Player player, Island island, String schematic) {
		this.player = player;
		this.island = island;
		this.schematic = schematic;
	}

	@Override
	public void run() {
		File inputFile = new File(CONFIG_DIR, String.format("schematics%s%s.schematic", File.separator, schematic));

		if (!inputFile.exists()) {
			player.sendMessage(Text.of(TextColors.RED, "Schematic at " + inputFile.getAbsolutePath() + " not found."));
			return;
		}

		DataContainer schematicData;
		try {
			schematicData = DataFormats.NBT.readFrom(new GZIPInputStream(new FileInputStream(inputFile)));
		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(Text.of(TextColors.DARK_RED, "Error loading schematic: " + e.getMessage()));
			return;
		}

		ArchetypeVolume volume = DataTranslators.SCHEMATIC.translate(schematicData);

		Optional<Chunk> chunkOptional = WORLD.loadChunk(island.getCenter().getChunkPosition(), true);
		chunkOptional.ifPresent(chunk -> {
			volume.apply(island.getCenter(), BlockChangeFlag.ALL, Cause.of(NamedCause.of("plugin", PLUGIN.getPluginContainer()), NamedCause.source(player)));
			chunk.unloadChunk();
		});

		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(CommandUtil.createTeleportConsumer(player, island.getSpawn(), island.getClaim())).submit(PLUGIN);
	}
}