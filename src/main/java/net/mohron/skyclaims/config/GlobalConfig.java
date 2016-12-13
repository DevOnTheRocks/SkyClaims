package net.mohron.skyclaims.config;

import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.Sponge;

public abstract class GlobalConfig {
	public GlobalConfig() {
		skyClaimsDimension = Sponge.getGame().getServer().getDefaultWorldName();
		database = new DatabaseConfig();
	}

	@Setting
	public String skyClaimsDimension;
	@Setting
	public DatabaseConfig database;
}