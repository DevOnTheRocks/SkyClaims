package net.mohron.skyclaims.world;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimResultType;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.util.ClaimUtil;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.util.WorldUtil;
import net.mohron.skyclaims.world.region.IRegionPattern;
import net.mohron.skyclaims.world.region.Region;
import net.mohron.skyclaims.world.region.SpiralRegionPattern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class Island {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(WorldUtil.getDefaultWorld());
	private static final IRegionPattern PATTERN = new SpiralRegionPattern();

	private UUID id;
	private UUID owner;
	private Claim claim;
	private Location<World> spawn;
	private boolean locked;

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public Island(User owner, String schematic) throws CreateIslandException {
		this.id = UUID.randomUUID();
		this.owner = owner.getUniqueId();
		this.locked = false;

		Region region = PATTERN.nextRegion();
		ClaimResult claimResult = ClaimUtil.createIslandClaim(owner, region);

		do {
			switch (claimResult.getResultType()) {
				case SUCCESS:
					CLAIM_MANAGER.addClaim(claimResult.getClaim().get(), Cause.source(PLUGIN).build());
					this.claim = claimResult.getClaim().get();
					break;
				case OVERLAPPING_CLAIM:
					if (ClaimUtil.isIslandClaim(claimResult.getClaim().get())) {
						throw new CreateIslandException(Text.of(TextColors.RED, "Failed to create island claim: The claim at the target location belongs to another island!"));
					} else {
						CLAIM_MANAGER.deleteClaim(claimResult.getClaim().get(), Cause.source(PLUGIN).build());
					}
					break;
				default:
					throw new CreateIslandException(Text.of(TextColors.RED, String.format("Failed to create claim: %s!", claimResult.getResultType())));
			}
		} while (claimResult.getResultType() == ClaimResultType.OVERLAPPING_CLAIM);

		// Run commands defined in config on creation
		ConfigUtil.getCreateCommands().ifPresent(commands -> {
			for (String command : commands) {
				PLUGIN.getGame().getCommandManager().process(PLUGIN.getGame().getServer().getConsole(), command.replace("@p", owner.getName()));
			}
		});

		// Generate the island using the specified schematic
		GenerateIslandTask generateIsland = new GenerateIslandTask(owner, this, schematic);
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(generateIsland).submit(PLUGIN);

		save();
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public Island(UUID id, UUID owner, UUID claimId, Vector3i spawnLocation, boolean locked) {
		this.id = id;
		this.owner = owner;
		this.spawn = new Location<>(ConfigUtil.getWorld(), spawnLocation);
		this.locked = locked;

		// 1st attempt to load claim by ID
		// 2nd attempt to find claim by location
		// Finally create a new claim after removing all overlapping claims if any
		if (CLAIM_MANAGER.getClaimByUUID(claimId).isPresent()) {
			this.claim = CLAIM_MANAGER.getClaimByUUID(claimId).get();
		} else if (CLAIM_MANAGER.getClaimAt(spawn, true).getType() == ClaimType.BASIC
				&& CLAIM_MANAGER.getClaimAt(spawn, true).getOwnerUniqueId().equals(owner)) {
			this.claim = CLAIM_MANAGER.getClaimAt(spawn, true);
		} else {
			ClaimResult claim;
			do {
				claim = ClaimUtil.createIslandClaim(getOwner().get(), getRegion());
				switch (claim.getResultType()) {
					case SUCCESS:
						this.claim = claim.getClaim().get();
						break;
					case OVERLAPPING_CLAIM:
						CLAIM_MANAGER.deleteClaim(claim.getClaim().get(), Cause.source(PLUGIN).build());
						PLUGIN.getLogger().error(String.format("Removing overlapping claim (Owner: %s, ID: %s) while restoring %s's island.", claim.getClaim().get().getOwnerName(), claim.getClaim().get().getUniqueId(), getOwnerName()));
						break;
					default:
						PLUGIN.getLogger().error(String.format("Failed to create claim for %s's island, reason: %s", getOwnerName(), claim.getResultType()));
						break;
				}
			} while (claim.getResultType() == ClaimResultType.OVERLAPPING_CLAIM);
		}
	}

	public UUID getUniqueId() {
		return id;
	}

	public UUID getOwnerUniqueId() {
		return owner;
	}

	public String getOwnerName() {
		return (getOwner().isPresent()) ? getOwner().get().getName() : "Unknown";
	}

	public Optional<User> getOwner() {
		Optional<UserStorageService> optStorage = Sponge.getServiceManager().provide(UserStorageService.class);
		if (optStorage.isPresent()) {
			UserStorageService storage = optStorage.get();
			return (storage.get(owner).isPresent()) ? Optional.of(storage.get(owner).get()) : Optional.empty();
		}
		return Optional.empty();
	}

	public Claim getClaim() {
		return claim;
	}

	public String getDateCreated() {
		return claim.getClaimData().getDateCreated().toString();
	}

	public Text getName() {
		return (claim.getName().isPresent()) ? claim.getName().get() : Text.of(claim.getUniqueId().toString());
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		save();
	}

	public World getWorld() {
		return ConfigUtil.getWorld();
	}

	public Location<World> getSpawn() {
		return spawn;
	}

	public void setSpawn(Location<World> spawn) {
		this.spawn = spawn;
		save();
	}

	public boolean isWithinIsland(Location<World> location) {
		return claim.contains(location, true, false);
	}

	public int getRadius() {
		return (claim.getGreaterBoundaryCorner().getBlockX() - claim.getLesserBoundaryCorner().getBlockX()) / 2;
	}

	public boolean hasPermissions(Player player) {
		return player.getUniqueId().equals(claim.getOwnerUniqueId())
				|| claim.getTrustManager().getContainers().contains(player.getUniqueId())
				|| claim.getTrustManager().getBuilders().contains(player.getUniqueId())
				|| claim.getTrustManager().getManagers().contains(player.getUniqueId());
	}

	public Region getRegion() {
		return new Region(getSpawn().getChunkPosition().getX() >> 5, getSpawn().getChunkPosition().getZ() >> 5);
	}

	public void save() {
		SkyClaims.islands.put(id, this);
		PLUGIN.getDatabase().saveIsland(this);
	}

	public void delete() {
		RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(getRegion());
		PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask).submit(PLUGIN);
		SkyClaims.islands.remove(id);
		PLUGIN.getDatabase().removeIsland(this);
	}
}