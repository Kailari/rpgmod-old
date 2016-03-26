package kailari.rpgmod.common.networking.messages.stats;

import io.netty.buffer.ByteBuf;
import kailari.rpgmod.RPGMod;
import kailari.rpgmod.api.common.stats.StatRegistry;
import kailari.rpgmod.common.Capabilities;
import kailari.rpgmod.common.stats.CharacterStats;
import kailari.rpgmod.util.CapHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Message for synchronizing CharacterStats variables
 */
public class SyncStatVariableMessage implements IMessage {

	private String nbtKey;
	private float value;

	// Required by Forge networking
	public SyncStatVariableMessage() {
		this(null, Float.NaN);
	}

	public SyncStatVariableMessage(String nbtKey, float value) {
		this.value = value;
		this.nbtKey = nbtKey;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.value = buf.readFloat();

		int len = buf.readByte();
		this.nbtKey = "";
		for (int i = 0; i < len; i++) {
			this.nbtKey += buf.readChar();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(this.value);

		char[] chars = this.nbtKey.toCharArray();
		buf.writeByte(chars.length);

		for (char c : chars) {
			buf.writeChar(c);
		}
	}

	public static class Handler implements IMessageHandler<SyncStatVariableMessage, IMessage> {
		@Override
		public IMessage onMessage(final SyncStatVariableMessage message, final MessageContext ctx) {
			if (ctx.side.isServer()) {
				RPGMod.logger.error("SyncStatVariableMessage received on server!");
				return null;
			}

			IThreadListener mainThread = RPGMod.proxy.getMainThread(ctx);

			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {

					EntityPlayer player = RPGMod.proxy.getPlayerEntity(ctx);
					CharacterStats stats = (CharacterStats) CapHelper.getCapability(player, Capabilities.STATS);

					if (stats != null) {
						stats.receiveVariableData(StatRegistry.get(message.nbtKey), message.value);
					}

				}
			});

			return null; // No response
		}
	}
}
