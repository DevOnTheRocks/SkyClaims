package net.mohron.skyclaims.claim;

import me.ryanhamshire.griefprevention.Visualization;
import me.ryanhamshire.griefprevention.api.claim.Claim;
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

public class SkyClaim implements IClaim {
	public SkyClaim() {

	}

	public Visualization getVisualizer() {
		return null;
	}

	public UUID getOwnerUniqueId() {
		return null;
	}

	public boolean isAdminClaim() {
		return false;
	}

	public boolean isBasicClaim() {
		return false;
	}

	public boolean isSubdivision() {
		return false;
	}

	public boolean isWildernessClaim() {
		return false;
	}

	public UUID getID() {
		return null;
	}

	public boolean canSiege(Player defender) {
		return false;
	}

	public void removeSurfaceFluids(Claim exclusionClaim) {

	}

	public boolean hasSurfaceFluids() {
		return false;
	}

	public int getArea() {
		return 0;
	}

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}

	public boolean hasFullAccess(User user) {
		return false;
	}

	public boolean hasFullTrust(User user) {
		return false;
	}

	public String allowEdit(Player player) {
		return null;
	}

	public String allowBuild(Object source, Location<World> location, User user) {
		return null;
	}

	public String allowBreak(Object source, BlockSnapshot blockSnapshot, User user) {
		return null;
	}

	public String allowAccess(User user) {
		return null;
	}

	public String allowAccess(User user, Location<World> location) {
		return null;
	}

	public String allowAccess(User user, Location<World> location, boolean interact) {
		return null;
	}

	public String allowItemDrop(User user, Location<World> location) {
		return null;
	}

	public String allowContainers(User user, Location<World> location) {
		return null;
	}

	public String allowGrantPermission(Player player) {
		return null;
	}

	public void clearPermissions() {

	}

	public Location<World> getLesserBoundaryCorner() {
		return null;
	}

	public Location<World> getGreaterBoundaryCorner() {
		return null;
	}

	public String getOwnerName() {
		return null;
	}

	public World getWorld() {
		return null;
	}

	public boolean contains(Location<World> location, boolean ignoreHeight, boolean excludeSubdivisions) {
		return false;
	}

	public boolean contains(Location<World> location) {
		return false;
	}

	public boolean overlaps(Claim otherClaim) {
		return false;
	}

	public String allowMoreEntities() {
		return null;
	}

	public boolean greaterThan(Claim otherClaim) {
		return false;
	}

	public ArrayList<Chunk> getChunks() {
		return null;
	}

	public Set<Long> getChunkHashes() {
		return null;
	}

	public IClaimData getClaimData() {
		return null;
	}

	public ClaimStorageData getClaimStorage() {
		return null;
	}

	public void setClaimData(IClaimData data) {

	}

	public void setClaimStorage(ClaimStorageData storage) {

	}

	public void updateClaimStorageData() {

	}

	public boolean protectPlayersInClaim() {
		return false;
	}

	public boolean isPvpEnabled() {
		return false;
	}

	public void setPvpEnabled(Tristate value) {

	}

	public boolean pvpRulesApply() {
		return false;
	}

	public void unload() {

	}

	public Context getContext() {
		return null;
	}
}
