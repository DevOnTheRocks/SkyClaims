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
	@Setting(value = "Options", comment = "The default values options use when not explicitly set.\ngithub.com/DevOnTheRocks/SkyClaims/wiki/Options")
	private OptionsConfig optionsConfig;

	public GlobalConfig() {
		version = ConfigManager.CONFIG_VERSION;
		permissionConfig = new PermissionConfig();
		miscConfig = new MiscConfig();
		storageConfig = new StorageConfig();
		worldConfig = new WorldConfig();
		optionsConfig = new OptionsConfig();
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

	public OptionsConfig getOptionsConfig() {
		return optionsConfig;
	}
}