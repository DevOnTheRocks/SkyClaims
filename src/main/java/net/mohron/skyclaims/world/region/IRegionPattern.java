package net.mohron.skyclaims.world.region;

import net.mohron.skyclaims.exception.InvalidRegionException;
import net.mohron.skyclaims.world.Coordinate;

import java.util.ArrayList;

public interface IRegionPattern {

	public abstract ArrayList<Region> generateRegionPattern();

	public abstract Region nextRegion() throws InvalidRegionException;

	public abstract Coordinate lastLocation();
}
