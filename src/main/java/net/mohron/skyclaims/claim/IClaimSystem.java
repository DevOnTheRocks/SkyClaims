package net.mohron.skyclaims.claim;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public interface IClaimSystem {
	public ClaimResult createClaim(World world, Vector3i a, Vector3i b, UUID claimId, Claim parent, ClaimType claimType, boolean cuboid, Player player);

	public Optional<Claim> getClaim(UUID claimId);
}
