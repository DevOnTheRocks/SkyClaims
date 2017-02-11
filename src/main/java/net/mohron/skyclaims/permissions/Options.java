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

import com.google.common.collect.Maps;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.argument.BiomeArgument;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Options {
	private static final PermissionService PERMISSION_SERVICE = SkyClaims.getInstance().getPermissionService();
	private static final Map<String, String> DEFAULT_OPTIONS = Maps.newHashMap();

	public static final String DEFAULT_SCHEMATIC = "skyclaims.default-schematic";
	public static final String DEFAULT_BIOME = "skyclaims.default-biome";
	public static final String INITIAL_SIZE = "skyclaims.initial-size";
	public static final String MAX_SIZE = "skyclaims.max-size";
	public static final String MAX_ISLANDS = "skyclaims.max-islands";

	static {
		DEFAULT_OPTIONS.put(DEFAULT_SCHEMATIC, "island");
		DEFAULT_OPTIONS.put(DEFAULT_BIOME, null);
		DEFAULT_OPTIONS.put(INITIAL_SIZE, "48");
		DEFAULT_OPTIONS.put(MAX_SIZE, "64");
		DEFAULT_OPTIONS.put(MAX_ISLANDS, "1");
	}

	public static String getStringOption(UUID playerUniqueId, String option) {
		Subject subject = PERMISSION_SERVICE.getUserSubjects().get(playerUniqueId.toString());
		return subject.getOption(option).orElse(DEFAULT_OPTIONS.get(option));
	}

	private static int getIntOption(UUID playerUniqueId, String option, int defaultValue) {
		Subject subject = PERMISSION_SERVICE.getUserSubjects().get(playerUniqueId.toString());
		String value = subject.getOption(option).orElse(DEFAULT_OPTIONS.get(option));
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static int getIntOption(UUID playerUniqueId, String option, int minValue, int maxValue) {
		int value = getIntOption(playerUniqueId, option, Integer.parseInt(DEFAULT_OPTIONS.get(option)));
		return (value >= minValue && value <= maxValue) ? value : Integer.parseInt(DEFAULT_OPTIONS.get(option));
	}

	public static Optional<BiomeType> getDefaultBiome(UUID playerUniqueId) {
		String biomeOption = getStringOption(playerUniqueId, DEFAULT_BIOME);
		if (biomeOption == null) return Optional.empty();
		for (BiomeType biome : BiomeArgument.BIOMES.values()) {
			if (biome.getName().equalsIgnoreCase(biomeOption)) return Optional.of(biome);
		}
		return Optional.empty();
	}
}
