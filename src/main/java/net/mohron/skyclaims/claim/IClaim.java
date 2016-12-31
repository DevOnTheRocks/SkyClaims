package net.mohron.skyclaims.claim;

import me.ryanhamshire.griefprevention.Visualization;
import me.ryanhamshire.griefprevention.claim.Claim;
import me.ryanhamshire.griefprevention.configuration.ClaimStorageData;
import me.ryanhamshire.griefprevention.configuration.IClaimData;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by cossacksman on 30/12/16.
 */
public interface IClaim {
    enum Type {
        ADMIN,
        BASIC,
        SUBDIVISION,
        WILDERNESS
    }

    // TODO:- Redact methods to what's necessary

    public Visualization getVisualizer();
    public UUID getOwnerUniqueId();
    public boolean isAdminClaim();
    public boolean isBasicClaim();
    public boolean isSubdivision();
    public boolean isWildernessClaim();
    public UUID getID();
    public boolean canSiege(Player defender);
    public void removeSurfaceFluids(Claim exclusionClaim);
    boolean hasSurfaceFluids();
    public int getArea();
    public int getWidth();
    public int getHeight();
    public boolean hasFullAccess(User user);
    public boolean hasFullTrust(User user);
    public String allowEdit(Player player);
    public String allowBuild(Object source, Location<World> location, User user);
    public String allowBreak(Object source, BlockSnapshot blockSnapshot, User user);
    public String allowAccess(User user);
    public String allowAccess(User user, Location<World> location);
    public String allowAccess(User user, Location<World> location, boolean interact);
    public String allowItemDrop(User user, Location<World> location);
    public String allowContainers(User user, Location<World> location);
    public String allowGrantPermission(Player player);
    public void clearPermissions();
    public Location<World> getLesserBoundaryCorner();
    public Location<World> getGreaterBoundaryCorner();
    public String getOwnerName();
    public World getWorld();
    public boolean contains(Location<World> location, boolean ignoreHeight, boolean excludeSubdivisions);
    public boolean contains(Location<World> location);
    public boolean overlaps(Claim otherClaim);
    public String allowMoreEntities();
    boolean greaterThan(Claim otherClaim);
    public ArrayList<Chunk> getChunks();
    public Set<Long> getChunkHashes();
    public IClaimData getClaimData();
    public ClaimStorageData getClaimStorage();
    public void setClaimData(IClaimData data);
    public void setClaimStorage(ClaimStorageData storage);
    public void updateClaimStorageData();
    public boolean protectPlayersInClaim();
    public boolean isPvpEnabled();
    public void setPvpEnabled(Tristate value);
    public boolean pvpRulesApply();
    public void unload();
    public Context getContext();
}
