package kailari.rpgmod.common.networking.messages.stats;

import io.netty.buffer.ByteBuf;
import kailari.rpgmod.RPGMod;
import kailari.rpgmod.api.common.stats.StatRegistry;
import kailari.rpgmod.api.common.stats.StatVariable;
import kailari.rpgmod.common.Capabilities;
import kailari.rpgmod.common.stats.CharacterStats;
import kailari.rpgmod.util.CapHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;
import java.util.Set;

/**
 * Message for synchronizing CharacterStats variables (full re-sync)
 */
public class SyncCharacterStatsMessage implements IMessage {
	
	private String[] nbtTags;
	private float[] values;

	private long randomSeed;

	public SyncCharacterStatsMessage() {
	}

	public SyncCharacterStatsMessage(Set<Map.Entry<StatVariable, Float>> variables, long randomSeed) {
		this.nbtTags = new String[variables.size()];
		this.values = new float[variables.size()];

		int index = 0;
		for (Map.Entry<StatVariable, Float> entry : variables) {
			this.nbtTags[index] = entry.getKey().getNBTKey();
			this.values[index] = entry.getValue();

			index++;
		}

		this.randomSeed = randomSeed;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int varCount = buf.readByte();

		this.nbtTags = new String[varCount];
		this.values = new float[varCount];

		for (int variableIndex = 0; variableIndex < varCount; variableIndex++) {
			int tagLength = buf.readByte();

			String tag = "";
			for (int charIndex = 0; charIndex < tagLength; charIndex++) {
				tag += buf.readChar();
			}

			this.nbtTags[variableIndex] = tag;
			this.values[variableIndex] = buf.readFloat();
		}

		this.randomSeed = buf.readLong();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		// Write variable count
		buf.writeByte(this.nbtTags.length);

		for (int variableIndex = 0; variableIndex < this.nbtTags.length; variableIndex++) {
			// Write string length
			buf.writeByte(this.nbtTags[variableIndex].length());
			
			// Write string characters
			char[] chars = this.nbtTags[variableIndex].toCharArray();
			for (int charIndex = 0; charIndex < chars.length; charIndex++) {
				buf.writeChar(chars[charIndex]);
			}
			
			// Write value
			buf.writeFloat(this.values[variableIndex]);
		}

		buf.writeLong(this.randomSeed);
	}
	
	
	public static class Handler implements IMessageHandler<SyncCharacterStatsMessage, IMessage> {
		@Override
		public IMessage onMessage(final SyncCharacterStatsMessage message, final MessageContext ctx) {
			if (ctx.side.isServer()) {
				RPGMod.logger.error("SyncCharacterStatsMessage received on server!");
				return null;
			}

			IThreadListener mainThread = RPGMod.proxy.getMainThread(ctx);

			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {

					EntityPlayer player = RPGMod.proxy.getPlayerEntity(ctx);

					CharacterStats stats = (CharacterStats) CapHelper.getCapability(player, Capabilities.CAPABILITY_STATS);

					if (stats != null) {
						for (int variableIndex = 0; variableIndex < message.nbtTags.length; variableIndex++) {
							stats.receiveVariableData(
									StatRegistry.get(message.nbtTags[variableIndex]),
									message.values[variableIndex]);
						}

						stats.setSeeds(message.randomSeed);
					}

				}
			});

			return null;
		}
	}
}
