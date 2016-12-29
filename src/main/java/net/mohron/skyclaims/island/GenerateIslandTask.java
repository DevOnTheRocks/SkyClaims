package net.mohron.skyclaims.island;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;
import com.sk89q.worldedit.world.registry.LegacyWorldData;
import com.sk89q.worldedit.world.registry.WorldData;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.world.Chunk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

public class GenerateIslandTask implements Runnable {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final SpongeWorldEdit SPONGE_WORLD_EDIT = PLUGIN.getSpongeWorldEdit();

	private Island island;
	private File schematic;

	public GenerateIslandTask(Island island, File schematic) {
		this.island = island;
		this.schematic = schematic;
	}

	@Override
	public void run() {
		copySchematic();

	}

	private void copySchematic() {
		try {
			FileInputStream fileInputStream = new FileInputStream(schematic);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			ClipboardReader clipboardReader = ClipboardFormat.SCHEMATIC.getReader(bufferedInputStream);

			WorldData worldData = LegacyWorldData.getInstance();
			Clipboard clipboard = clipboardReader.read(worldData);
			fileInputStream.close();

			ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard, worldData);
			EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(SPONGE_WORLD_EDIT.getWorld(island.getWorld()), 1000000);

			Optional<Chunk> chunk = island.getWorld().loadChunk(island.getCenter().getChunkPosition(), true);
			chunk.ifPresent(chunk1 -> {
				PasteBuilder pasteBuilder = clipboardHolder.createPaste(SPONGE_WORLD_EDIT.getWorld(island.getWorld()), worldData);
				try {
					Operations.complete(pasteBuilder.build());
				} catch (WorldEditException e) {
					PLUGIN.getLogger().error(String.format("Unable to create island for %s. Reason: %s", island.getOwnerName(), e));
				}
				chunk1.unloadChunk();
			});

		} catch (IOException e) {
			PLUGIN.getLogger().error(String.format("Unable to create island for %s. Reason: %s", island.getOwnerName(), e));
		}

	}
}