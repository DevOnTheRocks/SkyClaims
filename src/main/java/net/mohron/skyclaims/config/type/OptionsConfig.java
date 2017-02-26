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

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class OptionsConfig {
	@Setting(value = "default-schematic", comment = "The schematic that should be used when not otherwise specified.")
	private String schematic = "island";
	@Setting(value = "default-biome", comment = "The biome type to use, if any, to set an island to on creation and reset.")
	private String biome = "";
	@Setting(value = "min-size", comment = "Half of the width of an island, in blocks, used to claim the player's usable space.")
	private int minSize = 48;
	@Setting(value = "max-size", comment = "Half of the max width of an island.")
	private int maxSize = 64;

	public String getSchematic() {
		return schematic;
	}

	public String getBiome() {
		return biome;
	}

	public int getMinSize() {
		return (minSize < 8 || minSize > 256) ? 48 : minSize;
	}

	public int getMaxSize() {
		return (maxSize < minSize || maxSize > 256) ? minSize : maxSize;
	}
}
