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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.mohron.skyclaims.SkyClaims;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

@ConfigSerializable
public class MiscConfig {

  public enum ClearItemsType {BLACKLIST, WHITELIST}

  @Setting(value = "Log-Biomes", comment = "Whether a list of biomes and their permissions should be logged.")
  private boolean logBiomes = false;
  @Setting(value = "Island-on-Join", comment = "Automatically create an island for a player on join.\n" +
      "Requires a valid default schematic to be set (skyclaims.default-schematic)")
  private boolean islandOnJoin = false;
  @Setting(value = "Teleport-on-Creation", comment = "Automatically teleport the owner to their island on creation.")
  private boolean teleportOnCreate = true;
  @Setting(value = "Create-Commands", comment = "Commands to run on island creation and reset. Use @p in place of the player's name.")
  private List<String> createCommands = new ArrayList<>();
  @Setting(value = "Reset-Commands", comment = "Commands to run on island resets only. Use @p in place of the player's name.")
  private List<String> resetCommands = new ArrayList<>();
  @Setting(value = "Clear-Items", comment = "Items to be removed from players inventories when going on or off an island / claim")
  private List<ItemType> clearItems = new ArrayList<>();
  @Setting(value = "Clear-Items-Type", comment = "Sets whether the Clear-Items list should be treated as a blacklist or whitelist.")
  private ClearItemsType clearItemsType = ClearItemsType.BLACKLIST;
  @Setting(value = "Date-Format", comment = "The date format used throughout the plugin.\n" +
      "http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html")
  private String dateFormat = "MMMM d, yyyy h:mm a";

  public boolean isLogBiomes() {
    return logBiomes;
  }

  public boolean createIslandOnJoin() {
    return islandOnJoin;
  }

  public boolean isTeleportOnCreate() {
    return teleportOnCreate;
  }

  public List<String> getCreateCommands() {
    return createCommands;
  }

  public List<String> getResetCommands() {
    return resetCommands;
  }

  public List<ItemType> getClearItems() {
    return clearItems;
  }

  public ClearItemsType getClearItemsType() {
    return clearItemsType;
  }

  public SimpleDateFormat getDateFormat() {
    try {
      return new SimpleDateFormat(dateFormat);
    } catch (IllegalArgumentException e) {
      SkyClaims.getInstance().getLogger().error("Invalid Date Format: {}", dateFormat);
      return new SimpleDateFormat("MMMM d, yyyy h:mm a");
    }
  }
}
