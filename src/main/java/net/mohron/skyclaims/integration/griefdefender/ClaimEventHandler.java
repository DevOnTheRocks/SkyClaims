/*
 * Required Notice: Copyright (C) 2019 Mohron (https://www.mohron.dev)
 *
 * IslandDefender is licensed under the PolyForm Noncommercial License 1.0.0 (https://polyformproject.org/licenses/noncommercial/1.0.0).
 */

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

package net.mohron.skyclaims.integration.griefdefender;

import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.event.BorderClaimEvent;
import com.griefdefender.api.event.ChangeClaimEvent;
import com.griefdefender.api.event.CreateClaimEvent;
import com.griefdefender.api.event.RemoveClaimEvent;
import com.griefdefender.api.event.TrustClaimEvent;
import com.griefdefender.api.event.UserTrustClaimEvent;
import net.kyori.event.method.annotation.Subscribe;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.SkyClaimsTimings;
import net.mohron.skyclaims.config.type.MiscConfig.ClearItemsType;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.Invite;
import net.mohron.skyclaims.team.PrivilegeType;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandManager;
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
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

public class ClaimEventHandler {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();
  private static final Text PREFIX = Text.of(TextColors.WHITE, "[", TextColors.AQUA, PluginInfo.NAME, TextColors.WHITE, "] ");

  @Subscribe
  public void onClaimCreate(CreateClaimEvent event) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();

    if (!event.getCause().containsType(Player.class)) {
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    Claim claim = event.getClaim();

    if (!claim.getWorldUniqueId().equals(world.getUniqueId()) || isIslandDefender(event) || claim.isAdminClaim() || claim.getParent().isPresent()) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
      return;
    }

    event.setMessage(toComponent(Text.of(PREFIX, TextColors.RED, "You cannot create a player claim in this dimension!")));
    event.cancelled(true);

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }

  @Subscribe
  public void onClaimDelete(RemoveClaimEvent event) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();

    if (!event.getCause().containsType(Player.class)) {
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();

    if (isIslandDefender(event)) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
      return;
    }

    for (Claim claim : event.getClaims()) {
      if (claim.getWorldUniqueId().equals(world.getUniqueId()) && IslandManager.getByClaim(claim).isPresent()) {
        if (event instanceof RemoveClaimEvent.Abandon) {
          event.setMessage(TextComponent.of("You cannot abandon an island claim!", TextColor.RED));
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

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }

  @Subscribe
  public void onClaimChanged(ChangeClaimEvent event) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();

    if (!event.getCause().containsType(Player.class)) {
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    Claim claim = event.getClaim();

    if (!claim.getWorldUniqueId().equals(world.getUniqueId()) || isIslandDefender(event) || !IslandManager.getByClaim(claim).isPresent()) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
      return;
    }

    event.setMessage(toComponent(Text.of(
        PREFIX, TextColors.RED, "You cannot ",
        event instanceof ChangeClaimEvent.Resize ? "resize" : "change the claim type of",
        " an island claim!"
    )));
    event.cancelled(true);

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }

  @Subscribe
  public void onClaimResized(ChangeClaimEvent.Resize event) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();
    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    Claim claim = event.getClaim();

    if (!claim.getWorldUniqueId().equals(world.getUniqueId()) || !IslandManager.getByClaim(claim).isPresent()) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
      return;
    }

    if (claim.getWidth() >= 512) {
      event.setMessage(toComponent(Text.of(
          PREFIX, TextColors.RED, "An island claim can not be expanded beyond ",
          TextColors.LIGHT_PURPLE, 512, TextColors.RED, " x ", TextColors.LIGHT_PURPLE, 512, TextColors.RED, "!"
      )));
      event.cancelled(true);
    }

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }

  @Subscribe
  public void onClaimBorder(BorderClaimEvent event) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();

    Player player = event.getCause().first(Player.class).orElse(null);
    if (player == null) {
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    Claim claim = event.getClaim();

    if (!claim.getWorldUniqueId().equals(world.getUniqueId()) || !IslandManager.getByClaim(claim).isPresent()) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
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

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }

  @Subscribe
  public void onClaimTrust(UserTrustClaimEvent event) {
    SkyClaimsTimings.CLAIM_HANDLER.startTimingIfSync();

    Player player = event.getCause().first(Player.class).orElse(null);
    if (player == null || isIslandDefender(event) || player.hasPermission(Permissions.BYPASS_TRUST)) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
      return;
    }

    World world = PLUGIN.getConfig().getWorldConfig().getWorld();
    Claim claim = event.getClaim();

    if (!claim.getWorldUniqueId().equals(world.getUniqueId())) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
      return;
    }

    // Get The top level claim
    if (claim.isSubdivision()) {
      Claim parent = claim;
      while (parent.getParent().isPresent()) {
        parent = parent.getParent().get();
      }
      claim = parent;
    }
    // Ignore claims without an island.
    if (!IslandManager.getByClaim(claim).isPresent()) {
      SkyClaimsTimings.CLAIM_HANDLER.abort();
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

    SkyClaimsTimings.CLAIM_HANDLER.stopTimingIfSync();
  }

  private Component toComponent(Text text) {
    return GsonComponentSerializer.INSTANCE.deserialize(TextSerializers.JSON.serialize(text));
  }

  private boolean isIslandDefender(com.griefdefender.api.event.Event event) {
    return event.getCause().contains(PLUGIN.getPluginContainer());
  }
}
