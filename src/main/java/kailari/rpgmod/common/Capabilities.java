package kailari.rpgmod.common;

import kailari.rpgmod.RPGMod;
import kailari.rpgmod.api.common.stats.ICharacterStats;
import kailari.rpgmod.api.common.stats.attributes.ICharacterAttributes;
import kailari.rpgmod.common.stats.CapabilityCharacterStats;
import kailari.rpgmod.common.stats.ProviderCharacterStats;
import kailari.rpgmod.common.stats.attributes.CapabilityCharacterAttributes;
import kailari.rpgmod.common.stats.attributes.ProviderCharacterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Contains all capabilities added by this mod.
 */
public class Capabilities {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Capability definitions
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@CapabilityInject(ICharacterStats.class)
	public static Capability<ICharacterStats> STATS = null;
	public static final ResourceLocation STATS_ID = new ResourceLocation(RPGMod.MODID, "CharacterStats");

	@CapabilityInject(ICharacterAttributes.class)
	public static Capability<ICharacterAttributes> ATTRIBUTES = null;
	public static final ResourceLocation ATTRIBUTES_ID = new ResourceLocation(RPGMod.MODID, "CharacterAttributes");


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Registration
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void register() {
		CapabilityCharacterStats.register();
		CapabilityCharacterAttributes.register();

		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Boring stuff
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Handles attaching capabilities
	 */
	public static class EventHandler {
		@SubscribeEvent
		public void onAttachCapabilities(AttachCapabilitiesEvent.Entity event) {
			if (event.getEntity() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.getEntity();

				event.addCapability(STATS_ID, new ProviderCharacterStats(player));
				event.addCapability(ATTRIBUTES_ID, new ProviderCharacterAttributes(player));
			}
		}
	}
}
