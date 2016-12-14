package net.mohron.skyclaims;

import com.google.common.collect.Maps;
import me.ryanhamshire.griefprevention.util.PlayerUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;

import java.util.Map;

public enum Options {
	// The default radius of an Island upon creation
	BASE_RADIUS("skyclaims.baseradius", "32");

	private String name;
	private String value;

	Options(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public int getInt(Options option, Subject subject) {
		return PlayerUtils.getOptionIntValue(subject, option.name, Integer.parseInt(option.value));
	}
}
