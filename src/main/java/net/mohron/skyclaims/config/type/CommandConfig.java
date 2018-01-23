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

import com.google.common.collect.Lists;
import java.util.List;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class CommandConfig {

  @Setting(value = "Base-Command-Alias", comment = "The alias to use as the base command.")
  private List<String> baseAlias = Lists.newArrayList("is", "island");
  @Setting(value = "Admin-Command-Alias", comment = "The alias to use as the base admin command.")
  private List<String> adminAlias = Lists.newArrayList("sc", "isa");

  public List<String> getBaseAlias() {
    return baseAlias.isEmpty() ? Lists.newArrayList("is") : baseAlias;
  }

  public List<String> getAdminAlias() {
    return adminAlias.isEmpty() ? Lists.newArrayList("sc") : adminAlias;
  }
}
