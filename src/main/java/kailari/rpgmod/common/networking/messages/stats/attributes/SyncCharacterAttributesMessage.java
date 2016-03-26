package kailari.rpgmod.common.networking.messages.stats.attributes;

import io.netty.buffer.ByteBuf;
import kailari.rpgmod.RPGMod;
import kailari.rpgmod.api.common.stats.attributes.Attribute;
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
 * Message for synchronizing CharacterAttributes variables (full re-sync)
 */
public class SyncCharacterAttributesMessage implements IMessage {

	private String[] nbtTags;
	private int[] xp;
	private int[] bonus;

	public SyncCharacterAttributesMessage() {
	}

	public SyncCharacterAttributesMessage(CharacterAttributes attrs) {
		this.nbtTags = new String[AttributeRegistry.getCount()];
		this.xp = new int[AttributeRegistry.getCount()];
		this.bonus = new int[AttributeRegistry.getCount()];

		int index = 0;
		for (Attribute attribute : AttributeRegistry.getAll()) {
			this.nbtTags[index] = attribute.getNBTKey();
			this.xp[index] = attrs.getXP(attribute);
			this.bonus[index] = attrs.getBonus(attribute);

			index++;
		}
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int varCount = buf.readByte();

		this.nbtTags = new String[varCount];
		this.xp = new int[varCount];
		this.bonus = new int[varCount];

		for (int index = 0; index < varCount; index++) {
			int tagLength = buf.readByte();

			String tag = "";
			for (int charIndex = 0; charIndex < tagLength; charIndex++) {
				tag += buf.readChar();
			}

			this.nbtTags[index] = tag;
			this.xp[index] = buf.readInt();
			this.bonus[index] = buf.readInt();
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		// Write variable count
		buf.writeByte(this.nbtTags.length);

		for (int index = 0; index < this.nbtTags.length; index++) {
			// Write string length
			buf.writeByte(this.nbtTags[index].length());
			
			// Write string characters
			char[] chars = this.nbtTags[index].toCharArray();
			for (int charIndex = 0; charIndex < chars.length; charIndex++) {
				buf.writeChar(chars[charIndex]);
			}
			
			// Write xp
			buf.writeInt(this.xp[index]);
			buf.writeInt(this.bonus[index]);
		}
	}
	
	
	public static class Handler implements IMessageHandler<SyncCharacterAttributesMessage, IMessage> {
		@Override
		public IMessage onMessage(final SyncCharacterAttributesMessage message, final MessageContext ctx) {
			if (ctx.side.isServer()) {
				RPGMod.logger.error("SyncCharacterAttributesMessage received on server!");
				return null;
			}

			IThreadListener mainThread = RPGMod.proxy.getMainThread(ctx);

			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {

					EntityPlayer player = RPGMod.proxy.getPlayerEntity(ctx);

					CharacterAttributes attrs = (CharacterAttributes) CapHelper.getCapability(player, Capabilities.CAPABILITY_ATTRIBUTES);

					if (attrs != null) {
						for (int index = 0; index < message.nbtTags.length; index++) {
							attrs.receiveAttributeData(
									AttributeRegistry.get(message.nbtTags[index]),
									message.xp[index],
									message.bonus[index]);
						}
					}
				}
			});

			return null;
		}
	}
}
