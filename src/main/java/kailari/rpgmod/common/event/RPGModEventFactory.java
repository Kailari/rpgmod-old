package kailari.rpgmod.common.event;

import kailari.rpgmod.api.common.event.player.PlayerAttackMissedEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

/**
 * Helper for firing RPGApi events.
 */
public class RPGModEventFactory {
	public static void onPlayerAttackMissed(EntityPlayer player, Entity target, float evasion, float amount) {
		MinecraftForge.EVENT_BUS.post(new PlayerAttackMissedEvent(player, target, amount, evasion));
	}
}
