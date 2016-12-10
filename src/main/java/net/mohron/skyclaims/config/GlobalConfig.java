package net.mohron.skyclaims.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;

@ConfigSerializable
public class GlobalConfig {
	public GlobalConfig() {
		skyClaimsDimension = Sponge.getGame().getServer().getDefaultWorldName();
	}

	@Setting
	public String skyClaimsDimension;
}
