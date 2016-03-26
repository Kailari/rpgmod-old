package kailari.rpgmod.common.stats.attributes;

import kailari.rpgmod.common.Capabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Provides CharacterAttributes capability
 */
public class ProviderCharacterAttributes implements ICapabilitySerializable<NBTTagCompound> {
	private final CharacterAttributes attributes;

	public ProviderCharacterAttributes(EntityPlayer player) {
		this.attributes = new CharacterAttributes(player);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return Capabilities.ATTRIBUTES != null && capability == Capabilities.ATTRIBUTES;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == Capabilities.ATTRIBUTES ? (T) this.attributes : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		if (Capabilities.ATTRIBUTES != null) {
			return (NBTTagCompound) Capabilities.ATTRIBUTES.getStorage()
					.writeNBT(Capabilities.ATTRIBUTES, this.attributes, null);
		} else {
			return new NBTTagCompound();
		}
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		if (Capabilities.ATTRIBUTES != null) {
			Capabilities.ATTRIBUTES.getStorage()
					.readNBT(Capabilities.ATTRIBUTES, this.attributes, null, compound);
		}
	}
}
