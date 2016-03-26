package kailari.rpgmod.api.common.stats.attributes.link;

/**
 * Describes how attribute level-ups affect stats
 */
public interface IStatLink {
	/**
	 * Returns true if modifier should be applied at given level.
	 */
	boolean changesAtLevel(int level);

	/**
	 * Returns the amount how much the stat should change at given level.
	 */
	float getModifier(int level);
}
