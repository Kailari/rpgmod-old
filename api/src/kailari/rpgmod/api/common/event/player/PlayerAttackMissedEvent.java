package kailari.rpgmod.api.common.event.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * PlayerAttackMissedEvent is fired after player misses an attack due to the evasion proc.
 * <br>
 * The event is fired during the {@link net.minecraftforge.event.entity.living.LivingAttackEvent}.<br>
 * <br>
 * {@link #amount} contains amount of damage dodged due to this proc. Note that this amount does not yet
 * contain any stat-based damage multipliers.<br>
 * <br>
 * {@link #missChance} contains the value of miss chance stat of the attacker.
 * This is target's evasion and attacker miss-chance combined.<br>
 * {@link #attacker} contains reference to the attacking player.<br>
 * {@link #target} contains reference to attacked target.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class PlayerAttackMissedEvent extends Event {
	private final float amount;
	private final float missChance;
	private final EntityPlayer attacker;
	private final Entity target;

	public PlayerAttackMissedEvent(EntityPlayer attacker, Entity target, float amount, float missChance) {
		this.amount = amount;
		this.missChance = missChance;
		this.attacker = attacker;
		this.target = target;
	}

	public float getAmount() {
		return amount;
	}

	public float getMissChance() {
		return missChance;
	}

	public EntityPlayer getAttacker() {
		return attacker;
	}

	public Entity getTarget() {
		return target;
	}
}
