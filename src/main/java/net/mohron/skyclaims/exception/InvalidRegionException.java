package net.mohron.skyclaims.exception;

import org.spongepowered.api.text.Text;

public class InvalidRegionException extends Exception {
	private Text message;

	public InvalidRegionException(Text message) {
		super(message.toPlain());
		this.message = message;
	}

	public Text getText() {
		return message;
	}
}