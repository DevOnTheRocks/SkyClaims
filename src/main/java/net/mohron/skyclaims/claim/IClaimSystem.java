package net.mohron.skyclaims.claim;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.UUID;

public interface IClaimSystem {
	public IClaimResult createClaim(World world, Vector3i a, Vector3i b, UUID claimId, IClaim parent, IClaim.Type claimType, boolean cuboid, Player player);

	public IClaim getClaim(WorldProperties world, UUID claimId);
}
