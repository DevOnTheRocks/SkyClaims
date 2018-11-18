package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class InventoryConfig {

  @Setting(value = "All-Members", comment = "Whether all members or just the island owner's inventory should be effected.")
  private boolean allMembers = false;
  @Setting(value = "Player-Inventory", comment = "Clear inventory options for the player's inventory.")
  private ClearInventoryConfig playerInventory = new ClearInventoryConfig();
  @Setting(value = "Enderchest", comment = "Clear inventory options for a player's enderchest inventory.")
  private ClearInventoryConfig enderchest = new ClearInventoryConfig();

  public boolean isAllMembers() {
    return allMembers;
  }

  public ClearInventoryConfig getPlayerInventory() {
    return playerInventory;
  }

  public ClearInventoryConfig getEnderchest() {
    return enderchest;
  }
}
