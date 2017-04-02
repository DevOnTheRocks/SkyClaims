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

package net.mohron.skyclaims.config;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.GlobalConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.commons.io.FileUtils;
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
		this.initializeData();
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
	 * Create the data directory if it does not exist
	 */
	private void initializeData() {
		Path path = Paths.get(PLUGIN.getConfigDir() + File.separator + "data");
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				LOGGER.error(String.format("Failed to create data directory.\r\n %s", e.getMessage()));
			}
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
		Path defaultSchem = Paths.get(String.format("%s%sschematics%sisland.schematic", PLUGIN.getConfigDir(), File.separator, File.separator));
		if (!Files.exists(defaultSchem)) {
			try {
				//noinspection ConstantConditions - Resource will always be included
				FileUtils.copyURLToFile(this.getClass().getClassLoader().getResource("island.schematic"), defaultSchem.toFile());
			} catch (IOException e) {
				LOGGER.error(String.format("Failed to create default schematic.\r\n %s", e.getMessage()));
			}
		}
	}
}
