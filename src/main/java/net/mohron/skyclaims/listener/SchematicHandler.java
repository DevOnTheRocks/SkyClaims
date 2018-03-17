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

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.mohron.skyclaims.SkyClaimsTimings;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SchematicHandler {

  private static final Map<UUID, PlayerData> PLAYER_DATA = Maps.newHashMap();

  @Listener
  public void onInteract(InteractBlockEvent.Secondary.MainHand event, @Root Player player) {
    SkyClaimsTimings.SCHEMATIC_HANDLER.startTimingIfSync();

    if (!player.hasPermission(Permissions.COMMAND_SCHEMATIC_CREATE)) {
      SkyClaimsTimings.SCHEMATIC_HANDLER.abort();
      return;
    }

    Optional<ItemStack> item = player.getItemInHand(HandTypes.MAIN_HAND);
    if (item.isPresent() && item.get().getItem().equals(ItemTypes.GOLDEN_AXE)
        && event.getTargetBlock() != BlockSnapshot.NONE) {
      get(player).setPos2(event.getTargetBlock().getPosition());
      player.sendMessage(Text.of(TextColors.LIGHT_PURPLE,
          "Position 2 set to " + event.getTargetBlock().getPosition()));
      event.setCancelled(true);
    }

    SkyClaimsTimings.SCHEMATIC_HANDLER.stopTimingIfSync();
  }

  @Listener
  public void onInteract(InteractBlockEvent.Primary.MainHand event, @Root Player player) {
    SkyClaimsTimings.SCHEMATIC_HANDLER.startTimingIfSync();

    if (!player.hasPermission(Permissions.COMMAND_SCHEMATIC_CREATE)) {
      SkyClaimsTimings.SCHEMATIC_HANDLER.abort();
      return;
    }

    Optional<ItemStack> item = player.getItemInHand(HandTypes.MAIN_HAND);
    if (item.isPresent() && item.get().getItem().equals(ItemTypes.GOLDEN_AXE)) {
      get(player).setPos1(event.getTargetBlock().getPosition());
      player.sendMessage(Text.of(TextColors.LIGHT_PURPLE,
          "Position 1 set to " + event.getTargetBlock().getPosition()));
      event.setCancelled(true);
    }

    SkyClaimsTimings.SCHEMATIC_HANDLER.stopTimingIfSync();
  }

  public static PlayerData get(Player pl) {
    return PLAYER_DATA.computeIfAbsent(pl.getUniqueId(), k -> new PlayerData(pl.getUniqueId()));
  }

  public static class PlayerData {

    private final UUID uid;
    private Vector3i pos1;
    private Vector3i pos2;

    public PlayerData(UUID uid) {
      this.uid = uid;
    }

    public UUID getUid() {
      return this.uid;
    }

    public Vector3i getPos1() {
      return this.pos1;
    }

    public void setPos1(Vector3i pos) {
      this.pos1 = pos;
    }

    public Vector3i getPos2() {
      return this.pos2;
    }

    public void setPos2(Vector3i pos) {
      this.pos2 = pos;
    }
  }
}