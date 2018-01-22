/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims.world;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimResultType;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import me.ryanhamshire.griefprevention.api.claim.TrustType;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.exception.InvalidRegionException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.util.ClaimUtil;
import net.mohron.skyclaims.world.region.IRegionPattern;
import net.mohron.skyclaims.world.region.Region;
import net.mohron.skyclaims.world.region.SpiralRegionPattern;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.Ambient;
import org.spongepowered.api.entity.living.Aquatic;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextSource;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Island implements ContextSource {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();
  private static final IRegionPattern PATTERN = new SpiralRegionPattern();

  private final UUID id;
  private final Context context;

  private UUID owner;
  private UUID claim;
  private Transform<World> spawn;
  private boolean locked;

  public Island(User owner, String schematic) throws CreateIslandException {
    this.id = UUID.randomUUID();
    this.context = new Context("island", this.id.toString());
    this.owner = owner.getUniqueId();
    Region region;
    try {
      region = PATTERN.nextRegion();
    } catch (InvalidRegionException e) {
      throw new CreateIslandException(e.getText());
    }
    this.spawn = new Transform<>(region.getCenter());
    this.locked = true;

    // Create the island claim
    Claim claim = ClaimUtil.createIslandClaim(owner.getUniqueId(), region);
    claim.getData().setSpawnPos(spawn.getLocation());
    claim.getData().save();
    this.claim = claim.getUniqueId();

    // Run commands defined in config on creation
    for (String command : PLUGIN.getConfig().getMiscConfig().getCreateCommands()) {
      PLUGIN.getGame().getCommandManager()
          .process(PLUGIN.getGame().getServer().getConsole(),
              command.replace("@p", owner.getName()));
    }

    // Generate the island using the specified schematic
    GenerateIslandTask generateIsland = new GenerateIslandTask(owner.getUniqueId(), this,
        schematic);
    PLUGIN.getGame().getScheduler().createTaskBuilder().execute(generateIsland).submit(PLUGIN);

    save();
  }

  public Island(UUID id, UUID owner, UUID claimId, Vector3d spawnLocation, boolean locked) {
    this.id = id;
    this.context = new Context("island", this.id.toString());
    this.owner = owner;
    this.spawn = new Transform<>(PLUGIN.getConfig().getWorldConfig().getWorld(), spawnLocation);
    this.locked = locked;
    this.claim = claimId;

    ClaimManager claimManager = PLUGIN.getGriefPrevention().getClaimManager(spawn.getExtent());
    Claim claim = claimManager.getClaimByUUID(claimId).orElse(null);
    if (claim != null) {
      this.claim = claimId;
      int initialWidth = Options.getMinSize(owner) * 2;
      // Resize claims smaller than the player's initial-size
      if (claim.getWidth() < initialWidth) {
        setWidth(initialWidth);
      }
      if (claim.getType() != ClaimType.TOWN) {
        claim.changeType(ClaimType.TOWN);
      }
    } else {
      try {
        this.claim = ClaimUtil.createIslandClaim(owner, getRegion()).getUniqueId();
        PLUGIN.queueForSaving(this);
      } catch (CreateIslandException e) {
        PLUGIN.getLogger().error(
            String.format("Failed to create claim while loading %s (%s).", getName().toPlain(), id),
            e);
      }
    }
  }

  public static Optional<Island> get(UUID islandUniqueId) {
    return Optional.ofNullable(SkyClaims.islands.get(islandUniqueId));
  }

  public static Optional<Island> get(Location<World> location) {
    return SkyClaims.islands.entrySet().stream()
        .filter(i -> i.getValue().contains(location))
        .map(Map.Entry::getValue)
        .findFirst();
  }

  public static Optional<Island> get(Claim claim) {
    for (Island island : SkyClaims.islands.values()) {
      if (island.getClaim().isPresent() && island.getClaim().get().equals(claim)) {
        return Optional.of(island);
      }
    }
    return Optional.empty();
  }

  @Deprecated
  public static Optional<Island> getByOwner(UUID owner) {
    for (Island island : SkyClaims.islands.values()) {
      if (island.getOwnerUniqueId().equals(owner)) {
        return Optional.of(island);
      }
    }
    return Optional.empty();
  }

  public static boolean hasIsland(UUID owner) {
    if (SkyClaims.islands.isEmpty()) {
      return false;
    }
    for (Island island : SkyClaims.islands.values()) {
      if (island.getOwnerUniqueId().equals(owner)) {
        return true;
      }
    }
    return false;
  }

  public static int getTotalIslandsOwned(UUID owner) {
    return (int) SkyClaims.islands.values().stream()
        .filter(i -> i.getOwnerUniqueId().equals(owner))
        .count();
  }

  public static int getTotalIslands(User user) {
    return (int) SkyClaims.islands.values().stream()
        .filter(i -> i.isMember(user))
        .count();
  }

  public UUID getUniqueId() {
    return id;
  }

  @Override
  public Context getContext() {
    return this.context;
  }

  public UUID getOwnerUniqueId() {
    return owner;
  }

  public String getOwnerName() {
    return getName(owner);
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  private String getName(UUID uuid) {
    Optional<User> player = PLUGIN.getGame().getServiceManager()
        .provideUnchecked(UserStorageService.class).get(uuid);
    if (player.isPresent()) {
      return player.get().getName();
    } else {
      try {
        return PLUGIN.getGame().getServer().getGameProfileManager().get(uuid).get().getName().get();
      } catch (Exception e) {
        return "somebody";
      }
    }
  }

  public UUID getClaimUniqueId() {
    return claim;
  }

  public Optional<Claim> getClaim() {
    return PLUGIN.getGriefPrevention().getClaimManager(getWorld()).getClaimByUUID(this.claim);
  }

  public Date getDateCreated() {
    return getClaim().isPresent() ? Date.from(getClaim().get().getData().getDateCreated())
        : Date.from(Instant.now());
  }

  public Date getDateLastActive() {
    return getClaim().isPresent() ? Date.from(getClaim().get().getData().getDateLastActive())
        : Date.from(Instant.now());
  }

  public Text getName() {
    return (getClaim().isPresent() && getClaim().get().getName().isPresent())
        ? getClaim().get().getName().get()
        : Text.of(TextColors.AQUA, getOwnerName(), "'s Island");
  }

  public String getSortableName() {
    return getName().toPlain().toLowerCase();
  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
    save();
  }

  public World getWorld() {
    return PLUGIN.getConfig().getWorldConfig().getWorld();
  }

  public Transform<World> getSpawn() {
    return spawn;
  }

  public void setSpawn(Transform<World> transform) {
    if (contains(transform.getLocation())) {
      Transform<World> spawn = new Transform<>(getWorld(), transform.getPosition(),
          transform.getRotation());
      if (transform.getLocation().getY() < 0 || transform.getLocation().getY() > 256) {
        spawn.setPosition(
            new Vector3d(spawn.getLocation().getX(),
                PLUGIN.getConfig().getWorldConfig().getIslandHeight(),
                spawn.getLocation().getZ()
            ));
      }
      this.spawn = spawn;
      getClaim().ifPresent(claim -> claim.getData().setSpawnPos(spawn.getPosition().toInt()));
      save();
    }
  }

  public boolean contains(Location<World> location) {
    if (!getClaim().isPresent()) {
      return location.getExtent().equals(getWorld()) && Region.get(location).equals(getRegion());
    }

    int x = location.getBlockX();
    int z = location.getBlockZ();
    Location lesserBoundaryCorner = getClaim().get().getLesserBoundaryCorner();
    Location greaterBoundaryCorner = getClaim().get().getGreaterBoundaryCorner();

    return x >= lesserBoundaryCorner.getBlockX()
        && x <= greaterBoundaryCorner.getBlockX()
        && z >= lesserBoundaryCorner.getBlockZ()
        && z <= greaterBoundaryCorner.getBlockZ();
  }

  public int getWidth() {
    return getClaim().isPresent() ? getClaim().get().getWidth() : 512;
  }

  private boolean setWidth(int width) {
    if (width < 0 || width > 512) {
      return false;
    }
    PLUGIN.getGriefPrevention().getWorldPlayerData(getWorld().getProperties(), owner)
        .ifPresent(data -> getClaim().ifPresent(claim -> {
          int spacing = (512 - width) / 2;
          claim.resize(
              new Vector3i(
                  getRegion().getLesserBoundary().getX() + spacing,
                  data.getMinClaimLevel(),
                  getRegion().getLesserBoundary().getZ() + spacing
              ),
              new Vector3i(
                  getRegion().getGreaterBoundary().getX() - spacing,
                  data.getMaxClaimLevel(),
                  getRegion().getGreaterBoundary().getZ() - spacing
              )
          );
        }));
    return getWidth() == width;
  }

  public void addMember(User user, PrivilegeType type) {
    switch (type) {
      case OWNER:
        UUID existingOwner = owner;
        transfer(user);
        getClaim().ifPresent(c -> c.addUserTrust(existingOwner, TrustType.MANAGER));
        break;
      case MANAGER:
      case MEMBER:
        getClaim().ifPresent(c -> c.addUserTrust(user.getUniqueId(), type.getTrustType()));
        break;
      case NONE:
        getClaim().ifPresent(c -> c.removeUserTrust(user.getUniqueId(), type.getTrustType()));
        break;
    }
  }

  public void promote(User user) {
    getClaim().ifPresent(c -> {
      if (c.isUserTrusted(user, TrustType.BUILDER)) {
        c.removeUserTrust(user.getUniqueId(), TrustType.NONE);
        c.addUserTrust(user.getUniqueId(), TrustType.MANAGER);
      } else if (c.isUserTrusted(user, TrustType.MANAGER)) {
        c.removeUserTrust(user.getUniqueId(), TrustType.NONE);
        UUID existingOwner = owner;
        transfer(user);
        c.addUserTrust(existingOwner, TrustType.MANAGER);
      }
    });
  }

  public void demote(User user) {
    getClaim().ifPresent(c -> {
      if (c.isUserTrusted(user, TrustType.MANAGER)) {
        c.removeUserTrust(user.getUniqueId(), TrustType.NONE);
        c.addUserTrust(user.getUniqueId(), TrustType.BUILDER);
      }
    });
  }

  public void removeMember(User user) {
    getClaim().ifPresent(c -> c.removeUserTrust(user.getUniqueId(), TrustType.NONE));
  }

  public List<String> getMembers() {
    List<String> members = Lists.newArrayList();
    if (!getClaim().isPresent()) {
      return members;
    }
    for (UUID builder : getClaim().get().getUserTrusts(TrustType.BUILDER)) {
      members.add(getName(builder));
    }
    return members;
  }

  public List<String> getManagers() {
    List<String> members = Lists.newArrayList();
    if (!getClaim().isPresent()) {
      return members;
    }
    for (UUID manager : getClaim().get().getUserTrusts(TrustType.MANAGER)) {
      members.add(getName(manager));
    }
    return members;
  }

  public int getTotalMembers() {
    return (!getClaim().isPresent()) ? 1 : new HashSet<>(getClaim().get().getUserTrusts()).size();
  }

  public int getTotalEntities() {
    return getEntities().size();
  }

  public int getTotalTileEntities() {
    return getTileEntities().size();
  }

  public boolean isMember(User user) {
    return user.getUniqueId().equals(owner)
        || getClaim().isPresent()
        && (getClaim().get().isUserTrusted(user, TrustType.BUILDER)
        || getClaim().get().isUserTrusted(user, TrustType.MANAGER));
  }

  public boolean isManager(User user) {
    return user.getUniqueId().equals(owner)
        || (getClaim().isPresent() && getClaim().get().isUserTrusted(user, TrustType.MANAGER));
  }

  public boolean isOwner(User user) {
    return user.getUniqueId().equals(owner);
  }

  public PrivilegeType getPrivilegeType(User user) {
    if (isOwner(user)) {
      return PrivilegeType.OWNER;
    } else if (isManager(user)) {
      return PrivilegeType.MANAGER;
    } else if (isMember(user)) {
      return PrivilegeType.MEMBER;
    } else {
      return PrivilegeType.NONE;
    }
  }

  public Collection<Player> getPlayers() {
    return getWorld().getPlayers().stream()
        .filter(p -> contains(p.getLocation()))
        .collect(Collectors.toList());
  }

  public Collection<Entity> getEntities() {
    return getWorld().getEntities(e -> contains(e.getLocation()));
  }

  public Collection<Entity> getHostileEntities() {
    return getWorld().getEntities(e -> contains(e.getLocation()) && e instanceof Monster);
  }

  public Collection<Entity> getPassiveEntities() {
    return getWorld().getEntities(
        e -> contains(e.getLocation()) && e instanceof Animal || e instanceof Aquatic
            || e instanceof Ambient);
  }

  public Collection<Entity> getItemEntities() {
    return getWorld().getEntities(e -> contains(e.getLocation()) && e instanceof Item);
  }

  public Collection<TileEntity> getTileEntities() {
    return getWorld().getTileEntities(e -> contains(e.getLocation()));
  }

  public Region getRegion() {
    return Region.get(getSpawn().getLocation());
  }

  public void transfer(User user) {
    getClaim().ifPresent(claim -> {
      ClaimResult result = claim.transferOwner(user.getUniqueId());
      if (result.getResultType() != ClaimResultType.SUCCESS) {
        PLUGIN.getLogger().error(
            String.format("Failed to transfer claim (%s) when transferring %s's island to %s.\n%s",
                claim.getUniqueId(), getOwnerName(), user.getName(), result.getResultType()
            ));
      }
    });
    this.owner = user.getUniqueId();
    save();
  }

  public void expand(int blocks) {
    if (blocks < 1) {
      return;
    }
    setWidth(getWidth() + blocks * 2);
  }

  private void save() {
    SkyClaims.islands.put(id, this);
    PLUGIN.getDatabase().saveIsland(this);
  }

  public void clear() {
    RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(getRegion());
    PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask)
        .submit(PLUGIN);
  }

  public void reset(String schematic, boolean runCommands) {
    RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(this, schematic,
        runCommands);
    PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask)
        .submit(PLUGIN);
  }

  public void delete() {
    ClaimManager claimManager = PLUGIN.getGriefPrevention().getClaimManager(getWorld());
    getClaim().ifPresent(claim -> claimManager.deleteClaim(claim));
    SkyClaims.islands.remove(id);
    PLUGIN.getDatabase().removeIsland(this);
  }
}
