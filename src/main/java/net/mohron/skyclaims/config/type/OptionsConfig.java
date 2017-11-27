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
  private String schematic = "skyfactory";
  @Setting(value = "default-biome", comment = "The biome type to use, if any, to set an island to on creation and reset.")
  private String biome = "";
  @Setting(value = "min-size", comment = "Half of the width of an island, in blocks, used to claim the player's usable space (8-256).")
  private int minSize = 48;
  @Setting(value = "max-size", comment = "Half of the max width of an island, in blocks (min-256).")
  private int maxSize = 64;
  @Setting(value = "max-islands", comment = "The max number of islands a player can join. 0 to disable.")
  private int maxIslands = 0;

  public String getSchematic() {
    return schematic;
  }

  public String getBiome() {
    return biome;
  }

  public int getMinSize() {
    return Math.max(8, Math.min(minSize, 256));
  }

  public int getMaxSize() {
    return Math.max(minSize, Math.min(maxSize, 256));
  }

  public int getMaxIslands() {
    return maxIslands;
  }
}
