package kailari.rpgmod.common.networking.messages.stats.attributes;

import io.netty.buffer.ByteBuf;
import kailari.rpgmod.RPGMod;
import kailari.rpgmod.api.common.stats.attributes.AttributeRegistry;
import kailari.rpgmod.common.Capabilities;
import kailari.rpgmod.common.stats.attributes.CharacterAttributes;
import kailari.rpgmod.util.CapHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Message for synchronizing CharacterAttributes attribute experience.
 */
public class SyncAttributeMessage implements IMessage {

	private String nbtKey;
	private int xp;
	private int bonus;

	// Required by Forge networking
	public SyncAttributeMessage() {
	}

	public SyncAttributeMessage(String nbtKey, int xp, int bonus) {
		this.nbtKey = nbtKey;
		this.xp = xp;
		this.bonus = bonus;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int len = buf.readByte();
		this.nbtKey = "";
		for (int i = 0; i < len; i++) {
			this.nbtKey += buf.readChar();
		}

		this.xp = buf.readInt();
		this.bonus = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		char[] chars = this.nbtKey.toCharArray();
		buf.writeByte(chars.length);

		for (char c : chars) {
			buf.writeChar(c);
		}

		buf.writeInt(this.xp);
		buf.writeInt(this.bonus);
	}

	public static class Handler implements IMessageHandler<SyncAttributeMessage, IMessage> {
		@Override
		public IMessage onMessage(final SyncAttributeMessage message, final MessageContext ctx) {
			if (ctx.side.isServer()) {
				RPGMod.logger.error("SyncStatVariableMessage received on server!");
				return null;
			}

			IThreadListener mainThread = RPGMod.proxy.getMainThread(ctx);

			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {

					EntityPlayer player = RPGMod.proxy.getPlayerEntity(ctx);
					CharacterAttributes stats = (CharacterAttributes) CapHelper.getCapability(player, Capabilities.CAPABILITY_ATTRIBUTES);

					if (stats != null) {
						stats.receiveAttributeData(AttributeRegistry.get(message.nbtKey), message.xp, message.bonus);
					}

				}
			});

			return null; // No response
		}
	}
}
