package kailari.rpgmod.common.stats;

import kailari.rpgmod.api.common.stats.ICharacterStats;
import kailari.rpgmod.api.common.stats.StatRegistry;
import kailari.rpgmod.api.common.stats.StatVariable;
import kailari.rpgmod.api.common.stats.Stats;
import kailari.rpgmod.common.networking.Netman;
import kailari.rpgmod.common.networking.messages.stats.SyncCharacterStatsMessage;
import kailari.rpgmod.common.networking.messages.stats.SyncStatVariableMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

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
		return previousDistanceXPPos;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	Get/Set
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public float get(StatVariable variable) {
		return this.variables.get(variable);
	}

	@Override
	public void set(StatVariable variable, float value) {
		this.variables.put(variable, value);

		if (!this.player.worldObj.isRemote) {
			syncVariable(variable);
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	Instance
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final Map<StatVariable, Float> variables;

	private final EntityPlayer player;

	// Additional state variables
	// TODO: Remove dependency to default implementation by creating some sort of "state container" inside ICharacterStats
	private final Random missChanceRandom;
	private boolean wasSprinting; // Records sprinting state so that we know when to add/remove modifiers
	private BlockPos previousDistanceXPPos;

	public CharacterStats(EntityPlayer player) {
		this.player = player;

		// Just initialize here, seed will be set during re-sync (and initial sync).
		this.missChanceRandom = new Random();

		// Initialize all stats
		this.variables = new HashMap<StatVariable, Float>();
		for (StatVariable var : StatRegistry.getAll()) {
			this.variables.put(var, var.getDefaultValue());
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Networking
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void syncVariable(StatVariable variable) {
		Netman.channel_0.sendTo(
				new SyncStatVariableMessage(variable.getNBTKey(), this.get(variable)),
				(EntityPlayerMP) this.player);
	}

	public void doFullSync() {
		if (this.player.worldObj.isRemote) {
			throw new IllegalStateException("doFullSync should NEVER get called on remote!");
		}

		// Get a new random seed
		long seed = this.missChanceRandom.nextLong();
		setSeeds(seed);

		// Send all variables and new random seed to the client
		Netman.channel_0.sendTo(
				new SyncCharacterStatsMessage(this.variables.entrySet(), seed),
				(EntityPlayerMP) this.player);
	}

	@SideOnly(Side.CLIENT)
	public void receiveVariableData(StatVariable var, float value) {
		this.variables.put(var, value);
	}

	public void setSeeds(long seed) {
		this.missChanceRandom.setSeed(seed);
	}
}
