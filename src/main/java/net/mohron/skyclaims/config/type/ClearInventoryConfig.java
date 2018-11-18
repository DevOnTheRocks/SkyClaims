package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ClearInventoryConfig {

  @Setting(value = "Create", comment = "Whether to clear on island creation.")
  private boolean create = false;
  @Setting(value = "Reset", comment = "Whether to clear on island reset.")
  private boolean reset = true;
  @Setting(value = "Delete", comment = "Whether to clear on island deletion.")
  private boolean delete = true;
  @Setting(value = "Leave", comment = "Whether to clear when leaving an island. Only applies to the member leaving.")
  private boolean leave = true;
  @Setting(value = "Kick", comment = "Whether to clear when kicked from an island. Only applies to the member being kicked.")
  private boolean kick = true;

  public boolean isCreate() {
    return create;
  }

  public boolean isReset() {
    return reset;
  }

  public boolean isDelete() {
    return delete;
  }

  public boolean isLeave() {
    return leave;
  }

  public boolean isKick() {
    return kick;
  }
}
