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
public class EntityConfig {

    @Setting(value = "Limit-Spawning", comment = "Whether SkyClaims should limit island entity spawns.")
    private boolean limitSpawning = false;
    @Setting(value = "Max-Hostile", comment = "The max number of hostile mob spawns allowed per island. 0 to disable.\n" +
        "Can be overridden with the 'skyclaims.max-spawns.hostile' option.")
    private int maxHostile = 50;
    @Setting(value = "Max-Passive", comment = "The max number of passive mob spawns allowed per island. 0 to disable.\n" +
        "Can be overridden with the 'skyclaims.max-spawns.passive' option.")
    private int maxPassive = 30;
    @Setting(value = "Max-Spawns", comment = "The overall max number of mob spawns allowed per island. 0 to disable.\n" +
        "Can be overridden with the 'skyclaims.max-spawns' option.")
    private int maxSpawns = 70;

    public boolean isLimitSpawning() {
        return limitSpawning;
    }

    public int getMaxHostile() {
        return maxHostile;
    }

    public int getMaxPassive() {
        return maxPassive;
    }

    public int getMaxSpawns() {
        return maxSpawns;
    }
}
