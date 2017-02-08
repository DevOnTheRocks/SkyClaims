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

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.event.CreateClaimEvent;
import me.ryanhamshire.griefprevention.api.event.DeleteClaimEvent;
import me.ryanhamshire.griefprevention.api.event.ResizeClaimEvent;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.WorldConfig;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class ClaimEventHandler {
	private static WorldConfig config = SkyClaims.getInstance().getConfig().getWorldConfig();

	@Listener
	public void onClaimCreate(CreateClaimEvent event, @Root Player player) {

		Claim claim = event.getClaim();
		if (!claim.getWorld().equals(config.getWorld()) || !claim.isBasicClaim()) return;

		event.setMessage(Text.of(TextColors.RED, "You cannot create a basic claim in this dimension!"));
		event.setCancelled(true);
	}

	@Listener
	public void onClaimDelete(DeleteClaimEvent event, @Root Player player) {
		for (Claim claim : event.getClaims()) {
			if (claim.isBasicClaim() && claim.getWorld().equals(config.getWorld())) {
				if (event instanceof DeleteClaimEvent.Abandon)
					event.setMessage(Text.of(TextColors.RED, "You cannot abandon an island claim!"));
				else
					Island.get(claim).ifPresent(island -> {
						event.setMessage(Text.of(TextColors.RED, "A claim you are attempting to delete belongs to an island.\n", Text
								.of(TextColors.AQUA, "Do you want to delete ", island.getOwnerName(), "'s island?").toBuilder()
								.onHover(TextActions.showText(Text.of("Click here to delete.")))
								.onClick(TextActions.runCommand("isa delete " + island.getOwnerName()))
						));
					});
				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onClaimResize(ResizeClaimEvent event, @Root Player player) {
		Claim claim = event.getClaim();
		if (!claim.getWorld().equals(config.getWorld()) || !claim.isBasicClaim()) return;

		event.setMessage(Text.of(TextColors.RED, "You cannot resize an island claim!"));
		event.setCancelled(true);
	}
}