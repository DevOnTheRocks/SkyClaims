package net.mohron.skyclaims.lib;

public enum Options {
	// The default radius of an Island upon creation
	DEFAULT_SCHEMATIC("skyclaims.defaultschematic", "island");

	private String key;
	private String defaultValue;

	Options(String key, String defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public String getKey(){
		return key;
	}

	public String getDefault() {
		return defaultValue;
	}
}