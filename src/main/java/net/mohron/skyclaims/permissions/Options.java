package net.mohron.skyclaims.permissions;

import com.google.common.collect.Maps;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import java.util.Map;
import java.util.UUID;

public class Options {
	private static final PermissionService PERMISSION_SERVICE = SkyClaims.getInstance().getPermissionService();
	public static final Map<String, String> DEFAULT_OPTIONS = Maps.newHashMap();

	public static final String DEFAULT_SCHEMATIC = "skyclaims.default-schematic";
	public static final String DEFAULT_BIOME = "skyclaims.default-biome";
	public static final String MIN_RADIUS = "skyclaims.min-radius";
	public static final String MAX_RADIUS = "skyclaims.max-radius";
	public static final String MAX_ISLANDS = "skyclaims.max-islands";

	static {
		DEFAULT_OPTIONS.put(DEFAULT_SCHEMATIC, "island");
		DEFAULT_OPTIONS.put(MIN_RADIUS, "32");
		DEFAULT_OPTIONS.put(MAX_RADIUS, "48");
		DEFAULT_OPTIONS.put(DEFAULT_BIOME, null);
		DEFAULT_OPTIONS.put(MAX_ISLANDS, "1");
	}

	public static String getStringOption(UUID playerUniqueId, String option) {
		Subject subject = PERMISSION_SERVICE.getUserSubjects().get(playerUniqueId.toString());
		String value = subject.getOption(option).orElse(DEFAULT_OPTIONS.get(option));
		return value;
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
}
