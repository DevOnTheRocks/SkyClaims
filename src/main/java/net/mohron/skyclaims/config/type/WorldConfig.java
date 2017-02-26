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

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.WorldUtil;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.world.World;

@ConfigSerializable
public class WorldConfig {
	@Setting(value = "SkyClaims-World", comment = "Name of the world to manage islands in. Default: world")
	private String worldName;
	@Setting(value = "Island-Height", comment = "Height to build islands at. Default: 72")
	private int defaultHeight;
	@Setting(value = "Spawn-Regions", comment = "The height & width of regions to reserve for Spawn. Default: 1")
	private int spawnRegions;

	public WorldConfig() {
		worldName = SkyClaims.getInstance().getGame().getServer().getDefaultWorldName();
		defaultHeight = 72;
		spawnRegions = 1;
	}

	public World getWorld() {
		return SkyClaims.getInstance().getGame().getServer().getWorld(worldName).orElse(WorldUtil.getDefaultWorld());
	}

	public String getWorldName() {
		return worldName;
	}

	public int getDefaultHeight() {
		return defaultHeight;
	}

	public int getSpawnRegions() {
		return spawnRegions;
	}
}