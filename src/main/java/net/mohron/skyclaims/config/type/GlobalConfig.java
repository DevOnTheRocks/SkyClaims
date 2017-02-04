package net.mohron.skyclaims.config.type;

import net.mohron.skyclaims.config.ConfigManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class GlobalConfig {
	@Setting(value = "Config-Version")
	private int version = ConfigManager.CONFIG_VERSION;
	@Setting(value = "Permission")
	private PermissionConfig permissionConfig = new PermissionConfig();
	@Setting(value = "Misc")
	private MiscConfig miscConfig = new MiscConfig();
	@Setting(value = "Storage")
	private StorageConfig storageConfig = new StorageConfig();
	@Setting(value = "World")
	private WorldConfig worldConfig = new WorldConfig();

	public int getVersion() {
		return version;
	}

	public PermissionConfig getPermissionConfig() {
		return permissionConfig;
	}

	public MiscConfig getMiscConfig() {
		return miscConfig;
	}

	public StorageConfig getStorageConfig() {
		return storageConfig;
	}

	public WorldConfig getWorldConfig() {
		return worldConfig;
	}
}