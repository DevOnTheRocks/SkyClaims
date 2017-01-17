package net.mohron.skyclaims.permissions;

import com.google.common.collect.Maps;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.Arguments;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Options {
	private static final PermissionService PERMISSION_SERVICE = SkyClaims.getInstance().getPermissionService();
	public static final Map<String, String> DEFAULT_OPTIONS = Maps.newHashMap();

	public static final String DEFAULT_SCHEMATIC = "skyclaims.default-schematic";
	public static final String DEFAULT_BIOME = "skyclaims.default-biome";
	public static final String INITIAL_SIZE = "skyclaims.initial-size";
	public static final String GROWTH_PER_HOUR = "skyclaims.growth-per-hour";
	public static final String MAX_SIZE = "skyclaims.max-size";
	public static final String MAX_ISLANDS = "skyclaims.max-islands";

	static {
		DEFAULT_OPTIONS.put(DEFAULT_SCHEMATIC, "island");
		DEFAULT_OPTIONS.put(DEFAULT_BIOME, null);
		DEFAULT_OPTIONS.put(INITIAL_SIZE, "32");
		DEFAULT_OPTIONS.put(GROWTH_PER_HOUR, "6");
		DEFAULT_OPTIONS.put(MAX_SIZE, "48");
		DEFAULT_OPTIONS.put(MAX_ISLANDS, "1");
	}

	public static String getStringOption(UUID playerUniqueId, String option) {
		Subject subject = PERMISSION_SERVICE.getUserSubjects().get(playerUniqueId.toString());
		return subject.getOption(option).orElse(DEFAULT_OPTIONS.get(option));
	}

	public static int getIntOption(UUID playerUniqueId, String option, int defaultValue) {
		Subject subject = PERMISSION_SERVICE.getUserSubjects().get(playerUniqueId.toString());
		String value = subject.getOption(option).orElse(DEFAULT_OPTIONS.get(option));
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static int getIntOption(UUID playerUniqueId, String option, int defaultValue, int minValue, int maxValue) {
		int value = getIntOption(playerUniqueId, option, defaultValue);
		return (value >= minValue && value <= maxValue) ? value : defaultValue;
	}

	public static Optional<BiomeType> getDefaultBiome(UUID playerUniqueId) {
		String biomeOption = getStringOption(playerUniqueId, DEFAULT_BIOME);
		if (biomeOption == null) return Optional.empty();
		for (BiomeType biome : Arguments.BIOMES.values()) {
			if (biome.getName().equalsIgnoreCase(biomeOption)) return Optional.of(biome);
		}
		return Optional.empty();
	}
}
