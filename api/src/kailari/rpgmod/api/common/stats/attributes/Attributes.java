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

	public static final Attribute STRENGTH = new Attribute("strength", "768cf7ac-9600-49b6-9ca4-d6d9d5cc04f1", true)
			.addXPSource(Mining.DIGGING, 10)
			.addXPSource(Mining.MINING, 40)
			.addXPSource(Mining.SHOVELING, 5)
			.addXPSource(Mining.WOODCUTTING, 30)
			.addXPSource(Damage.DEALING_MELEE, 5)
			.addXPSource(Damage.BLOCKING, 10)
			.addXPSource(Farming.TILLING, 10)
			.addXPSource(Forging.FORGING, 10)
			.addXPSource(Farming.TILLING, 10)
			.linkStat(Stats.MAX_HEALTH, new StepStatLink(5, 5, 0.1f))
			.linkStat(Stats.ATTACK_DAMAGE, new StepStatLink(1, 1, 0.005f))
			.linkStat(Stats.EXHAUSTION_MAX, new StepStatLink(1, 1, 0.05f))
			.linkStat(Stats.MINING_SPEED_MULT, new StepStatLink(1, 1, 0.01f));

	public static final Attribute AGILITY = new Attribute("agility", "a2ae6e12-afe9-41e2-8572-0c3b1c37cdc0", true)
			.addXPSource(Maneuvers.WALKING, 10)
			.addXPSource(Maneuvers.SPRINTING, 20)
			.addXPSource(Maneuvers.JUMPING, 2)
			.addXPSource(Maneuvers.JUMPING_WHILE_SPRINTING, 3)
			.linkStat(Stats.MOVEMENT_SPEED, new StepStatLink(1, 1, 0.0005f))
			.linkStat(Stats.SPRINT_SPEED_MULT, new StepStatLink(1, 1, 0.005f));
			// TODO: Add more of these
			// TODO: Stop speed increase fucking up player FOV (probably happens due to base value - modifier difference, renderer thinks we are sprinting all the time)


	public static final Attribute VITALITY = new Attribute("vitality", "7a793daa-79b7-4b2a-beea-cdb95f23b877", true);

	public static final Attribute ENDURANCE = new Attribute("endurance", "c1a7dd57-6fef-4def-9235-96b8ff544c27", true)
			.linkStat(Stats.MAX_HEALTH, new StepStatLink(5, 5, 2.0f));


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Minor attributes
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Attribute LUCK = new Attribute("luck", "a142496d-7975-4359-9849-6e959543992b", false)
			.addXPSource(Damage.DODGING, 1)
			.linkStat(Stats.LUCK, new StepStatLink(1, 1, 0.1f));

	public static final Attribute MISFORTUNE = new Attribute("misfortune", "19b0076e-7869-487a-b4b7-95ee5b3a937f", false)
			.addXPSource(Damage.MISSING, 10);

	public static final Attribute KARMA_GOOD = new Attribute("karma_good", "12e07e09-a894-4d1b-9667-d80a8e8757c7", false)
			.addXPSource(Killing.MONSTERS, 10);

	public static final Attribute KARMA_EVIL = new Attribute("karma_evil", "3e88ebc6-4680-4347-8bcb-a0fa14407af8", false)
			.addXPSource(Killing.PLAYERS, 10)
			.addXPSource(Killing.VILLAGERS, 5);

}
