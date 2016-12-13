package net.mohron.skyclaims.config;

import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.SkyClaims;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SkyClaimsConfig {
	private SkyClaims plugin = SkyClaims.getInstance();
	private Logger logger = plugin.getLogger();
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private CommentedConfigurationNode config;
	private Path configDir = plugin.getConfigDir();
	private Path configFile;

	public SkyClaimsConfig() {
		logger.info(String.format("Initializing %s configuration.", PluginInfo.NAME));

		configFile = Paths.get(String.format("%s\\%s.conf", configDir, PluginInfo.NAME));
		config = load();

//		try {
//			config.getValue(TypeToken.of(GlobalConfig.class), GlobalConfig());
//		} catch (ObjectMappingException e) {
//			logger.error(String.format("Failed to generate default configuration.\r\n %s", e.getMessage()));
//		}

		save();
	}

	private CommentedConfigurationNode load() {
		configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();

		if (!Files.exists(configDir)) {
			try {
				Files.createDirectory(configDir);
			} catch (IOException e) {
				logger.error(String.format("Failed to create configuration folder.\r\n %s", e.getMessage()));
			}
		}
		if (!Files.exists(configFile)) {
			try {
				Files.createFile(configFile);
			} catch (IOException e) {
				logger.error(String.format("Failed to generate configuration file.\r\n %s", e.getMessage()));
			}
		}
		try {
			return configLoader.load();
		} catch (IOException e) {
			return configLoader.createEmptyNode(ConfigurationOptions.defaults());
		}
	}

	private void save() {
		try {
			configLoader.save(config);
			logger.info("Configuration saved.");
		} catch (IOException e) {
			logger.error(String.format("Failed to save configuration file.\r\n %s", e.getMessage()));
		}
	}
}
