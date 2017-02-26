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

package net.mohron.skyclaims.listener;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class ClientJoinHandler {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	@Listener
	public void onClientJoin(ClientConnectionEvent.Join event, @Root Player player) {
		if (!PLUGIN.getConfig().getMiscConfig().createIslandOnJoin() || Island.hasIsland(player.getUniqueId()))
			return;

		Sponge.getScheduler().createTaskBuilder()
			.execute(src -> {
				try {
					new Island(player, Options.getDefaultSchematic(player.getUniqueId()));
					PLUGIN.getLogger().info(String.format("Automatically created an island for %s.", player.getName()));
				} catch (CreateIslandException e) {
					// Oh well, we tried!
					PLUGIN.getLogger().warn(String.format("Failed to create an island on join for %s.\n%s", player.getName(), e.getMessage()));
				}
			})
			.delayTicks(40)
			.submit(PLUGIN);

	}
}