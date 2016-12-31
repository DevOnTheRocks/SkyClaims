package net.mohron.skyclaims.claim;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.UUID;

/**
 * Created by cossacksman on 30/12/16.
 */
public interface IClaimSystem {
    public IClaimResult createClaim(World world, int x1, int x2, int y1, int y2, int z1, int z2, UUID claimId, IClaim parent, IClaim.Type claimType, boolean cuboid, Player player);
    public IClaim getClaim(WorldProperties world, UUID claimId);
}
