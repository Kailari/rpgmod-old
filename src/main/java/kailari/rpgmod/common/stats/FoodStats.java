package kailari.rpgmod.common.stats;

import kailari.rpgmod.RPGMod;
import kailari.rpgmod.api.common.stats.ICharacterStats;
import kailari.rpgmod.api.common.stats.Stats;
import kailari.rpgmod.common.Capabilities;
import kailari.rpgmod.util.CapHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Modified version of {@link net.minecraft.util.FoodStats}
 * <p>
 * Implementation is based on copy-pasted version of the said class.
 * <p>
 * Offers practically the same behavior as the original, with the exception of
 * some constants made variables that can be modified based on per-character stats.
 */
public class FoodStats extends net.minecraft.util.FoodStats {

	/**
	 * The player's food level.
	 */
	private int foodLevel = 20;

	/**
	 * The player's food saturation.
	 */
	private float foodSaturationLevel = 5.0F;

	/**
	 * The player's food exhaustion.
	 */
	private float foodExhaustionLevel;

	/**
	 * The player's food timer value.
	 */
	private int foodTimer;
	private int prevFoodLevel = 20;

	/**
	 * Character's stats
	 */
	private ICharacterStats cachedStats;
	private final EntityPlayer player;
	
	private ICharacterStats getStats() {
		if (this.cachedStats == null) {
			this.cachedStats = CapHelper.getCapability(this.player, Capabilities.STATS);

			if (this.cachedStats == null) {
				throw new IllegalStateException("Player entity did not have required capabilities!");
			}
		}

		return this.cachedStats;
	}


	public FoodStats(EntityPlayer player, net.minecraft.util.FoodStats original) {
		this.player = player;

		this.foodLevel = original.getFoodLevel();
		this.foodSaturationLevel = original.getSaturationLevel();

		// Get foodTimer and foodExhaustionLevel via reflection
		this.foodTimer = ObfuscationReflectionHelper.getPrivateValue(
				net.minecraft.util.FoodStats.class,
				original,
				"foodTimer", "field_75123_d");
		this.foodExhaustionLevel = ObfuscationReflectionHelper.getPrivateValue(
				net.minecraft.util.FoodStats.class,
				original,
				"foodExhaustionLevel", "field_75126_c");
	}


	/**
	 * Add food stats.
	 */
	@Override
	public void addStats(int foodLevelIncrease, float foodSaturationModifier) {
		// Apply modifiers
		foodLevelIncrease *= getStats().get(Stats.FOOD_MULT);

		// Increase food level
		this.foodLevel = Math.min(this.foodLevel + foodLevelIncrease, (int) getStats().get(Stats.FOOD_MAX));

		// Calculate saturation increase
		float saturationIncrease = (float) foodLevelIncrease * foodSaturationModifier * 2.0f;

		// Apply modifiers
		saturationIncrease *= getStats().get(Stats.SATURATION_MULT);

		// Increase saturation
		this.foodSaturationLevel = Math.min(this.foodSaturationLevel + saturationIncrease, (float) this.foodLevel);
	}

	@Override
	public void addStats(ItemFood foodItem, ItemStack stack) {
		this.addStats(foodItem.getHealAmount(stack), foodItem.getSaturationModifier(stack));
	}

	/**
	 * Handles the food game logic.
	 */
	@Override
	public void onUpdate(EntityPlayer player) {
		EnumDifficulty enumdifficulty = player.worldObj.getDifficulty();
		this.prevFoodLevel = this.foodLevel;

		// Apply exhaustion
		if (this.foodExhaustionLevel > getStats().get(Stats.EXHAUSTION_MAX)) {
			this.foodExhaustionLevel -= getStats().get(Stats.EXHAUSTION_MAX);

			// Decrease saturation, and if saturation is at zero, start consuming hunger
			// (unless we are on peaceful difficulty)
			if (this.foodSaturationLevel > 0.0F) {
				this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
			} else if (enumdifficulty != EnumDifficulty.PEACEFUL) {
				this.foodLevel = Math.max(this.foodLevel - 1, 0);
			}
		}

		boolean useNaturalRegeneration = player.worldObj.getGameRules().getBoolean("naturalRegeneration");

		// TODO: Determine naturalRegen foodLevel caps for modified max values

		// Apply natural regeneration / hurt player if starving
		if (useNaturalRegeneration && this.foodSaturationLevel > 0.0F && player.shouldHeal() && this.foodLevel >= 20) {

			++this.foodTimer;

			if (this.foodTimer >= 10) {
				float f = Math.min(this.foodSaturationLevel, 4.0F);
				player.heal(f / 4.0F);
				this.addExhaustion(f);
				this.foodTimer = 0;
			}
		} else if (useNaturalRegeneration && this.foodLevel >= 18 && player.shouldHeal()) {
			++this.foodTimer;

			if (this.foodTimer >= 80) {
				player.heal(1.0F);
				this.addExhaustion(4.0F);
				this.foodTimer = 0;
			}
		} else if (this.foodLevel <= 0) {
			++this.foodTimer;

			if (this.foodTimer >= 80) {
				if (player.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL) {
					player.attackEntityFrom(DamageSource.starve, 1.0F);
				}

				this.foodTimer = 0;
			}
		} else {
			this.foodTimer = 0;
		}
	}

	/**
	 * Reads the food data for the player.
	 */
	@Override
	public void readNBT(NBTTagCompound compound) {
		RPGMod.logger.info("Reading foodStats from NBT!");

		// Compound is potentially NULL during first login
		if (compound != null && compound.hasKey("foodLevel", 99)) {
			this.foodLevel = compound.getInteger("foodLevel");
			this.foodTimer = compound.getInteger("foodTickTimer");
			this.foodSaturationLevel = compound.getFloat("foodSaturationLevel");
			this.foodExhaustionLevel = compound.getFloat("foodExhaustionLevel");
		}
	}

	/**
	 * Writes the food data for the player.
	 */
	@Override
	public void writeNBT(NBTTagCompound compound) {
		RPGMod.logger.info("Writing foodStats to NBT!");

		compound.setInteger("foodLevel", this.foodLevel);
		compound.setInteger("foodTickTimer", this.foodTimer);
		compound.setFloat("foodSaturationLevel", this.foodSaturationLevel);
		compound.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
	}

	/**
	 * Get the player's food level.
	 */
	@Override
	public int getFoodLevel() {
		return this.foodLevel;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getPrevFoodLevel() {
		return this.prevFoodLevel;
	}

	/**
	 * Get whether the player can eat food.
	 */
	@Override
	public boolean needFood() {
		return this.foodLevel < getStats().get(Stats.FOOD_MAX);
	}

	/**
	 * adds input to foodExhaustionLevel to a max of 40
	 */
	@Override
	public void addExhaustion(float amount) {
		// Apply modifiers
		amount *= getStats().get(Stats.EXHAUSTION_MULT);

		this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + amount, 40.0f);
	}

	/**
	 * Get the player's food saturation level.
	 */
	@Override
	public float getSaturationLevel() {
		return this.foodSaturationLevel;
	}

	@Override
	public void setFoodLevel(int foodLevelIn) {
		this.foodLevel = foodLevelIn;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setFoodSaturationLevel(float foodSaturationLevelIn) {
		this.foodSaturationLevel = foodSaturationLevelIn;
	}
}
