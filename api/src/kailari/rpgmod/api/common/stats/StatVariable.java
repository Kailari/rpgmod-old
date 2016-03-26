package kailari.rpgmod.api.common.stats;

/**
 * POJO-class for containing minimum required stat information
 */
public final class StatVariable {
	private final String nbtKey;
	private final float defaultValue;

	public StatVariable(String nbtKey, float defaultValue) {
		this.nbtKey = nbtKey;
		this.defaultValue = defaultValue;

		StatRegistry.register(this);
	}

	public String getNBTKey() {
		return this.nbtKey;
	}

	public float getDefaultValue() {
		return this.defaultValue;
	}
}
