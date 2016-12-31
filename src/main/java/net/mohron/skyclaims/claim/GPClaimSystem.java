package net.mohron.skyclaims.claim;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.DataStore;
import me.ryanhamshire.griefprevention.claim.Claim;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.*;

/**
 * Created by cossacksman on 30/12/16.
 */
public class GPClaimSystem implements IClaimSystem {

    DataStore dataStore = SkyClaims.getInstance().getGriefPrevention().dataStore;

    public GPClaimSystem() {

    }

    public IClaimResult createClaim(World world, Vector3i a, Vector3i b, UUID claimId, IClaim parent, IClaim.Type claimType, boolean cuboid, Player player) {
        Claim.Type type = Claim.Type.valueOf(claimType.name());
        return (IClaimResult) dataStore.createClaim(world, a.getX(), b.getX(), a.getY(), b.getY(), a.getZ(), b.getZ(), claimId, (Claim)parent, type, cuboid, player);
    }

    public IClaim getClaim(WorldProperties world, UUID claimId) {
        return (IClaim) dataStore.getClaim(world, claimId);
    }
}
