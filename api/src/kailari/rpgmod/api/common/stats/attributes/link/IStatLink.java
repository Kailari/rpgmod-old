package kailari.rpgmod.api.common.stats.attributes.link;

/**
 * Describes how attribute level-ups affect stats
 */
public interface IStatLink {

	/**
	 * The type of operation. 0 -> additive, 1 -> multiplicative (base), 2 -> multiplicative (cumulative)
	 * Used to determine type of operation to apply on created internal vanilla modifier.
	 */
	int getOperation();

	/**
	 * Returns true if modifier should be applied at given level.
	 */
	boolean changesAtLevel(int level);

	/**
	 * Returns the amount how much the stat should change at given level.
	 */
	float getModifier(int level);
}
