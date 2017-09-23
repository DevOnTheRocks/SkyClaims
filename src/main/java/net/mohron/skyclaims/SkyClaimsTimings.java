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

package net.mohron.skyclaims;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;

public class SkyClaimsTimings {

    // TASKS
    public static final Timing GENERATE_ISLAND = Timings.of(SkyClaims.getInstance().getPluginContainer(), "onGenerateIsland");
    public static final Timing CLEAR_ISLAND = Timings.of(SkyClaims.getInstance().getPluginContainer(), "onClearIsland");
    public static final Timing ISLAND_CLEANUP = Timings.of(SkyClaims.getInstance().getPluginContainer(), "onIslandCleanupTask");

    // LISTENERS
    public static final Timing CLIENT_JOIN = Timings.of(SkyClaims.getInstance().getPluginContainer(), "onClientJoin");
    public static final Timing CREATE_ISLAND_ON_JOIN = Timings.of(SkyClaims.getInstance().getPluginContainer(), "createIslandOnJoin", CLIENT_JOIN);
    public static final Timing DELIVER_INVITES = Timings.of(SkyClaims.getInstance().getPluginContainer(), "deliverInvites", CLIENT_JOIN);
    public static final Timing ENTITY_SPAWN = Timings.of(SkyClaims.getInstance().getPluginContainer(), "EntitySpawn");
    public static final Timing PLAYER_RESPAWN = Timings.of(SkyClaims.getInstance().getPluginContainer(), "onPlayerRespawn");
    public static final Timing SCHEMATIC_HANDLER = Timings.of(SkyClaims.getInstance().getPluginContainer(), "onSelectSchematic");
    public static final Timing WORLD_LOAD = Timings.of(SkyClaims.getInstance().getPluginContainer(), "onWorldLoad");
    public static final Timing CLAIM_HANDLER = Timings.of(SkyClaims.getInstance().getPluginContainer(), "onClaimEvent");
    public static final Timing PORTAL_HANDLER = Timings.of(SkyClaims.getInstance().getPluginContainer(), "onPortalUse");

}
