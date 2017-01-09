package net.mohron.skyclaims.config;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.GlobalConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
	private final SkyClaims PLUGIN = SkyClaims.getInstance();
	private final Logger LOGGER = PLUGIN.getLogger();
	public static final int CONFIG_VERSION = 1;

	private ObjectMapper<GlobalConfig>.BoundInstance configMapper;
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> loader) {
		this.loader = loader;
		try {
			this.configMapper = ObjectMapper.forObject(PLUGIN.getConfig());
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}

		this.load();
		this.initializeSchematic();
	}

	/**
	 * Saves the serialized config to file
	 */
	public void save() {
		try {
			SimpleCommentedConfigurationNode out = SimpleCommentedConfigurationNode.root();
			this.configMapper.serialize(out);
			this.loader.save(out);
		} catch (ObjectMappingException | IOException e) {
			LOGGER.error(String.format("Failed to save config.\r\n %s", e.getMessage()));
		}
	}

	/**
	 * Loads the configs into serialized objects, for the configMapper
	 */
	public void load() {
		try {
			this.configMapper.populate(this.loader.load());
		} catch (ObjectMappingException | IOException e) {
			LOGGER.error(String.format("Failed to load config.\r\n %s", e.getMessage()));
		}
	}

	/**
	 * Create the default schematic file, from resource, into the config-specified folder
	 */
	private void initializeSchematic() {
		Path schemDir = Paths.get(PLUGIN.getConfigDir() + File.separator + "schematics");
		if (!Files.exists(schemDir)) {
			try {
				Files.createDirectory(schemDir);
			} catch (IOException e) {
				LOGGER.error(String.format("Failed to create schematics directory.\r\n %s", e.getMessage()));
			}
		}
/*		Path defaultSchematic = Paths.get(String.format("%s%sschematics%sisland.schematic", PLUGIN.getConfigDir(), File.separator, File.separator));
		if (!Files.exists(defaultSchematic)) {
			try {
				Files.createFile(defaultSchematic);

				try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("/island.schematic")) {
					try (OutputStream outputStream = new FileOutputStream(defaultSchematic.toString())) {
						try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
							byte[] buffer = new byte[1024];
							int bytesRead;

							while ((bytesRead = inputStream.read()) != -1)
								gzipOutputStream.write(buffer, 0, bytesRead);
						}
					}
				}
			} catch (IOException e) {
				LOGGER.error(String.format("Failed to create default schematic.\r\n %s", e.getMessage()));
			}
		}*/
	}
}