package net.mohron.skyclaims.world.region;

import net.mohron.skyclaims.exception.InvalidRegionException;
import net.mohron.skyclaims.world.Coordinate;

import java.util.ArrayList;

public interface IRegionPattern {
	ArrayList<Region> generateRegionPattern();

	public Region nextRegion() throws InvalidRegionException;

	public Coordinate lastLocation();
}
