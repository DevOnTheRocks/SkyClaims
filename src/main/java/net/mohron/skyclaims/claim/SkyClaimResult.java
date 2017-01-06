package net.mohron.skyclaims.claim;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimResultType;

import java.util.Optional;

public class SkyClaimResult implements ClaimResult {
	private ClaimResultType resultType;
	private Optional<Claim> claim;

	public SkyClaimResult(ClaimResultType resultType, Optional<Claim> claim) {
		this.resultType = resultType;
		this.claim = claim;
	}

	public ClaimResultType getResultType() {
		return resultType;
	}

	public Optional<Claim> getClaim() {
		return claim;
	}
}
