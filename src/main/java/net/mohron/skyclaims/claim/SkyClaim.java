package net.mohron.skyclaims.claim;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.TrustManager;
import me.ryanhamshire.griefprevention.api.data.ClaimData;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SkyClaim implements Claim {
	public SkyClaim() {

	}

	@Override
	public UUID getUniqueId() {
		return null;
	}

	@Override
	public String getOwnerName() {
		return null;
	}

	@Override
	public Optional<Claim> getParent() {
		return null;
	}

	@Override
	public Location<World> getLesserBoundaryCorner() {
		return null;
	}

	@Override
	public Location<World> getGreaterBoundaryCorner() {
		return null;
	}

	@Override
	public boolean isCuboid() {
		return false;
	}

	@Override
	public int getArea() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public List<Chunk> getChunks() {
		return null;
	}

	@Override
	public World getWorld() {
		return null;
	}

	@Override
	public ClaimResult resizeClaim(int i, int i1, int i2, int i3, int i4, int i5, Cause cause) {
		return null;
	}

	@Override
	public ClaimResult createSubdivision(Vector3i vector3i, Vector3i vector3i1, UUID uuid, boolean b, Cause cause) {
		return null;
	}

	@Override
	public List<Claim> getSubdivisions() {
		return null;
	}

	@Override
	public TrustManager getTrustManager() {
		return null;
	}

	@Override
	public boolean contains(Location<World> location, boolean b, boolean b1) {
		return false;
	}

	@Override
	public boolean overlaps(Claim claim) {
		return false;
	}

	@Override
	public boolean extendClaim(int i) {
		return false;
	}

	@Override
	public ClaimResult deleteSubdivision(Claim claim, Cause cause) {
		return null;
	}

	@Override
	public ClaimData getClaimData() {
		return null;
	}

	@Override
	public Context getContext() {
		return null;
	}
}
