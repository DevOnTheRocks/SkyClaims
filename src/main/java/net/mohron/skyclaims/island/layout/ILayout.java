package net.mohron.skyclaims.island.layout;

import net.mohron.skyclaims.Region;

import java.util.ArrayList;

public interface ILayout {
	ArrayList<Region> generateRegionPattern();

	public Region nextRegion();
}
