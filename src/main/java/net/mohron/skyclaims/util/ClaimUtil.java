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

package net.mohron.skyclaims.util;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClaimUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static Claim createIslandClaim(UUID ownerUniqueId, Region region) throws CreateIslandException {
		ClaimManager claimManager = PLUGIN.getGriefPrevention().getClaimManager(PLUGIN.getConfig().getWorldConfig().getWorld());

		Claim claim = null;
		ClaimResult claimResult;
		do {
			claimResult = ClaimUtil.createIslandClaimResult(ownerUniqueId, region);
			switch (claimResult.getResultType()) {
				case SUCCESS:
					claim = claimResult.getClaim().get();
					claimManager.addClaim(claim, PLUGIN.getCause());
					PLUGIN.getLogger().debug(String.format(
						"Creating %s's claim in region (%s, %s). Claimed from %sx, %sz - %sx, %sz.",
						getName(ownerUniqueId),
						region.getX(), region.getZ(),
						claim.getLesserBoundaryCorner().getBlockX(), claim.getLesserBoundaryCorner().getBlockZ(),
						claim.getGreaterBoundaryCorner().getBlockX(), claim.getGreaterBoundaryCorner().getBlockZ()
					));

					claim.getData().setResizable(false);
					claim.getData().setClaimExpiration(false);
					claim.getData().setRequiresClaimBlocks(false);
					break;
				case OVERLAPPING_CLAIM:
					for (Claim claim1 : claimResult.getClaims()) {
						claimManager.deleteClaim(claim1, PLUGIN.getCause());
					}
					PLUGIN.getLogger().info(String.format(
						"Removing claim overlapping %s's island (Owner: %s, ID: %s).",
						getName(ownerUniqueId),
						claimResult.getClaim().get().getOwnerName(),
						claimResult.getClaim().get().getUniqueId()
					));
					break;
				default:
					throw new CreateIslandException(Text.of(TextColors.RED,
						String.format("Failed to create claim: %s!", claimResult.getResultType())
					));
			}
		} while (claim == null);

		return claim;
	}

	private static ClaimResult createIslandClaimResult(UUID ownerUniqueId, Region region) {
		int initialSpacing = 256 - Options.getMinSize(ownerUniqueId);
		return Claim.builder()
			.world(PLUGIN.getConfig().getWorldConfig().getWorld())
			.bounds(
				new Vector3i(
					region.getLesserBoundary().getX() + initialSpacing, 0,
					region.getLesserBoundary().getZ() + initialSpacing
				),
				new Vector3i(
					region.getGreaterBoundary().getX() - initialSpacing, 255,
					region.getGreaterBoundary().getZ() - initialSpacing
				)
			)
			.owner(ownerUniqueId)
			.type(ClaimType.BASIC)
			.requiresClaimBlocks(false)
			.cause(PLUGIN.getCause())
			.cuboid(false)
			.build();
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static void createSpawnClaim(List<Region> regions) {
		ClaimManager claimManager = PLUGIN.getGriefPrevention().getClaimManager(PLUGIN.getConfig().getWorldConfig().getWorld());

		ClaimResult claimResult = ClaimUtil.createSpawnClaimResult(regions);
		if (claimResult.successful()) {
			PLUGIN.getLogger().debug(String.format("Reserved %s regions for spawn. Admin Claim: %s", regions.size(),
				claimResult.getClaim().get().getUniqueId()
			));
			claimManager.addClaim(claimResult.getClaim().get(), PLUGIN.getCause());
		}
	}

	private static ClaimResult createSpawnClaimResult(List<Region> regions) {
		Region lesserRegion = new Region(0, 0);
		Region greaterRegion = new Region(0, 0);
		for (Region region : regions) {
			if (region.getX() == region.getZ()) {
				if (region.getX() < lesserRegion.getX()) lesserRegion = region;
				if (region.getX() < greaterRegion.getX()) greaterRegion = region;
			}
		}

		return Claim.builder()
			.world(PLUGIN.getConfig().getWorldConfig().getWorld())
			.bounds(
				new Vector3i(lesserRegion.getLesserBoundary().getX(), 0, lesserRegion.getLesserBoundary().getZ()),
				new Vector3i(greaterRegion.getGreaterBoundary().getX(), 255, greaterRegion.getGreaterBoundary().getZ())
			)
			.type(ClaimType.ADMIN)
			.cause(PLUGIN.getCause())
			.cuboid(false)
			.build();
	}

	public static void setEntryFlag(Claim claim, boolean entry) {
		// Set ENTER_CLAIM flag in the claim supplied to the defined value
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private static String getName(UUID uuid) {
		Optional<User> user = PLUGIN.getGame().getServiceManager().provide(UserStorageService.class).get().get(uuid);
		if (user.isPresent()) {
			return user.get().getName();
		} else {
			try {
				return PLUGIN.getGame().getServer().getGameProfileManager().get(uuid).get().getName().get();
			} catch (Exception e) {
				return "somebody";
			}
		}
	}
}