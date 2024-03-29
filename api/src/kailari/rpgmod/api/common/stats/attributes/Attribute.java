package kailari.rpgmod.api.common.stats.attributes;

import kailari.rpgmod.api.common.stats.StatVariable;
import kailari.rpgmod.api.common.stats.attributes.link.IStatLink;
import kailari.rpgmod.api.common.stats.attributes.xp.AttributeXPSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Describes a basic attribute
 */
public class Attribute {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Levelling
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public int getXPRequiredForLevel(int level) {
		// TODO: XP ramp -class for determining these
		return 10 + Math.round(100.0f * (float) Math.pow(1.51f, level));
	}

	public int getLevel(int xp) {
		int level = 1;

		// Calculate required XP for first level
		int xpRequiredForNextLevel = getXPRequiredForLevel(level);

		// Find the level
		while (xp >= xpRequiredForNextLevel) {
			level++;
			xpRequiredForNextLevel = getXPRequiredForLevel(level);
		}

		return level;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Linking stats
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Links this attribute to a stat variable.
	 *
	 * @param var  Stat variable to link to
	 * @param link Behavior for level-ups
	 * @return self for method chaining
	 */
	public final Attribute linkStat(StatVariable var, IStatLink link) {
		this.linkedStats.put(var, link);
		return this;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// XP Sources
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Binds attribute to an xp source. forceDefaultAmount is set to true and multiplier is set to 1.0f.
	 * This means that amount of xp gained is always exactly the value specified in 'amount'
	 *
	 * @param source XP source to bind to
	 * @param amount Amount of xp to gain
	 */
	public final Attribute addXPSource(AttributeXPSource source, int amount) {
		return addXPSource(source, amount, true);
	}

	/**
	 * Binds attribute to an xp source. Multiplier is set to 1.0f
	 *
	 * @param source             XP source to bind to
	 * @param defaultAmount      Default amount of xp to gain (this value is used if event specifies no other value)
	 * @param forceDefaultAmount If true, default amount is used even if event would specify its own amount-
	 */
	public final Attribute addXPSource(AttributeXPSource source, int defaultAmount, boolean forceDefaultAmount) {
		source.linkAttribute(this, defaultAmount, 1.0f, forceDefaultAmount);
		return this;
	}

	/**
	 * Binds attribute to an xp source
	 *
	 * @param source        XP source to bind to
	 * @param defaultAmount Default amount of xp to gain (this value is used if event specifies no other value)
	 * @param multiplier    If event specifies a xp amount, it is multiplied with this first.
	 */
	public final Attribute addXPSource(AttributeXPSource source, int defaultAmount, float multiplier) {
		source.linkAttribute(this, defaultAmount, multiplier, false);
		return this;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Getters
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public final String getDisplayName() {
		// TODO: Make this read .lang file
		return this.unlocalizedName;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Getters for mandatory boring stuff
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Required for identification and NBT I/O
	 */
	public final String getUnlocalizedName() {
		return unlocalizedName;
	}

	public final UUID getUUID() {
		return this.uuid;
	}

	public final boolean isMajor() {
		return this.isMajor;
	}

	/**
	 * Returns list of links to stats
	 */
	public final Map<StatVariable, IStatLink> getLinkedStats() {
		return this.linkedStats;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Mandatory boring stuff
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final Map<StatVariable, IStatLink> linkedStats;
	private final String unlocalizedName;
	private final UUID uuid;

	private final boolean isMajor;

	public Attribute(String unlocalizedName, String uuid, boolean isMajor) {
		this.unlocalizedName = unlocalizedName;
		this.isMajor = isMajor;
		this.uuid = UUID.fromString(uuid);

		this.linkedStats = new HashMap<StatVariable, IStatLink>();

		AttributeRegistry.register(this);
	}
}
