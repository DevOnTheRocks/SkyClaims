package net.mohron.skyclaims.listener;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class ClientJoinHandler {
	@Listener
	public void onClientJoin(ClientConnectionEvent.Join event, @Root Player player) {
		if (!SkyClaims.getInstance().getConfig().getMiscConfig().createIslandOnJoin()) return;

		try {
			if (!Island.hasIsland(player.getUniqueId()))
				new Island(player, Options.getStringOption(player.getUniqueId(), Options.DEFAULT_SCHEMATIC));
			SkyClaims.getInstance().getLogger().info(String.format("Automatically created an island for %s.", player.getName()));
		} catch (CreateIslandException e) {
			// Oh well, we tried!
			SkyClaims.getInstance().getLogger().warn(String.format("Failed to create an island on join for %s.\n%s", player.getName(), e.getMessage()));
		}
	}
}