package kailari.rpgmod.api.common.stats;

/**
 * Contains all default character stat-variables
 */
public final class Stats {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Health
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Max HP
	 */
	public static final StatVariable HP_MAX = new StatVariable("maxHP", 20.0f);
	
	/**
	 * Chance to evade incoming hits
	 */
	public static final StatVariable EVASION = new StatVariable("evasion", 0.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Mobility
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final StatVariable MOVEMENT_SPEED = new StatVariable("moveSpeed", 0.7f);

	public static final StatVariable SPRINT_SPEED_MULT = new StatVariable("sprintMult", 0.0f);

	public static final StatVariable JUMP_BONUS = new StatVariable("jumpForceBonus", 0.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Hunger
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Maximum food level player can have.
	 * Defaults to vanilla value of 20
	 */
	public static final StatVariable FOOD_MAX = new StatVariable("maxFoodLevel", 20);
	
	/**
	 * Maximum amount of exhaustion.
	 * Defaults to vanilla value of 4.0
	 */
	public static final StatVariable EXHAUSTION_MAX = new StatVariable("maxExhaustion", 4.0f);
	
	/**
	 * Modifies how much exhausting activities affect the character.
	 * Defaults to 1.0
	 */
	public static final StatVariable EXHAUSTION_MULT = new StatVariable("exhaustionMult", 1.0f);
	
	/**
	 * Modifies how much hunger one food unit is worth.
	 * Defaults to 1.0
	 */
	public static final StatVariable FOOD_MULT = new StatVariable("hungerRestorationMult", 1.0f);
	
	/**
	 * Modifies how much saturation gets restored.
	 * Defaults to 1.0
	 */
	public static final StatVariable SATURATION_MULT = new StatVariable("saturationMult", 1.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Combat
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Chance to miss outgoing attacks
	 */
	public static final StatVariable MISS_CHANCE = new StatVariable("missChance", 0.0f);
	
	/**
	 * Damage multiplier for outgoing melee attacks
	 */
	public static final StatVariable DAMAGE_MULT_MELEE = new StatVariable("meleeDmgMult", 1.0f);
	
	/**
	 * Damage multiplier for outgoing ranged attacks
	 */
	public static final StatVariable DAMAGE_MULT_RANGED = new StatVariable("rangedDmgMult", 1.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Mining
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Additive mining speed bonus
	 */
	public static final StatVariable MINING_SPEED_BONUS = new StatVariable("miningSpeedBonus", 0.0f);

	/**
	 * Multiplier for mining speed
	 */
	public static final StatVariable MINING_SPEED_MULT = new StatVariable("miningSpeedMult", 1.0f);

	/**
	 * Additive mining speed bonus for pickaxes
	 */
	public static final StatVariable MINING_SPEED_BONUS_PICK = new StatVariable("pickMiningSpeedBonus", 0.0f);

	/**
	 * Multiplier for mining speed for pickaxes
	 */
	public static final StatVariable MINING_SPEED_MULT_PICK = new StatVariable("pickMiningSpeedMult", 1.0f);

	/**
	 * Additive mining speed bonus for axes
	 */
	public static final StatVariable MINING_SPEED_BONUS_AXE = new StatVariable("axeMiningSpeedBonus", 0.0f);

	/**
	 * Multiplier for mining speed for axes
	 */
	public static final StatVariable MINING_SPEED_MULT_AXE = new StatVariable("axeMiningSpeedMult", 1.0f);

	/**
	 * Additive mining speed bonus for shovels
	 */
	public static final StatVariable MINING_SPEED_BONUS_SHOVEL = new StatVariable("shovelMiningSpeedBonus", 0.0f);

	/**
	 * Multiplier for mining speed for shovels
	 */
	public static final StatVariable MINING_SPEED_MULT_SHOVEL = new StatVariable("shovelMiningSpeedMult", 1.0f);

	/**
	 * Additive mining speed bonus for digging with hand
	 */
	public static final StatVariable MINING_SPEED_BONUS_HAND = new StatVariable("handMiningSpeedBonus", 0.0f);

	/**
	 * Multiplier for mining speed for digging with hand
	 */
	public static final StatVariable MINING_SPEED_MULT_HAND = new StatVariable("handMiningSpeedMult", 1.0f);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Using anvil
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Additive modifier to anvil break chance
	 */
	public static final StatVariable ANVIL_BREAK_CHANCE_BONUS = new StatVariable("anvilBreakChanceBonus", 0.0f);

	/**
	 * Multiplier for anvil break chance
	 */
	public static final StatVariable ANVIL_BREAK_CHANCE_MULT = new StatVariable("anvilBreakChanceMult", 1.0f);
}
