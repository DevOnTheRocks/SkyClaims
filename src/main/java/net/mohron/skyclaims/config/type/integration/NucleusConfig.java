/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2022 Mohron
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

package net.mohron.skyclaims.config.type.integration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class NucleusConfig extends PluginIntegration {

  @Setting(value = "First-Join-Kit", comment =
      "Not Implemented. Add \"kit give @p firstJoinKit\" to Reset-Commands."
          + "\nSet to enable/disable redeeming Nucleus' FirstJoinKit when using /is reset.")
  private boolean firstJoinKit = true;
  @Setting(value = "Island-Home", comment = "Set to enable/disable /is sethome & /is home as a configurable home separate from an island spawn.")
  private boolean homesEnabled = true;

  public boolean isFirstJoinKit() {
    return firstJoinKit;
  }

  public boolean isHomesEnabled() {
    return homesEnabled;
  }
}
