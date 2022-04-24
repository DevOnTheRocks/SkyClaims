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

import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.event.BorderClaimEvent;
import com.griefdefender.api.event.ChangeClaimEvent;
import com.griefdefender.api.event.ClaimEvent;
import com.griefdefender.api.event.CreateClaimEvent;
import com.griefdefender.api.event.RemoveClaimEvent;
import com.griefdefender.api.event.TrustClaimEvent;
import com.griefdefender.api.event.UserTrustClaimEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.spongeapi.SpongeComponentSerializer;
import net.kyori.event.EventSubscriber;
import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.MiscConfig.ClearItemsType;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.Invite;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

public class ClaimEventHandler implements EventSubscriber<ClaimEvent> {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();
  private static final Text PREFIX = Text.of(TextColors.WHITE, "[", TextColors.AQUA, PluginInfo.NAME, TextColors.WHITE, "] ");

  @Override
  public void on(@NonNull ClaimEvent event) throws Throwable {
    if (event instanceof CreateClaimEvent) {
      onClaimCreate((CreateClaimEvent) event);
    } else if (event instanceof RemoveClaimEvent) {
      onClaimDelete((RemoveClaimEvent) event);
    } else if (event instanceof ChangeClaimEvent) {
      if (event instanceof ChangeClaimEvent.Resize) {
        onClaimResized((ChangeClaimEvent.Resize) event);
      } else {
        onClaimChanged((ChangeClaimEvent) event);
      }
    } else if (event instanceof BorderClaimEvent) {
      onClaimBorder((BorderClaimEvent) event);
    } else if (event instanceof UserTrustClaimEvent) {
      onClaimTrust((UserTrustClaimEvent) event);
    }
  }

