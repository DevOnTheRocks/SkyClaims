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

package net.mohron.skyclaims.config.type.integration;

import com.griefdefender.api.permission.flag.Flag;
import com.griefdefender.api.permission.flag.Flags;
import java.util.HashMap;
import java.util.Map;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class GriefDefenderConfig {

  @Setting(value = "Wilderness-Flags", comment = "Use to set up default flags to be set on the Wilderness claim.")
  private Map<Flag, Boolean> wildernessFlags = new HashMap<Flag, Boolean>() {{
    put(Flags.BLOCK_BREAK, false);
    put(Flags.BLOCK_PLACE, false);
  }};

  public Map<Flag, Boolean> getWildernessFlags() {
    return wildernessFlags;
  }
}
