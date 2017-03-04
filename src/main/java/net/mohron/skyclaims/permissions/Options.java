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

package net.mohron.skyclaims.permissions;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.argument.BiomeArgument;
import net.mohron.skyclaims.config.type.OptionsConfig;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.Optional;
import java.util.UUID;

public class Options {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final PermissionService PERMISSION_SERVICE = SkyClaims.getInstance().getPermissionService();

	// SkyClaims Options
	private static final String DEFAULT_SCHEMATIC = "skyclaims.default-schematic";
	private static final String DEFAULT_BIOME = "skyclaims.default-biome";
	private static final String MIN_SIZE = "skyclaims.min-size";
	private static final String MAX_SIZE = "skyclaims.max-size";
	private static final String MAX_ISLANDS = "skyclaims.max-islands";
	private static final String ISLAND_EXPIRATION = "skyclaims.expiration";

	public static String getDefaultSchematic(UUID playerUniqueId) {
		return getStringOption(playerUniqueId, DEFAULT_SCHEMATIC, PLUGIN.getConfig().getOptionsConfig().getSchematic());
	}

	public static Optional<BiomeType> getDefaultBiome(UUID playerUniqueId) {
		String biomeOption = getStringOption(playerUniqueId, DEFAULT_BIOME, PLUGIN.getConfig().getOptionsConfig().getBiome());
		if (StringUtils.isBlank(biomeOption)) return Optional.empty();
		for (BiomeType biome : BiomeArgument.BIOMES.values()) {
			if (biome.getName().equalsIgnoreCase(biomeOption)) return Optional.of(biome);
		}
		return Optional.empty();
	}

	public static int getMinSize(UUID playerUniqueId) {
		return getIntOption(playerUniqueId, MIN_SIZE, PLUGIN.getConfig().getOptionsConfig().getMinSize(), 8, 256);
	}

	public static int getMaxSize(UUID playerUniqueId) {
		return getIntOption(playerUniqueId, MAX_SIZE, PLUGIN.getConfig().getOptionsConfig().getMaxSize(), 8, 256);
	}

	private static String getStringOption(UUID playerUniqueId, String option, String defaultValue) {
		return PERMISSION_SERVICE.getUserSubjects().get(playerUniqueId.toString()).getOption(option).orElse(defaultValue);
	}

	private static int getIntOption(UUID playerUniqueId, String option, int defaultValue, int min, int max) {
		int value = getIntOption(playerUniqueId, option, defaultValue);
		return (value < min || value > max) ? defaultValue : value;
	}

	private static int getIntOption(UUID playerUniqueId, String option, int defaultValue) {
		String value = PERMISSION_SERVICE.getUserSubjects().get(playerUniqueId.toString()).getOption(option).orElse(String.valueOf(defaultValue));
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
