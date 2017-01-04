package net.mohron.skyclaims.lib;

public enum Options {
	// The default radius of an Island upon creation
	BASE_RADIUS("skyclaims.baseradius", "32");

	private String name;
	private String value;

	Options(String name, String value) {
		this.name = name;
		this.value = value;
	}
}