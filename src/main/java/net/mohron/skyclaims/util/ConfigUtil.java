package net.mohron.skyclaims.util;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.lib.Arguments;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.gen.WorldGeneratorModifiers;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Optional;

public class ConfigUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final Game GAME = PLUGIN.getGame();
	private static GlobalConfig config = PLUGIN.getConfig();

	public static int get(Integer config, int defaultValue) {
		return (config != null) ? config : defaultValue;
	}

	public static World getWorld() {
		Server server = GAME.getServer();
		Optional<World> world = (config.world != null) ? server.getWorld(config.world.worldName) : Optional.empty();
		Optional<World> defaultWorld = server.getWorld(server.getDefaultWorldName());
		return world.isPresent() ? world.get() : defaultWorld.get();
	}

	public static Optional<BiomeType> getDefaultBiome() {
		if (config.world == null || config.world.defaultBiome == null) return Optional.empty();
		for (BiomeType biome : Arguments.BIOMES.values()) {
			if (biome.getName().equalsIgnoreCase(config.world.defaultBiome)) return Optional.of(biome);
		}
		return Optional.empty();
	}

	/**
	 * Export a resource embedded into a Jar file to the local file path.
	 *
	 * @param resourceName
	 * @return The path to the exported resource
	 * @throws Exception
	 */
	public static String ExportResource(String resourceName) throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;
		String jarFolder;
		try {
			stream = ConfigUtil.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
			if (stream == null) {
				throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
			}

			int readBytes;
			byte[] buffer = new byte[4096];
			jarFolder = new File(ConfigUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
			resStreamOut = new FileOutputStream(jarFolder + resourceName);
			while ((readBytes = stream.read(buffer)) > 0) {
				resStreamOut.write(buffer, 0, readBytes);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			stream.close();
			resStreamOut.close();
		}

		return jarFolder + resourceName;
	}

	public static void setVoidGenerator() {
		Optional<WorldProperties> worldProperties = GAME.getServer().getDefaultWorld();
		if (!worldProperties.isPresent()) {
			try {
				GAME.getServer().createWorldProperties("", WorldArchetypes.THE_VOID);
			} catch (IOException e) {
				PLUGIN.getLogger().error("Failed to create worldName: " + e);
			}
		} else {
			Collection<WorldGeneratorModifier> generatorModifiers = null;
			generatorModifiers.add(WorldGeneratorModifiers.VOID);
			worldProperties.get().setGeneratorModifiers(generatorModifiers);
		}
	}
}