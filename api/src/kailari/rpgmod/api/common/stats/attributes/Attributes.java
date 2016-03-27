package kailari.rpgmod.api.common.stats.attributes;

import kailari.rpgmod.api.common.stats.Stats;
import kailari.rpgmod.api.common.stats.attributes.link.StepStatLink;
import kailari.rpgmod.api.common.stats.attributes.xp.AttributeXPSources.*;

/**
 * Contains constants for all default attributes
 */
public class Attributes {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Main attributes
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * General purpose strength-attribute.
	 * <p/>
	 * Grants:<br/>
	 * 	<code>HP_MAX</code> -> starting from level 5, gain 1.0 every 5 levels<br/>
	 * 	<code>DAMAGE_MULT_MELEE</code> -> starting from level 1, gain 0.005 every level<br/>
	 * 	<code>EXHAUSTION_MAX</code> -> starting from level 1, gain 0.05 every level<br/>
	 */
	public static final Attribute STRENGTH = new Attribute("strength", true)
			.addXPSource(Mining.DIGGING, 10)
			.addXPSource(Mining.MINING, 40)
			.addXPSource(Mining.SHOVELING, 20)
			.addXPSource(Mining.WOODCUTTING, 30)
			.addXPSource(Damage.DEALING_MELEE, 1)
			.addXPSource(Maneuvers.JUMPING, 1)
			.addXPSource(Maneuvers.SPRINTING, 1)
			.addXPSource(Maneuvers.JUMPING_WHILE_SPRINTING, 2)
			.addXPSource(Damage.BLOCKING, 10)
			.addXPSource(Farming.TILLING, 10)
			.addXPSource(Forging.FORGING, 10)
			.addXPSource(Farming.TILLING, 10)
			.linkStat(Stats.HP_MAX, new StepStatLink(5, 5, 1.0f))
			.linkStat(Stats.DAMAGE_MULT_MELEE, new StepStatLink(1, 1, 0.005f))
			.linkStat(Stats.EXHAUSTION_MAX, new StepStatLink(1, 1, 0.05f))
			.linkStat(Stats.MINING_SPEED_BONUS, new StepStatLink(1, 1, 0.01f))
			.linkStat(Stats.MINING_SPEED_BONUS, new StepStatLink(1, 1, 0.01f))
			;

	public static final Attribute AGILITY = new Attribute("agility", true);
	public static final Attribute VITALITY = new Attribute("vitality", true);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Minor attributes
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Attribute LUCK = new Attribute("luck", false)
			.addXPSource(Damage.DODGING, 10);

	public static final Attribute MISFORTUNE = new Attribute("misfortune", false);

	public static final Attribute KARMA_GOOD = new Attribute("karma_good", false)
			.addXPSource(Killing.MONSTERS, 10);

	public static final Attribute KARMA_EVIL = new Attribute("karma_evil", false)
			.addXPSource(Killing.PLAYERS, 10)
			.addXPSource(Killing.VILLAGERS, 5);

}
