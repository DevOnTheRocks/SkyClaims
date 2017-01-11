package net.mohron.skyclaims.listener;

import net.mohron.skyclaims.util.IslandUtil;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.World;

public class RespawnHandler {
	@Listener
	public void onPlayerRespawn(RespawnPlayerEvent event, @Root Player player) {
		if (event.isBedSpawn() || !IslandUtil.hasIsland(player.getUniqueId())) return;

		IslandUtil.getIslandByOwner(player.getUniqueId()).ifPresent(island -> {
			Transform<World> transform = event.getToTransform();
			transform.setLocation(island.getSpawn());
			event.setToTransform(transform);
		});
	}
}
