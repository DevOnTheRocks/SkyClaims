package net.mohron.skyclaims.exception;

import org.spongepowered.api.text.Text;

public class InvalidRegionException extends SkyClaimsException {
	public InvalidRegionException(Text message) {
		super(message);
	}
}