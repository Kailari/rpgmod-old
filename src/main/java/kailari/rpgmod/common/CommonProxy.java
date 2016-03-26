package kailari.rpgmod.common;

import kailari.rpgmod.common.networking.Netman;
import kailari.rpgmod.common.stats.StatsEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Common-proxy for registering shared stuff
 */
public class CommonProxy {

	public void preInit() {
		Netman.initialize();

		Capabilities.register();

		registerEventHandlers();
	}

	/**
	 * Registers all event handlers that are not registered in their corresponding classes.
	 */
	private void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new StatsEventHandler());
	}


	public void init() {

	}


	public void postInit() {

	}

	public IThreadListener getMainThread(MessageContext context) {
		return (WorldServer) context.getServerHandler().playerEntity.worldObj;
	}

	public EntityPlayer getPlayerEntity(MessageContext context) {
		return context.getServerHandler().playerEntity;
	}
}
