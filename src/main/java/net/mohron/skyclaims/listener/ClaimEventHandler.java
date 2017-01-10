package net.mohron.skyclaims.listener;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.event.CreateClaimEvent;
import me.ryanhamshire.griefprevention.api.event.DeleteClaimEvent;
import me.ryanhamshire.griefprevention.api.event.ResizeClaimEvent;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.util.WorldUtil;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;

public class ClaimEventHandler {
	@Listener
	public void onClaimCreate(CreateClaimEvent event, @Root Player player) {
		Claim claim = event.getClaim();
		if (event.isCancelled() || event.getClaim().getWorld() != WorldUtil.getDefaultWorld()) return;
		if (player.hasPermission(Permissions.ADMIN_OVERRIDE)) return;

		player.sendMessage(Text.of("You can create claims in this dimension!"));
		event.setCancelled(true);
	}

	@Listener
	public void onClaimDelete(DeleteClaimEvent event, @Root Player player) {
		Claim claim = event.getClaim();
		if (event.isCancelled() || event.getClaim().getWorld() != WorldUtil.getDefaultWorld()) return;
		if (player.hasPermission(Permissions.ADMIN_OVERRIDE)) return;

		player.sendMessage(Text.of("You can delete claims in this dimension!"));
		event.setCancelled(true);
	}

	@Listener
	public void onClaimResize(ResizeClaimEvent event, @Root Player player) {
		Claim claim = event.getClaim();
		if (event.isCancelled() || !SkyClaims.islandClaims.contains(claim)) return;
		if (player.hasPermission(Permissions.ADMIN_OVERRIDE)) return;

		player.sendMessage(Text.of("You can not resize an island claim!"));
		event.setCancelled(true);
	}
}
