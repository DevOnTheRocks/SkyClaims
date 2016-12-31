package net.mohron.skyclaims.claim;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BasicClaimSystem implements IClaimSystem {
	private Map<UUID, IClaim> claims = new HashMap<>();

	public BasicClaimSystem() {

	}

	public IClaimResult createClaim(World world, Vector3i a, Vector3i b, UUID claimId, IClaim parent, IClaim.Type claimType, boolean cuboid, Player player) {
		// TODO:- Create Claim
		// TODO:- Add claim to claims
		// TODO:- Return Claim Result
		return null;
	}

	public IClaim getClaim(WorldProperties world, UUID claimId) {
		return claims.get(claimId);
	}
}
