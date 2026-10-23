package dev.auroraveil;

/** The effect levels you cycle through with the keybind. Each level includes the ones before it. */
public enum AuroraMode {
	OFF("Off"),
	TRAIL("Ribbon trail"),
	AURA("Trail + spiral aura"),
	FULL("Trail + aura + aurora sky");

	public final String label;

	AuroraMode(String label) {
		this.label = label;
	}

	public AuroraMode next() {
		AuroraMode[] all = values();
		return all[(ordinal() + 1) % all.length];
	}

	public boolean atLeast(AuroraMode other) {
		return ordinal() >= other.ordinal();
	}
}