  public void onClaimCreate(CreateClaimEvent event) {
    if (!event.getCause().containsType(Player.class)) {
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    Claim claim = event.getClaim();

    if (!claim.getWorldUniqueId().equals(world.getUniqueId()) || isIslandDefender(event) || claim.isAdminClaim() || claim.getParent() != null) {
      return;
    }

    event.setMessage(toComponent(Text.of(PREFIX, TextColors.RED, "You cannot create a player claim in this dimension!")));
    event.cancelled(true);
  }

  public void onClaimDelete(RemoveClaimEvent event) {
    if (!event.getCause().containsType(Player.class)) {
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();

    if (isIslandDefender(event)) {
      return;
    }

    for (Claim claim : event.getClaims()) {
      if (claim.getWorldUniqueId().equals(world.getUniqueId()) && IslandManager.getByClaim(claim).isPresent()) {
        if (event instanceof RemoveClaimEvent.Abandon) {
          event.setMessage(toComponent(Text.of(TextColors.RED, "You cannot abandon an island claim!")));
        } else {
          IslandManager.getByClaim(claim).ifPresent((Island island) -> event.setMessage(toComponent(Text.of(
              PREFIX, TextColors.RED, "A claim you are attempting to delete belongs to an island.", Text.NEW_LINE,
              Text.of(TextColors.AQUA, "Do you want to delete ", island.getOwnerName(), "'s island?").toBuilder()
                  .onHover(TextActions.showText(Text.of("Click here to delete.")))
                  .onClick(TextActions.executeCallback(src -> island.delete()))
          ))));
        }
        event.cancelled(true);
      }
    }
  }

  public void onClaimChanged(ChangeClaimEvent event) {
    if (!event.getCause().containsType(Player.class)) {
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    Claim claim = event.getClaim();

    if (!claim.getWorldUniqueId().equals(world.getUniqueId()) || isIslandDefender(event) || !IslandManager.getByClaim(claim).isPresent()) {
      return;
    }

    event.setMessage(toComponent(Text.of(
        PREFIX, TextColors.RED, "You cannot ",
        event instanceof ChangeClaimEvent.Resize ? "resize" : "change the claim type of",
        " an island claim!"
    )));
    event.cancelled(true);
  }

  public void onClaimResized(ChangeClaimEvent.Resize event) {    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    Claim claim = event.getClaim();

    if (!claim.getWorldUniqueId().equals(world.getUniqueId()) || !IslandManager.getByClaim(claim).isPresent()) {
      return;
    }

    if (claim.getWidth() >= 512) {
      event.setMessage(toComponent(Text.of(
          PREFIX, TextColors.RED, "An island claim can not be expanded beyond ",
          TextColors.LIGHT_PURPLE, 512, TextColors.RED, " x ", TextColors.LIGHT_PURPLE, 512, TextColors.RED, "!"
      )));
      event.cancelled(true);
    }
  }

  public void onClaimBorder(BorderClaimEvent event) {
    Player player = event.getCause().first(Player.class).orElse(null);
    if (player == null) {
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    Claim claim = event.getClaim();

    if (!claim.getWorldUniqueId().equals(world.getUniqueId()) || !IslandManager.getByClaim(claim).isPresent()) {
      return;
    }

    Island island = IslandManager.getByClaim(claim).get();
    if (island.isLocked() && !island.isMember(player) && !player.hasPermission(Permissions.BYPASS_LOCK)) {
      event.cancelled(true);
      event.setMessage(toComponent(Text.of(PREFIX, TextColors.RED, "You do not have permission to enter ", island.getName(), TextColors.RED, "!")));
    }

    // Clears configured items when entering/leaving an island
    if (PLUGIN.getConfig().getMiscConfig().getClearItemsType() == ClearItemsType.BLACKLIST) {
      // If set to blacklist, remove all matching items
      for (ItemType item : PLUGIN.getConfig().getMiscConfig().getClearItems()) {
        ItemStack stack = ItemStack.builder().itemType(item).build();
        player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(stack)).poll();
      }
    } else {
      // If set to whitelist, remove all non-matching items
      for (Inventory slot : player.getInventory().slots()) {
        if (slot.peek().isPresent() && !PLUGIN.getConfig().getMiscConfig().getClearItems().contains(slot.peek().get().getType())) {
          slot.poll();
        }
      }
    }
  }

  public void onClaimTrust(UserTrustClaimEvent event) {
    Player player = event.getCause().first(Player.class).orElse(null);
    if (player == null || isIslandDefender(event) || player.hasPermission(Permissions.BYPASS_TRUST)) {
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    Claim claim = event.getClaim();

    if (!claim.getWorldUniqueId().equals(world.getUniqueId())) {
      return;
    }

    // Get The top level claim
    if (claim.isSubdivision()) {
      Claim parent = claim;
      while (parent.getParent() != null) {
        parent = parent.getParent();
      }
      claim = parent;
    }
    // Ignore claims without an island.
    if (!IslandManager.getByClaim(claim).isPresent()) {
      return;
    }
    // Send out invites
    Island island = IslandManager.getByClaim(claim).get();
    for (PrivilegeType type : PrivilegeType.values()) {
      if (type.getTrustType() == event.getTrustType()) {
        event.cancelled(true);
        event.getUsers().forEach(uuid ->
            PLUGIN.getGame().getServiceManager().provideUnchecked(UserStorageService.class).get(uuid).ifPresent(user -> {
              if (event instanceof TrustClaimEvent.Add && island.getPrivilegeType(user) != type) {
                Invite.builder()
                    .island(island)
                    .sender(player)
                    .receiver(user)
                    .privilegeType(type)
                    .build()
                    .send();
                event.setMessage(toComponent(
                    Text.of(PREFIX, TextColors.GREEN, "Island invite sent to ", type.format(user.getName()), TextColors.GREEN, ".")
                ));
              }
              if (event instanceof TrustClaimEvent.Remove) {
                event.setMessage(toComponent(Text.of(
                    PREFIX, TextColors.RED, "Use ",
                    Text.builder("/is kick").color(TextColors.AQUA).style(TextStyles.ITALIC).onClick(TextActions.suggestCommand("/is kick ")),
                    " to remove a player from this island."
                )));
              }
            }));
      }
    }
  }

  private Component toComponent(Text text) {
    return SpongeComponentSerializer.get().deserialize(text);
  }

  private boolean isIslandDefender(com.griefdefender.api.event.Event event) {
    return event.getCause().contains(PLUGIN.getPluginContainer());
  }
}
