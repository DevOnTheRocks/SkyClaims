package net.mohron.skyclaims.claim;

import me.ryanhamshire.griefprevention.claim.Claim;

/**
 * Created by cossacksman on 31/12/16.
 */
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
