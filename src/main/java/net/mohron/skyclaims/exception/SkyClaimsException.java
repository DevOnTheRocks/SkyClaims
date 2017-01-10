package net.mohron.skyclaims.exception;

import org.spongepowered.api.text.Text;

public class SkyClaimsException extends Exception {
	private Text message;

	public SkyClaimsException(Text message) {
		super(message.toPlain());
		this.message = message;
	}

	public Text getText() {
		return message;
	}
}