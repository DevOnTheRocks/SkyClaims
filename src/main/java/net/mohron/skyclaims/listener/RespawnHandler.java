package net.mohron.skyclaims.listener;

import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class RespawnHandler {
	@Listener
	public void onPlayerRespawn(RespawnPlayerEvent event, @Root Player player) {
		if (event.isBedSpawn() || !Island.hasIsland(player.getUniqueId())) return;

		Island.getByOwner(player.getUniqueId()).ifPresent(island -> {
			event.setToTransform(island.getSpawn());
		});
	}
}
