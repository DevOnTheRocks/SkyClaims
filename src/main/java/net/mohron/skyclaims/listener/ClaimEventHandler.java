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

package net.mohron.skyclaims.listener;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.event.BorderClaimEvent;
import me.ryanhamshire.griefprevention.api.event.ChangeClaimEvent;
import me.ryanhamshire.griefprevention.api.event.CreateClaimEvent;
import me.ryanhamshire.griefprevention.api.event.DeleteClaimEvent;
import me.ryanhamshire.griefprevention.api.event.TrustClaimEvent;
import me.ryanhamshire.griefprevention.api.event.UserTrustClaimEvent;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.Invite;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

public class ClaimEventHandler {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  @Listener
  public void onClaimCreate(CreateClaimEvent event, @Root Player player,
      @Getter(value = "getClaim") Claim claim) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();
    World world = PLUGIN.getConfig().getWorldConfig().getWorld();

    if (!claim.getWorld().equals(world) || claim.isAdminClaim() || claim.getParent().isPresent()) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
      return;
    }

    event.setMessage(Text.of(TextColors.RED, "You cannot create a basic claim in this dimension!"));
    event.setCancelled(true);

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }

  @Listener
  public void onClaimDelete(DeleteClaimEvent event, @Root Player player) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();
    World world = PLUGIN.getConfig().getWorldConfig().getWorld();

    for (Claim claim : event.getClaims()) {
      if (claim.getWorld().equals(world) && !claim.isAdminClaim() && !claim.getParent()
          .isPresent()) {
        if (event instanceof DeleteClaimEvent.Abandon) {
          event.setMessage(Text.of(TextColors.RED, "You cannot abandon an island claim!"));
        } else {
          Island.get(claim).ifPresent((Island island) -> {
            event.setMessage(Text.of(TextColors.RED,
                "A claim you are attempting to delete belongs to an island.\n", Text
                    .of(TextColors.AQUA, "Do you want to delete ", island.getOwnerName(),
                        "'s island?").toBuilder()
                    .onHover(TextActions.showText(Text.of("Click here to delete.")))
                    .onClick(TextActions.executeCallback(src -> island.delete()))
            ));
          });
        }
        event.setCancelled(true);
      }
    }

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }

  @Listener
  public void onClaimChanged(ChangeClaimEvent event, @Root Player player,
      @Getter(value = "getClaim") Claim claim) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();
    World world = PLUGIN.getConfig().getWorldConfig().getWorld();

    if (!claim.getWorld().equals(world) || claim.isAdminClaim() || claim.getParent().isPresent()) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
      return;
    }

    event.setMessage(Text.of(
        TextColors.RED, "You cannot ",
        event instanceof ChangeClaimEvent.Resize ? "resize" : "change the claim type of",
        " an island claim!"
    ));
    event.setCancelled(true);

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }

  @Listener
  public void onClaimBorder(BorderClaimEvent event, @Root Player player,
      @Getter(value = "getClaim") Claim claim) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();
    World world = PLUGIN.getConfig().getWorldConfig().getWorld();

    if (claim.getWorld().equals(world) && !claim.isAdminClaim() && !claim.getParent().isPresent()) {
      if (!Island.get(claim).isPresent()) {
        SkyClaimsTimings.CLAIM_HANDLER.abort();
        return;
      }
      Island island = Island.get(claim).get();
      if (island.isLocked() && !island.isMember(player) && !player
          .hasPermission(Permissions.BYPASS_LOCK)) {
        event.setCancelled(true);
        event.setMessage(
            Text.of(TextColors.RED, "You do not have permission to enter ", island.getName(),
                TextColors.RED, "!"));
      }
    }

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }

  @Listener
  public void onClaimTrust(UserTrustClaimEvent event, @Root Player player,
      @Getter(value = "getClaim") Claim claim) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();

    if (PLUGIN.getConfig().getIntegrationConfig().getGriefPrevention().getDisabledTrustTypes()
        .contains(event.getTrustType())) {
      event.setCancelled(true);
      event.setMessage(Text.of(TextColors.RED, "The use of ", TextColors.GOLD, event.getTrustType(),
          TextColors.RED, " has been disabled!"));
      SkyClaimsTimings.CLAIM_HANDLER.abort();
      return;
    }

    if (player.hasPermission(Permissions.BYPASS_TRUST)) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    if (claim.getWorld().equals(world)) {
      // Get The top level claim
      if (claim.isSubdivision()) {
        Claim parent = claim;
        while (parent.getParent().isPresent()) {
          parent = parent.getParent().get();
        }
        claim = parent;
      }
      // Ignore non-basic claims or claims without an island.
      if (!claim.isBasicClaim() && !Island.get(claim).isPresent()) {
        SkyClaimsTimings.CLAIM_HANDLER.abort();
        return;
      }
      // Send out invites
      Island island = Island.get(claim).get();
      for (PrivilegeType type : PrivilegeType.values()) {
        if (type.getTrustType() == event.getTrustType()) {
          event.setCancelled(true);
          event.getUsers().forEach(uuid ->
              PLUGIN.getGame().getServiceManager().provideUnchecked(UserStorageService.class)
                  .get(uuid).ifPresent(user -> {
                if (event instanceof TrustClaimEvent.Add && island.getPrivilegeType(user) != type) {
                  Invite.builder()
                      .island(island)
                      .sender(player)
                      .receiver(user)
                      .privilegeType(type)
                      .build()
                      .send();
                  event.setMessage(Text.of(
                      TextColors.GREEN, "Island invite sent to ", type.format(user.getName()),
                      TextColors.GREEN, "."
                  ));
                }
                if (event instanceof TrustClaimEvent.Remove) {
                  event.setMessage(Text.of(
                      TextColors.RED, "Use ",
                      Text.builder("/is kick").color(TextColors.AQUA).style(TextStyles.ITALIC)
                          .onClick(TextActions.suggestCommand("/is kick ")),
                      " to remove a player from this island.")
                  );
                }
              }));
        }
      }
    }

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }
}
