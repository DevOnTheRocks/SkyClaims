package net.mohron.skyclaims.claim;

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

    public GPClaimSystem() {

    }

    public IClaimResult createClaim(World world, int x1, int x2, int y1, int y2, int z1, int z2, UUID claimId, IClaim parent, IClaim.Type claimType, boolean cuboid, Player player) {
        return null;
    }

    public IClaim getClaim(WorldProperties world, UUID claimId) {
        return (IClaim) SkyClaims.getInstance().getGriefPrevention().dataStore.getClaim(world, claimId);
    }
}
