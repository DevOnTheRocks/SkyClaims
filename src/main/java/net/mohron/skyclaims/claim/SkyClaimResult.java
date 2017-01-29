package net.mohron.skyclaims.claim;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimResultType;
import org.spongepowered.api.text.Text;

import java.util.List;
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

	@Override
	public Optional<Text> getMessage() {
		return null;
	}

	@Override
	public List<Claim> getClaims() {
		return null;
	}

	public Optional<Claim> getClaim() {
		return claim;
	}
}
