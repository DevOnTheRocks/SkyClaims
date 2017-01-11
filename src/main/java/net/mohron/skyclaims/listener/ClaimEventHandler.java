package net.mohron.skyclaims.listener;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.event.CreateClaimEvent;
import me.ryanhamshire.griefprevention.api.event.DeleteClaimEvent;
import me.ryanhamshire.griefprevention.api.event.ResizeClaimEvent;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.WorldUtil;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ClaimEventHandler {
	@Listener
	public void onClaimCreate(CreateClaimEvent event, @Root Player player) {
		Claim claim = event.getClaim();
		if (event.isCancelled() || (claim.getWorld() != WorldUtil.getDefaultWorld() && claim.isBasicClaim())) return;

		player.sendMessage(Text.of(TextColors.RED, "You cannot create claims in this dimension!"));
		event.setCancelled(true);
	}

	@Listener
	public void onClaimDelete(DeleteClaimEvent event, @Root Player player) {
		Claim claim = event.getClaim();
		if (event.isCancelled() || !SkyClaims.islandClaims.contains(claim)) return;

		player.sendMessage(Text.of(TextColors.RED, "You cannot delete an island claim!"));
		event.setCancelled(true);
	}

	@Listener
	public void onClaimResize(ResizeClaimEvent event, @Root Player player) {
		Claim claim = event.getClaim();
		if (event.isCancelled() || !SkyClaims.islandClaims.contains(claim)) return;

		player.sendMessage(Text.of(TextColors.RED, "You cannot resize an island claim!"));
		event.setCancelled(true);
	}
}
