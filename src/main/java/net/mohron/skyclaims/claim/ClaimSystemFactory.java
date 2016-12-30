package net.mohron.skyclaims.claim;

/**
 * Created by cossacksman on 30/12/16.
 */
public class ClaimSystemFactory {
    private static IClaimSystem claimSystem;

    public static IClaimSystem getClaimSystem() {
        if (claimSystem != null)
            return claimSystem;

        try {
            Class.forName("me.ryanhamshire.griefprevention.claim.Claim");
            claimSystem = new GPClaimSystem();
        } catch (ClassNotFoundException e) {
            claimSystem = new BasicClaimSystem();
        }

        return claimSystem;
    }
}
