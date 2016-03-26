package kailari.rpgmod.api.common.stats.attributes.xp;

/**
 * Default attribute XP sources
 */
public final class AttributeXPSources {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Mining
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Digging holes and punching trees!
	 */
	public static final class Mining {

		/**
		 * Breaking any block with any item or without items or whatever. Breaking blocks.
		 */
		public static final AttributeXPSource BREAKING_BLOCKS = new AttributeXPSource();

		/**
		 * Mining with a pick-axe. If block mined is an ore-block, multiplier is based on amount of vanilla xp dropped.
		 */
		public static final AttributeXPSource MINING = new AttributeXPSource();

		/**
		 * Digging with shovel
		 */
		public static final AttributeXPSource SHOVELING = new AttributeXPSource();

		/**
		 * Cutting trees with an axe
		 */
		public static final AttributeXPSource WOODCUTTING = new AttributeXPSource();

		/**
		 * Breaking blocks by hand
		 */
		public static final AttributeXPSource DIGGING = new AttributeXPSource();
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Farming
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * All things farming crops
	 */
	public static final class Farming {

		/**
		 * Using hoe (right-click)
		 */
		public static final AttributeXPSource TILLING = new AttributeXPSource();

		/**
		 * Planting seeds or saplings
		 */
		public static final AttributeXPSource PLANTING = new AttributeXPSource();

		/**
		 * Using bone-meal on crops
		 */
		public static final AttributeXPSource FERTILIZING = new AttributeXPSource();

		/**
		 * Harvesting crops (gathering fully grown wheat, carrots, potatoes, etc.)
		 * XXX: On hiatus until I can come up with some elegant way to identify crop harvesting
		 */
		//public static final AttributeXPSource HARVESTING_CROPS = new AttributeXPSource();
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Killing
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Killing/hunting stuff. All of these specify dropped XP as the xp amount.
	 */
	public static final class Killing {

		// These are pretty much self-explanatory so no comments here

		public static final AttributeXPSource KILLING = new AttributeXPSource();
		public static final AttributeXPSource ANIMALS = new AttributeXPSource();
		public static final AttributeXPSource MONSTERS = new AttributeXPSource();
		public static final AttributeXPSource PLAYERS = new AttributeXPSource();
		public static final AttributeXPSource VILLAGERS = new AttributeXPSource();
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Damaging mobs / entities
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Dealing damage to mobs / entities
	 */
	public static final class Damage {
		public static final AttributeXPSource DEALING = new AttributeXPSource();
		public static final AttributeXPSource DEALING_MELEE = new AttributeXPSource();
		public static final AttributeXPSource DEALING_RANGED = new AttributeXPSource();

		public static final AttributeXPSource TAKING = new AttributeXPSource();
		public static final AttributeXPSource TAKING_MELEE = new AttributeXPSource();
		public static final AttributeXPSource TAKING_RANGED = new AttributeXPSource();

		public static final AttributeXPSource DODGING = new AttributeXPSource();
		public static final AttributeXPSource DODGING_MELEE = new AttributeXPSource();
		public static final AttributeXPSource DODGING_RANGED = new AttributeXPSource();

		public static final AttributeXPSource MISSING = new AttributeXPSource();
		public static final AttributeXPSource MISSING_MELEE = new AttributeXPSource();
		public static final AttributeXPSource MISSING_RANGED = new AttributeXPSource();

		public static final AttributeXPSource BLOCKING = new AttributeXPSource();
		public static final AttributeXPSource BLOCKING_MELEE = new AttributeXPSource();
		public static final AttributeXPSource BLOCKING_RANGED = new AttributeXPSource();
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Crafting
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Crafting stuff
	 */
	public static final class Crafting {

		/**
		 * Crafting things
		 */
		public static final AttributeXPSource CRAFTING = new AttributeXPSource();

		/**
		 * Combining tools (repairing) in crafting grid
		 */
		public static final AttributeXPSource REPAIRING = new AttributeXPSource();
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Furnace operating
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Operating furnace
	 */
	public static final class Furnace {

		/**
		 * Any operation performed via furnace
		 */
		public static final AttributeXPSource OPERATING = new AttributeXPSource();


		/**
		 * Cooking food
		 */
		public static final AttributeXPSource COOKING = new AttributeXPSource();

		/**
		 * Smelting ore
		 */
		public static final AttributeXPSource SMELTING = new AttributeXPSource();
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Brewing
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Brewing potions
	 */
	public static final class Brewing {

		/**
		 * General operation of brewing stand.
		 */
		public static final AttributeXPSource BREWING = new AttributeXPSource();
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Enchanting
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Using enchanting table
	 *
	 * TODO: This requires additional forge hooks. Enchantment table currently has none.
	 */
//	public static final class Enchanting {
//
//		/**
//		 * Using enchanting table
//		 */
//		public static final AttributeXPSource ENCHANTING = new AttributeXPSource();
//
//		/**
//		 * Enchanting books
//		 */
//		public static final AttributeXPSource BOOKS = new AttributeXPSource();
//
//		/**
//		 * Enchanting tools
//		 */
//		public static final AttributeXPSource TOOLS = new AttributeXPSource();
//
//		/**
//		 * Enchanting weapons
//		 */
//		public static final AttributeXPSource WEAPONS = new AttributeXPSource();
//
//		/**
//		 * Enchanting armor
//		 */
//		public static final AttributeXPSource ARMOR = new AttributeXPSource();
//	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Forging (using anvil)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Using anvil. Non-player entities cannot use anvil, so these work only for player entities.
	 */
	public static final class Forging {

		/**
		 * Any usage of anvil
		 */
		public static final AttributeXPSource FORGING = new AttributeXPSource();

		/**
		 * Using anvil to do something to tools
		 */
		public static final AttributeXPSource TOOLS = new AttributeXPSource();

		/**
		 * Using anvil to do something to weapons
		 */
		public static final AttributeXPSource WEAPONS = new AttributeXPSource();

		/**
		 * Using anvil to do something to armor
		 */
		public static final AttributeXPSource ARMOR = new AttributeXPSource();

		/**
		 * Forging books together using anvil
		 */
		public static final AttributeXPSource BOOKS = new AttributeXPSource();

		/**
		 * Repairing tools using anvil
		 */
		public static final AttributeXPSource REPAIRING = new AttributeXPSource();

		/**
		 * Applying enchantments to items using anvil
		 */
		public static final AttributeXPSource ENCHANTING = new AttributeXPSource();

		/**
		 * Naming items using anvil
		 */
		public static final AttributeXPSource NAMING = new AttributeXPSource();
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Maneuvers
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Moving and other mobility-related stuff
	 */
	public static final class Maneuvers {

		public static final AttributeXPSource WALKING = new AttributeXPSource();
		public static final AttributeXPSource JUMPING = new AttributeXPSource();
		public static final AttributeXPSource SPRINTING = new AttributeXPSource();
		public static final AttributeXPSource JUMPING_WHILE_SPRINTING = new AttributeXPSource();
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Misc
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * These are either too stupid or otherwise not fitting for other categories.
	 * ...or alternatively I've just been too lazy to categorize them :s
	 */
	public static final class Misc {
		public static final AttributeXPSource RESPAWNING = new AttributeXPSource();
	}
}
