package kailari.rpgmod.api.common.stats.attributes;

/**
 * Contains constants for all default attributes
 */
public class Attributes {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Main attributes
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Attribute STRENGTH = new Attribute("strength")
			/*.addXPSource(Mining.DIGGING, 10)
			.addXPSource(Mining.MINING, 40)
			.addXPSource(Mining.SHOVELING, 20)
			.addXPSource(Mining.WOODCUTTING, 30)
			.addXPSource(Damage.DEALING_MELEE, 1)
			.addXPSource(Maneuvers.JUMPING, 1)
			.addXPSource(Maneuvers.SPRINTING, 1)
			.addXPSource(Maneuvers.JUMPING_WHILE_SPRINTING, 2)
			.addXPSource(Damage.BLOCKING, 10)
			.addXPSource(Farming.TILLING, 10)
			.addXPSource(Forging.FORGING, 10)*/;

	public static final Attribute AGILITY = new Attribute("agility");
	public static final Attribute VITALITY = new Attribute("vitality");


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Minor attributes
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Attribute LUCK = new Attribute("luck");
	public static final Attribute MISFORTUNE = new Attribute("misfortune");

	public static final Attribute KARMA_GOOD = new Attribute("karma_good");
	public static final Attribute KARMA_EVIL = new Attribute("karma_evil");

}
