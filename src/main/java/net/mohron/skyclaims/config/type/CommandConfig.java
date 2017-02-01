/*
 * SkyClaims - A Skyblock plugin made for Sponge designed for
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

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class CommandConfig {
	@Setting(value = "Separate-BiomeType-Permissions")
	private boolean separateBiomePerms = false;
	@Setting(value = "Separate-Schematic-Permissions")
	private boolean separateSchematicPerms = false;
	@Setting(value = "Separate-Target-Permissions")
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
