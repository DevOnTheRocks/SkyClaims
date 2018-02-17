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

package net.mohron.skyclaims.world.region;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.Coordinate;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandManager;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Region {

  private int x;
  private int z;

  public Region(int x, int z) {
    this.x = x;
    this.z = z;
  }

  public static boolean isOccupied(Region region) {
    if (IslandManager.ISLANDS.isEmpty()) {
      return false;
    }

    for (Island island : IslandManager.ISLANDS.values()) {
      if (region.equals(island.getRegion())) {
        return true;
      }
    }

    return false;
  }

  public static Region get(Location<World> location) {
    return new Region(location.getBlockX() >> 4 >> 5, location.getBlockZ() >> 4 >> 5);
  }

  public int getX() {
    return x;
  }

  public int getZ() {
    return z;
  }

  public Coordinate getLesserBoundary() {
    return new Coordinate(x << 5 << 4, z << 5 << 4);
  }

  public Coordinate getGreaterBoundary() {
    return new Coordinate((((x + 1) << 5) << 4) - 1, (((z + 1) << 5) << 4) - 1);
  }

  public Location<World> getCenter() {
    return new Location<>(
        SkyClaims.getInstance().getConfig().getWorldConfig().getWorld(),
        (getGreaterBoundary().getX() + getLesserBoundary().getX()) / 2.0,
        SkyClaims.getInstance().getConfig().getWorldConfig().getIslandHeight(),
        (getGreaterBoundary().getZ() + getLesserBoundary().getZ()) / 2.0
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Region region = (Region) o;

    return x == region.x && z == region.z;
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + z;
    return result;
  }
}