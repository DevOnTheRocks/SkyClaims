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

@ConfigSerializable
public class MiscConfig {

  @Setting(value = "Log-Biomes", comment = "Whether a list of biomes and their permissions should be logged.")
  private boolean logBiomes = false;
  @Setting(value = "Island-on-Join", comment = "Automatically create an island for a player on join.")
  private boolean islandOnJoin = false;
  @Setting(value = "Clear-on-Teleports", comment = "Whether players inventory gets cleared on 'is tp', 'is home' and 'is create' commands, to prevent resource sharing")
  private boolean clearOnTeleports = false;
  @Setting(value = "List-Schematics", comment = "Whether players with access to multiple schematics see a list when not specifying a schematic.")
  private boolean listSchematics = true;
  @Setting(value = "Teleport-on-Creation", comment = "Automatically teleport the owner to their island on creation.")
  private boolean teleportOnCreate = true;
  @Setting(value = "Create-Commands", comment = "Commands to run on island creation and reset. Use @p in place of the player's name.")
  private List<String> createCommands = new ArrayList<>();
  @Setting(value = "Reset-Commands", comment = "Commands to run on island resets only. Use @p in place of the player's name.")
  private List<String> resetCommands = new ArrayList<>();
  @Setting(value = "Date-Format", comment = "The date format used throughout the plugin.\n" +
      "http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html")
  private String dateFormat = "MMMM d, yyyy h:mm a";

  public boolean isLogBiomes() {
    return logBiomes;
  }

  public boolean createIslandOnJoin() {
    return islandOnJoin;
  }

  public boolean isClearOnTeleports() {
    return clearOnTeleports;
  }

  public boolean isListSchematics() {
    return listSchematics;
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

  public SimpleDateFormat getDateFormat() {
    try {
      return new SimpleDateFormat(dateFormat);
    } catch (IllegalArgumentException e) {
      SkyClaims.getInstance().getLogger().info("Invalid Date Format: {}", dateFormat);
      return new SimpleDateFormat("MMMM d, yyyy h:mm a");
    }
  }
}
