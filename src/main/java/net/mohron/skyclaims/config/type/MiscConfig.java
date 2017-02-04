package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class MiscConfig {
	@Setting(value = "Island-on-Join", comment = "Automatically create an island for a player on join.")
	private boolean islandOnJoin;
	@Setting(value = "Create-Commands", comment = "Commands to run on island creation and reset.")
	private List<String> createCommands;
	@Setting(value = "Reset-Commands", comment = "Commands to run on island resets only.")
	private List<String> resetCommands;

	public MiscConfig() {
		islandOnJoin = false;
		createCommands = new ArrayList<>();
		resetCommands = new ArrayList<>();
	}

	public boolean createIslandOnJoin() {
		return islandOnJoin;
	}

	public List<String> getCreateCommands() {
		return createCommands;
	}

	public List<String> getResetCommands() {
		return resetCommands;
	}
}