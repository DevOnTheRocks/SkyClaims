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

package net.mohron.skyclaims.permissions;

public class Permissions {

  // User Permissions
  public static final String COMMAND_CREATE = "skyclaims.command.create";
  public static final String COMMAND_DELETE = "skyclaims.command.delete";
  public static final String COMMAND_DEMOTE = "skyclaims.command.demote";
  public static final String COMMAND_EXPAND = "skyclaims.command.expand";
  public static final String COMMAND_ENTITY_INFO = "skyclaims.command.entity.info";
  public static final String COMMAND_HOME = "skyclaims.command.home";
  public static final String COMMAND_INFO = "skyclaims.command.info";
  public static final String COMMAND_INVITE = "skyclaims.command.invite";
  public static final String COMMAND_KICK = "skyclaims.command.kick";
  public static final String COMMAND_LEAVE = "skyclaims.command.leave";
  public static final String COMMAND_LIST = "skyclaims.command.list.base";
  public static final String COMMAND_LIST_UNLOCKED = "skyclaims.command.list.unlocked";
  public static final String COMMAND_LIST_SORT = "skyclaims.command.list.sort";
  public static final String COMMAND_LOCK = "skyclaims.command.lock";
  public static final String COMMAND_PROMOTE = "skyclaims.command.promote";
  public static final String COMMAND_RESET = "skyclaims.command.reset";
  public static final String COMMAND_SPAWN = "skyclaims.command.spawn";
  public static final String COMMAND_SET_HOME = "skyclaims.command.sethome";
  public static final String COMMAND_SET_SPAWN = "skyclaims.command.setspawn";
  public static final String COMMAND_SET_BIOME = "skyclaims.command.setbiome";

  // Keep Inventory
  public static final String KEEP_INV_PLAYER_CREATE = "skyclaims.keepinv.player.create";
  public static final String KEEP_INV_PLAYER_DELETE = "skyclaims.keepinv.player.delete";
  public static final String KEEP_INV_PLAYER_KICK = "skyclaims.keepinv.player.kick";
  public static final String KEEP_INV_PLAYER_LEAVE = "skyclaims.keepinv.player.leave";
  public static final String KEEP_INV_PLAYER_RESET = "skyclaims.keepinv.player.reset";
  public static final String KEEP_INV_ENDERCHEST_CREATE = "skyclaims.keepinv.enderchest.create";
  public static final String KEEP_INV_ENDERCHEST_DELETE = "skyclaims.keepinv.enderchest.delete";
  public static final String KEEP_INV_ENDERCHEST_KICK = "skyclaims.keepinv.enderchest.kick";
  public static final String KEEP_INV_ENDERCHEST_LEAVE = "skyclaims.keepinv.enderchest.leave";
  public static final String KEEP_INV_ENDERCHEST_RESET = "skyclaims.keepinv.enderchest.reset";

  // Command Arguments
  public static final String COMMAND_ARGUMENTS_SCHEMATICS = "skyclaims.arguments.schematics";
  public static final String COMMAND_ARGUMENTS_BIOMES = "skyclaims.arguments.biomes";
  public static final String COMMAND_ARGUMENTS_BLOCK = "skyclaims.arguments.block";
  public static final String COMMAND_ARGUMENTS_CHUNK = "skyclaims.arguments.chunk";

  // Admin Permissions
  // Commands
  public static final String COMMAND_DELETE_OTHERS = "skyclaims.admin.delete";
  public static final String COMMAND_EXPAND_OTHERS = "skyclaims.admin.expand";
  public static final String COMMAND_ENTITY_CLEAR = "skyclaims.admin.entity.clear";
  public static final String COMMAND_LIST_ALL = "skyclaims.admin.list";
  public static final String COMMAND_LOCK_OTHERS = "skyclaims.admin.lock.others";
  public static final String COMMAND_RELOAD = "skyclaims.admin.reload";
  public static final String COMMAND_RESET_KEEP_INV = "skyclaims.admin.reset.keepinv";
  public static final String COMMAND_SET_BIOME_OTHERS = "skyclaims.admin.setbiome";
  public static final String COMMAND_SPAWN_OTHERS = "skyclaims.admin.spawn";
  public static final String COMMAND_SET_SPAWN_OTHERS = "skyclaims.admin.setspawn";
  public static final String COMMAND_TRANSFER = "skyclaims.admin.transfer";
  public static final String COMMAND_VERSION = "skyclaims.admin.version";
  // Schematics
  public static final String COMMAND_SCHEMATIC = "skyclaims.admin.schematic.base";
  public static final String COMMAND_SCHEMATIC_COMMAND = "skyclaims.admin.schematic.command";
  public static final String COMMAND_SCHEMATIC_CREATE = "skyclaims.admin.schematic.create";
  public static final String COMMAND_SCHEMATIC_DELETE = "skyclaims.admin.schematic.delete";
  public static final String COMMAND_SCHEMATIC_INFO = "skyclaims.admin.schematic.info";
  public static final String COMMAND_SCHEMATIC_LIST = "skyclaims.admin.schematic.list.base";
  public static final String COMMAND_SCHEMATIC_LIST_ALL = "skyclaims.admin.schematic.list.all";
  public static final String COMMAND_SCHEMATIC_SET_BIOME = "skyclaims.admin.schematic.setbiome";
  public static final String COMMAND_SCHEMATIC_SET_HEIGHT = "skyclaims.admin.schematic.setheight";
  public static final String COMMAND_SCHEMATIC_SET_ICON = "skyclaims.admin.schematic.seticon";
  public static final String COMMAND_SCHEMATIC_SET_NAME = "skyclaims.admin.schematic.setname";
  public static final String COMMAND_SCHEMATIC_SET_PRESET = "skyclaims.admin.schematic.setpreset";
  // Bypass/Exemptions
  public static final String EXEMPT_KICK = "skyclaims.admin.kick.exempt";
  public static final String BYPASS_LOCK = "skyclaims.admin.lock.bypass";
  public static final String BYPASS_TRUST = "skyclaims.admin.trust.bypass";
}
