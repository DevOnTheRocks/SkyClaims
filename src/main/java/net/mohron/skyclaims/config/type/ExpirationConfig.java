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

import com.google.common.base.Preconditions;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ExpirationConfig {

  @Setting(value = "Enabled", comment = "Whether SkyClaims should remove inactive islands that exceed the expiration threshold.")
  private boolean enabled = false;
  @Setting(value = "Interval", comment = "The frequency, in minutes, that islands will be considered for removal.")
  private int interval = 15;
  @Setting(value = "Threshold", comment =
      "The amount of time, in days, that an island must be inactive before removal.\n" +
          "Can be overridden with the 'skyclaims.expiration' option.")
  private int threshold = 30;

  public boolean isEnabled() {
    return enabled;
  }

  public int getInterval() {
    Preconditions.checkState(interval > 0);
    return interval;
  }

  public int getThreshold() {
    Preconditions.checkState(threshold > 0);
    return threshold;
  }
}
