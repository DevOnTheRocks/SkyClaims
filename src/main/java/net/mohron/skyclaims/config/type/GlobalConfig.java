package net.mohron.skyclaims.config.type;

import net.mohron.skyclaims.config.ConfigManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class GlobalConfig {
	@Setting(value = "Config-Version")
	private int version;
	@Setting(value = "Permission")
	private PermissionConfig permissionConfig;
	@Setting(value = "Misc")
	private MiscConfig miscConfig;
	@Setting(value = "Storage")
	private StorageConfig storageConfig;
	@Setting(value = "World")
	private WorldConfig worldConfig;

	public GlobalConfig() {
		version = ConfigManager.CONFIG_VERSION;
		permissionConfig = new PermissionConfig();
		miscConfig = new MiscConfig();
		storageConfig = new StorageConfig();
		worldConfig = new WorldConfig();
	}

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