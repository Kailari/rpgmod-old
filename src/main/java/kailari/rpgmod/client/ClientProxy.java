package kailari.rpgmod.client;

import kailari.rpgmod.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Clientside initialization/registration code
 */
public class ClientProxy extends CommonProxy {
	@Override
	public IThreadListener getMainThread(MessageContext context) {
		return Minecraft.getMinecraft();
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext context) {
		return (context.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(context));
	}
}
