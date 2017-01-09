package net.mohron.skyclaims.world;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.exception.InvalidRegionException;
import net.mohron.skyclaims.util.ClaimUtil;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.util.WorldUtil;
import net.mohron.skyclaims.world.region.IRegionPattern;
import net.mohron.skyclaims.world.region.Region;
import net.mohron.skyclaims.world.region.SpiralRegionPattern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashSet;
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
		Region region;
		try {
			region = PATTERN.nextRegion();
		} catch (InvalidRegionException e) {
			throw new CreateIslandException(e.getText());
		}
		this.spawn = region.getCenterBlock();
		PLUGIN.getLogger().info(String.format("Set Island spawn to %s", region.getCenterBlock().toString()));
		this.locked = false;

		// Create the island claim
		this.claim = ClaimUtil.createIslandClaim(owner, region);
		// Set claim to not expire or be resizable
		this.claim.getClaimData().setResizable(false);
		this.claim.getClaimData().setClaimExpiration(false);

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
			try {
				this.claim = ClaimUtil.createIslandClaim(getOwner().get(), getRegion());
				// Set claim to not expire or be resizable
				if (this.claim.getClaimData().isResizable()) this.claim.getClaimData().setResizable(false);
				if (this.claim.getClaimData().allowClaimExpiration())
					this.claim.getClaimData().setClaimExpiration(false);
			} catch (CreateIslandException e) {
				PLUGIN.getLogger().error("Failed to create a new claim for island " + id);
			}
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
		return (claim.getName().isPresent()) ? claim.getName().get() : Text.of(getOwnerName(), "'s Island");
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		ClaimUtil.setEntryFlag(claim, locked);
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

	public HashSet<UUID> getMembers() {
		HashSet<UUID> members = new HashSet<>();
		for (UUID member : claim.getTrustManager().getBuilders()) {
			members.add(member);
		}
		for (UUID member : claim.getTrustManager().getManagers()) {
			members.add(member);
		}
		return members;
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