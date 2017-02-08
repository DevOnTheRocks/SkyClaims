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

package net.mohron.skyclaims.database;

import net.mohron.skyclaims.world.Island;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface IDatabase {
	HashMap<UUID, Island> loadData();

	void saveData(Collection<Island> islands);

	void saveData(Map<UUID, Island> islands);

	void saveIsland(Island island);

	void removeIsland(Island island);
}
