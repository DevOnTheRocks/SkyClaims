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

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ryanhamshire.griefprevention.api.claim.ClaimFlag;
import me.ryanhamshire.griefprevention.api.claim.TrustType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.util.Tristate;

@ConfigSerializable
public class GriefPrevention {

  @Setting(value = "Disabled-Trust-Types", comment = "Trust types to disable use on Islands.")
  private List<TrustType> disabledTrustTypes = Lists
      .newArrayList(TrustType.ACCESSOR, TrustType.CONTAINER);

  //    @Setting(value = "Island-Flags", comment = "Use to set up default flags to be set on Island claims.")
  //    private EnumMap<ClaimFlag, Tristate> islandFlags = new EnumMap<ClaimFlag, Tristate>(ClaimFlag.class) {{
  //        put(ClaimFlag.ITEM_DROP, Tristate.FALSE);
  //        put(ClaimFlag.ITEM_PICKUP, Tristate.FALSE);
  //    }};
  //
  @Setting(value = "Wilderness-Flags", comment = "Use to set up default flags to be set on the Wilderness claim.")
  private Map<ClaimFlag, Tristate> wildernessFlags = new HashMap<ClaimFlag, Tristate>() {{
    put(ClaimFlag.BLOCK_BREAK, Tristate.FALSE);
    put(ClaimFlag.BLOCK_PLACE, Tristate.FALSE);
  }};

  public List<TrustType> getDisabledTrustTypes() {
    return disabledTrustTypes;
  }

  //    public EnumMap<ClaimFlag, Tristate> getIslandFlags() {
  //        return islandFlags;
  //    }
  //
  public Map<ClaimFlag, Tristate> getWildernessFlags() {
    return wildernessFlags;
  }
}
