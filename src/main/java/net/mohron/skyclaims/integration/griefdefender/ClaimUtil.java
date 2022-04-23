/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2022 Mohron
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

package net.mohron.skyclaims.integration.griefdefender;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;
import com.griefdefender.api.claim.ClaimResult;
import com.griefdefender.api.claim.ClaimTypes;
import com.griefdefender.api.data.PlayerData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public final class ClaimUtil {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  private ClaimUtil() {
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public static Claim createIslandClaim(@Nonnull UUID ownerUniqueId, @Nonnull Region region) throws CreateIslandException {
    Sponge.getCauseStackManager().pushCause(PLUGIN.getPluginContainer());
    ClaimManager claimManager = GriefDefender.getCore().getClaimManager(PLUGIN.getConfig().getWorldConfig().getWorld().getUniqueId());

    Claim claim = null;
    ClaimResult claimResult;
    do {
      claimResult = ClaimUtil.createIslandClaimResult(ownerUniqueId, region);
      switch (claimResult.getResultType()) {
        case SUCCESS:
          claim = claimResult.getClaim();
          PLUGIN.getLogger().debug(
              "Creating {}'s claim in region ({}, {}). Claimed from {}x, {}z - {}x, {}z.",
              getName(ownerUniqueId),
              region.getX(), region.getZ(),
              claim.getLesserBoundaryCorner().getX(),
              claim.getLesserBoundaryCorner().getZ(),
              claim.getGreaterBoundaryCorner().getX(),
              claim.getGreaterBoundaryCorner().getZ()
          );
          break;
        case OVERLAPPING_CLAIM:
          for (Claim overlappedClaim : claimResult.getClaims()) {
            ClaimResult delete = claimManager.deleteClaim(overlappedClaim);
            if (!delete.successful()) {
              PLUGIN.getLogger().error("{}: {}", delete.getResultType(), delete.getMessage().toString());
              throw new CreateIslandException(Text.of(
                  TextColors.RED, "Failed to delete overlapping claim: ", overlappedClaim.getUniqueId()
              ));
            }
            PLUGIN.getLogger().info(
                "Removed claim overlapping {}'s island (Owner: {}, ID: {}).",
                getName(ownerUniqueId),
                overlappedClaim.getOwnerName(),
                overlappedClaim.getUniqueId()
            );
          }
          break;
        default:
          throw new CreateIslandException(Text.of(
              TextColors.RED, "Failed to create claim: ", claimResult.getResultType(),
              Text.NEW_LINE, claimResult.getMessage().orElse(Component.text("No message provided"))
          ));
      }
    } while (claim == null);
    Sponge.getCauseStackManager().popCause();

    return claim;
  }

  private static ClaimResult createIslandClaimResult(@Nonnull UUID ownerUniqueId, @Nonnull Region region) throws CreateIslandException {
    int initialSpacing = 256 - Options.getMinSize(ownerUniqueId);
    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    checkNotNull(world, "Error Creating Claim: World (%s) is null", world.getName());
    checkArgument(world.isLoaded(), "Error Creating Claim: World (%s) is not loaded", world.getName());
    checkNotNull(ownerUniqueId, "Error Creating Claim: Owner is null");
    checkNotNull(region, "Error Creating Claim: Region is null");

    PlayerData playerData = Optional.ofNullable(GriefDefender.getCore()
        .getPlayerData(world.getUniqueId(), ownerUniqueId))
        .orElseThrow(() -> new CreateIslandException(Text.of(
            TextColors.RED, "Unable to load GriefDefender player data!"
        )));

    return Claim.builder()
        .type(ClaimTypes.TOWN)
        .world(world.getUniqueId())
        .bounds(
            new Vector3i(
                region.getLesserBoundary().getX() + initialSpacing, playerData.getMinClaimLevel(),
                region.getLesserBoundary().getZ() + initialSpacing
            ),
            new Vector3i(
                region.getGreaterBoundary().getX() - initialSpacing, playerData.getMaxClaimLevel(),
                region.getGreaterBoundary().getZ() - initialSpacing
            )
        )
        .owner(ownerUniqueId)
        .expire(false)
        .resizable(false)
        .requireClaimBlocks(false)
        .sizeRestrictions(false)
        .createLimitRestrictions(false)
        .build();
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public static void createSpawnClaim(List<Region> regions) {
    ClaimResult claimResult = ClaimUtil.createSpawnClaimResult(regions);
    if (claimResult.successful()) {
      PLUGIN.getLogger().debug("Reserved {} regions for spawn. Admin Claim: {}", regions.size(), claimResult.getClaim().getUniqueId());
    }
  }

  private static ClaimResult createSpawnClaimResult(List<Region> regions) {
    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    checkNotNull(world, "Error Creating Claim: World (%s) is null", world.getName());
    checkArgument(world.isLoaded(), "Error Creating Claim: World (%s) is not loaded", world.getName());
    Region lesserRegion = new Region(0, 0);
    Region greaterRegion = new Region(0, 0);
    for (Region region : regions) {
      if (region.getX() == region.getZ()) {
        if (region.getX() < lesserRegion.getX()) {
          lesserRegion = region;
        }
        if (region.getX() > greaterRegion.getX()) {
          greaterRegion = region;
        }
      }
    }

    return Claim.builder()
        .type(ClaimTypes.ADMIN)
        .world(world.getUniqueId())
        .bounds(
            new Vector3i(lesserRegion.getLesserBoundary().getX(), 0, lesserRegion.getLesserBoundary().getZ()),
            new Vector3i(greaterRegion.getGreaterBoundary().getX(), 255, greaterRegion.getGreaterBoundary().getZ())
        )
        .build();
  }

  private static String getName(UUID uuid) {
    Optional<User> user = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid);
    if (user.isPresent()) {
      return user.get().getName();
    } else {
      try {
        return Sponge.getServer().getGameProfileManager().get(uuid).get().getName().orElse("somebody");
      } catch (Exception e) {
        return "somebody";
      }
    }
  }
}