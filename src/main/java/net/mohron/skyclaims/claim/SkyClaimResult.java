package net.mohron.skyclaims.claim;

public class SkyClaimResult implements IClaimResult {
	public boolean succeeded;
	public IClaim claim;

	public boolean getStatus() {
		return succeeded;
	}

	public IClaim getClaim() {
		return claim;
	}
}
