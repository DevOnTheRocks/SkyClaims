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

package net.mohron.skyclaims.config.type;

import net.mohron.skyclaims.config.ConfigManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class GlobalConfig {

  @Setting(value = "Config-Version")
  private int version;
  @Setting(value = "Command")
  private CommandConfig commandConfig = new CommandConfig();
  @Setting(value = "Economy")
  private EconomyConfig economyConfig = new EconomyConfig();
  @Setting(value = "Entity")
  private EntityConfig entityConfig = new EntityConfig();
  @Setting(value = "Integration")
  private IntegrationConfig integrationConfig = new IntegrationConfig();
  @Setting(value = "Island-Expiration")
  private ExpirationConfig expirationConfig = new ExpirationConfig();
  @Setting(value = "Misc")
  private MiscConfig miscConfig = new MiscConfig();
  @Setting(value = "Permission")
  private PermissionConfig permissionConfig = new PermissionConfig();
  @Setting(value = "Storage")
  private StorageConfig storageConfig = new StorageConfig();
  @Setting(value = "World")
  private WorldConfig worldConfig = new WorldConfig();

  public GlobalConfig() {
    version = ConfigManager.CONFIG_VERSION;
  }

  public int getVersion() {
    return version;
  }

  public CommandConfig getCommandConfig() {
    return commandConfig;
  }

  public EconomyConfig getEconomyConfig() {
    return economyConfig;
  }

  public EntityConfig getEntityConfig() {
    return entityConfig;
  }

  public IntegrationConfig getIntegrationConfig() {
    return integrationConfig;
  }

  public ExpirationConfig getExpirationConfig() {
    return expirationConfig;
  }

  public MiscConfig getMiscConfig() {
    return miscConfig;
  }

  public PermissionConfig getPermissionConfig() {
    return permissionConfig;
  }

  public StorageConfig getStorageConfig() {
    return storageConfig;
  }

  public WorldConfig getWorldConfig() {
    return worldConfig;
  }
}