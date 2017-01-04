package net.mohron.skyclaims.world.region;

import java.util.ArrayList;

public interface IRegionPattern {
	ArrayList<Region> generateRegionPattern();

	public Region nextRegion();
}
