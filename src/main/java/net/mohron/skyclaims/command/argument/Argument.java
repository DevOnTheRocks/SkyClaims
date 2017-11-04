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

package net.mohron.skyclaims.command.argument;

import org.spongepowered.api.text.Text;

public class Argument {

    public static BiomeArgument biome(Text key) {
        return new BiomeArgument(key);
    }

    public static IslandArgument island(Text key) {
        return new IslandArgument(key);
    }

    public static PositiveIntegerArgument positiveInteger(Text key) {
        return new PositiveIntegerArgument(key);
    }

    public static SchematicArgument schematic(Text key) {
        return new SchematicArgument(key);
    }

    public static SortArgument sort(Text key) {
        return new SortArgument(key);
    }

    public static TargetArgument target(Text key) {
        return new TargetArgument(key);
    }

    public static TwoUserArgument twoUser(Text key, Text key2) {
        return new TwoUserArgument(key, key2);
    }
}
