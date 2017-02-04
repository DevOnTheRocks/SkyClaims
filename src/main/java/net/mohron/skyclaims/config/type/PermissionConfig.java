package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class PermissionConfig {
	@Setting(value = "Separate-BiomeType-Permissions", comment = "Enable permission checking for the Biome Type Argument.")
	private boolean separateBiomePerms = false;
	@Setting(value = "Separate-Schematic-Permissions", comment = "Enable permission checking for the Schematic Argument.")
	private boolean separateSchematicPerms = false;
	@Setting(value = "Separate-Target-Permissions", comment = "Enable permission checking for the Target Argument.")
	private boolean separateTargetPerms = false;

	public boolean isSeparateBiomePerms() {
		return separateBiomePerms;
	}

	public boolean isSeparateSchematicPerms() {
		return separateSchematicPerms;
	}

	public boolean isSeparateTargetPerms() {
		return separateTargetPerms;
	}
}