package kailari.rpgmod.common.stats;

import kailari.rpgmod.api.common.stats.ICharacterStats;
import kailari.rpgmod.api.common.stats.StatRegistry;
import kailari.rpgmod.api.common.stats.StatVariable;
import kailari.rpgmod.api.common.stats.Stats;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;

import java.util.Random;
import java.util.UUID;

/**
 * Container for character stats.
 */
public class CharacterStats implements ICharacterStats {

	public boolean doesAttackMiss(DamageSource source, float targetEvasion) {
		// Explosions cannot be dodged
		if (source.isExplosion()) {
			return false;
		}

		float rand = CharacterStats.this.missChanceRandom.nextFloat();
		return rand < this.get(Stats.MISS_CHANCE) || rand < targetEvasion;
	}

	public void setWasSprinting(boolean sprinting) {
		this.wasSprinting = sprinting;
	}

	public boolean wasSprinting() {
		return this.wasSprinting;
	}

	public void setPreviousDistanceXPPos(BlockPos previousDistanceXPPos) {
		this.previousDistanceXPPos = previousDistanceXPPos;
	}

	public BlockPos getPreviousDistanceXPPos() {
		return this.previousDistanceXPPos == null
				? (this.previousDistanceXPPos = this.player.getPosition())
				: this.previousDistanceXPPos;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	Get/Set
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public float get(StatVariable variable) {
		return (float) this.getAttribute(variable).getAttributeValue();
	}

	@Override
	public float getBaseValue(StatVariable variable) {
		return (float) this.getAttribute(variable).getBaseValue();
	}

	@Override
	public void set(StatVariable variable, float value) {
		this.getAttribute(variable).setBaseValue(value);
	}

	@Override
	public void addModifier(StatVariable variable, AttributeModifier modifier) {
		if (!this.getAttribute(variable).hasModifier(modifier)) {
			this.getAttribute(variable).applyModifier(modifier);
		}
	}

	@Override
	public void removeModifier(StatVariable variable, AttributeModifier modifier) {
		this.removeModifier(variable, modifier.getID());
	}

	@Override
	public void removeModifier(StatVariable variable, UUID uuid) {
		this.getAttribute(variable).removeModifier(uuid);
	}

	private IAttributeInstance getAttribute(StatVariable variable) {
		return this.player.getEntityAttribute(variable.getTarget());
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	Instance
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final EntityPlayer player;

	// Additional state variables
	// TODO: Remove dependency to default implementation by creating some sort of "state container" inside ICharacterStats
	private final Random missChanceRandom;
	private boolean wasSprinting; // Records sprinting state so that we know when to add/remove modifiers

	// XXX: This is used for attribute XP, yet it still resides in stats? Refactor, goddammit.
	private BlockPos previousDistanceXPPos;

	public CharacterStats(EntityPlayer player) {
		this.player = player;

		// Just initialize here, seed will be set during re-sync (and initial sync).
		this.missChanceRandom = new Random();
	}


	public static void registerVanillaAttributes(EntityPlayer player) {
		if (player != null) {
			// Register all unregistered attributes
			for (StatVariable variable : StatRegistry.getVarsNeedingRegistering()) {
				player.getAttributeMap().registerAttribute(variable.getTarget());
			}
		}
	}
}
