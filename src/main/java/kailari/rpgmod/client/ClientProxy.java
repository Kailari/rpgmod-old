package kailari.rpgmod.client;

import kailari.rpgmod.client.gui.GuiActionLog;
import kailari.rpgmod.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Clientside initialization/registration code
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
	public void preInit() {
		super.preInit();

		MinecraftForge.EVENT_BUS.register(new GuiActionLog());
	}

	@Override
	public IThreadListener getMainThread(MessageContext context) {
		return Minecraft.getMinecraft();
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext context) {
		return (context.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(context));
	}
}
