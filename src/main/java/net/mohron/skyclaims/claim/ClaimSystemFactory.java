package net.mohron.skyclaims.claim;

public class ClaimSystemFactory {
	private static IClaimSystem claimSystem;

	public static IClaimSystem getClaimSystem() {
		if (claimSystem != null)
			return claimSystem;

		try {
			Class.forName("me.ryanhamshire.griefprevention.GriefPrevention");
			claimSystem = new GPClaimSystem();
		} catch (ClassNotFoundException e) {
			claimSystem = new BasicClaimSystem();
		}

		return claimSystem;
	}
}
