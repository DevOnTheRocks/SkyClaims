package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class MiscConfig {
	@Setting(value = "Create-Commands", comment = "Commands to run on island creation and reset")
	public List<String> createCommands;
	@Setting(value = "Reset-Commands", comment = "Commands to run on island resets only")
	public List<String> resetCommands;

	MiscConfig() {
		createCommands = new ArrayList<>();
		resetCommands = new ArrayList<>();
	}
}