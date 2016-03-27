package kailari.rpgmod.api.common.stats;

import net.minecraft.entity.SharedMonsterAttributes;

/**
 * Contains all default character stat-variables
 */
public final class Stats {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Health
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Max HP. Uses vanilla MAX_HEALTH attribute.
	 */
	public static final StatVariable MAX_HEALTH = new StatVariable(SharedMonsterAttributes.MAX_HEALTH, true);
	
	/**
	 * Chance to evade incoming hits. Uses internal attribute. Uses internal attribute.
	 * <br>
	 * Defaults to 0.0. Lower bound is set to zero to prevent negative values.
	 * Upper bound is set to 1.0 which equals 100%.
	 */
	public static final StatVariable EVASION = new StatVariable("stats.evasion", 0.0f, 0.0f, 1.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Mobility
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Movement speed. Uses vanilla MOVEMENT_SPEED attribute.
	 */
	public static final StatVariable MOVEMENT_SPEED = new StatVariable(SharedMonsterAttributes.MOVEMENT_SPEED, true);

	/**
	 * Sprint speed multiplier. Uses internal attribute.
	 * <br>
	 * Operation is applied as MOVE_SPEED = MOVE_SPEED * (1 + SPRINT_SPEED_MULT),
	 * thus smallest possible value is set to -1.0, at which the entity can't move by sprinting.
	 * Upper bound arbitrarily selected and is subject to change.
	 */
	public static final StatVariable SPRINT_SPEED_MULT = new StatVariable("stats.sprintMult", 0.0f, -1.0f, 10.0f);

	/**
	 * Additive bonus to the jump force. Uses internal attribute.
	 * <br>
	 * Vanilla jump strength is 0.42, thus lower limit is set to -0.42 to prevent negative jump strength.
	 * Upper bound is arbitrarily selected and is subject to change.
	 */
	public static final StatVariable JUMP_BONUS = new StatVariable("stats.jumpForceBonus", 0.0f, -0.42f, 10.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Hunger
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Maximum food level player can have. Uses internal attribute.
	 * <br>
	 * Defaults to vanilla value of 20. Lower bound is set to zero, as negative
	 * values would probably break something. Upper bound is arbitrary.
	 */
	public static final StatVariable FOOD_MAX = new StatVariable("stats.maxFoodLevel", 20.0f, 0.0f, 1024.0f);
	
	/**
	 * Maximum amount of exhaustion. Uses internal attribute.
	 * <br>
	 * Defaults to vanilla value of 4.0. Lower bound is set to zero, as negative values would
	 * probably break something. Upper bound is arbitrary.
	 */
	public static final StatVariable EXHAUSTION_MAX = new StatVariable("stats.maxExhaustion", 4.0f, 0.0f, 40.0f);
	
	/**
	 * Modifies how much exhausting activities affect the character. Uses internal attribute.
	 * <br>
	 * Defaults to 1.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable EXHAUSTION_MULT = new StatVariable("stats.exhaustionMult", 1.0f, 0.0f, 100.0f);
	
	/**
	 * Modifies how much hunger one food unit is worth. Uses internal attribute.
	 * <br>
	 * Defaults to 1.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable FOOD_MULT = new StatVariable("stats.hungerRestorationMult", 1.0f, 0.0f, 100.0f);
	
	/**
	 * Modifies how much saturation gets restored per food unit. Uses internal attribute.
	 * <br>
	 * Defaults to 1.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable SATURATION_MULT = new StatVariable("stats.saturationMult", 1.0f, 0.0f, 100.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Combat
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Chance to miss outgoing attacks. Uses internal attribute.
	 * <br>
	 * Defaults to 0.0. Lower bound is set to zero to prevent negative values.
	 * Upper bound is set to 1.0 which equals 100%.
	 */
	public static final StatVariable MISS_CHANCE = new StatVariable("stats.missChance", 0.0f, 0.0f, 1.0f);

	/**
	 * Base damage. Uses vanilla ATTACK_DAMAGE attribute.
	 */
	public static final StatVariable ATTACK_DAMAGE = new StatVariable(SharedMonsterAttributes.ATTACK_DAMAGE, true);

	/**
	 * Damage multiplier for outgoing melee attacks. Uses internal attribute.
	 * <br>
	 * Defaults to 1.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable DAMAGE_MULT_MELEE = new StatVariable("stats.meleeDmgMult", 1.0f, 0.0f, 10000.0f);
	
	/**
	 * Damage multiplier for outgoing ranged attacks. Uses internal attribute.
	 * <br>
	 * Defaults to 1.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable DAMAGE_MULT_RANGED = new StatVariable("stats.rangedDmgMult", 1.0f, 0.0f, 10000.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Mining
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
		Mining speed is calculated using:
			bonus = genericBonus + toolBonus
			mult  = genericMult  + toolMult
			speed = (base + bonus) * (1.0f + mult);
	 */

	/**
	 * Additive mining speed bonus. Uses internal attribute.
	 * <br>
	 * Defaults to 0.0. Upper and lower bounds are arbitrary.
	 */
	public static final StatVariable MINING_SPEED_BONUS = new StatVariable("stats.miningSpeedBonus", 0.0f, -100.0f, 100.0f);

	/**
	 * Multiplier for mining speed. Uses internal attribute.
	 * <br>
	 * Defaults to 0.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable MINING_SPEED_MULT = new StatVariable("stats.miningSpeedMult", 0.0f, 0.0f, 100.0f);

	/**
	 * Additive mining speed bonus for pickaxes. Uses internal attribute.
	 * <br>
	 * Defaults to 0.0. Upper and lower bounds are arbitrary.
	 */
	public static final StatVariable MINING_SPEED_BONUS_PICK = new StatVariable("stats.pickMiningSpeedBonus", 0.0f, -100.0f, 100.0f);

	/**
	 * Multiplier for mining speed for pickaxes. Uses internal attribute.
	 * <br>
	 * Defaults to 1.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable MINING_SPEED_MULT_PICK = new StatVariable("stats.pickMiningSpeedMult", 0.0f, 0.0f, 100.0f);

	/**
	 * Additive mining speed bonus for axes. Uses internal attribute.
	 * <br>
	 * Defaults to 0.0. Upper and lower bounds are arbitrary.
	 */
	public static final StatVariable MINING_SPEED_BONUS_AXE = new StatVariable("stats.axeMiningSpeedBonus", 0.0f, -100.0f, 100.0f);

	/**
	 * Multiplier for mining speed for axes. Uses internal attribute.
	 * <br>
	 * Defaults to 1.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable MINING_SPEED_MULT_AXE = new StatVariable("stats.axeMiningSpeedMult", 0.0f, 0.0f, 100.0f);

	/**
	 * Additive mining speed bonus for shovels. Uses internal attribute.
	 * <br>
	 * Defaults to 0.0. Upper and lower bounds are arbitrary.
	 */
	public static final StatVariable MINING_SPEED_BONUS_SHOVEL = new StatVariable("stats.shovelMiningSpeedBonus", 0.0f, -100.0f, 100.0f);

	/**
	 * Multiplier for mining speed for shovels. Uses internal attribute.
	 * <br>
	 * Defaults to 1.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable MINING_SPEED_MULT_SHOVEL = new StatVariable("stats.shovelMiningSpeedMult", 0.0f, 0.0f, 100.0f);

	/**
	 * Additive mining speed bonus for digging with hand. Uses internal attribute.
	 * <br>
	 * Defaults to 0.0. Upper and lower bounds are arbitrary.
	 */
	public static final StatVariable MINING_SPEED_BONUS_HAND = new StatVariable("stats.handMiningSpeedBonus", 0.0f, -100.0f, 100.0f);

	/**
	 * Multiplier for mining speed for digging with hand. Uses internal attribute.
	 * <br>
	 * Defaults to 1.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable MINING_SPEED_MULT_HAND = new StatVariable("stats.handMiningSpeedMult", 0.0f, 0.0f, 100.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Using anvil
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Additive modifier to anvil break chance. Uses internal attribute.
	 * <br>
	 * Defaults to 0.0. Lower and upper are set to +/- 1.0f which equals a 100% change in probability.
	 */
	public static final StatVariable ANVIL_BREAK_CHANCE_BONUS = new StatVariable("stats.anvilBreakChanceBonus", 0.0f, -1.0f, 1.0f);

	/**
	 * Multiplier for anvil break chance. Uses internal attribute.
	 * <br>
	 * Defaults to 1.0. Lower bound is set to zero to prevent negative values. Upper bound is arbitrary.
	 */
	public static final StatVariable ANVIL_BREAK_CHANCE_MULT = new StatVariable("stats.anvilBreakChanceMult", 1.0f, 0.0f, 100.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Misc
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Luck. Uses vanilla LUCK attribute.
	 */
	public static final StatVariable LUCK = new StatVariable(SharedMonsterAttributes.LUCK, true);
}
