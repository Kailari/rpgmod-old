package kailari.rpgmod.common.stats;

import kailari.rpgmod.common.Capabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Capability provider for character stats
 */
public class ProviderCharacterStats implements ICapabilitySerializable<NBTTagCompound> {
	private final CharacterStats stats;

	public ProviderCharacterStats(EntityPlayer player) {
		this.stats = new CharacterStats(player);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return Capabilities.CAPABILITY_STATS != null && capability == Capabilities.CAPABILITY_STATS;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == Capabilities.CAPABILITY_STATS ? (T) stats : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		if (Capabilities.CAPABILITY_STATS != null) {
			return (NBTTagCompound) Capabilities.CAPABILITY_STATS.getStorage()
					.writeNBT(Capabilities.CAPABILITY_STATS, this.stats, null);
		} else {
			return new NBTTagCompound();
		}
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		if (Capabilities.CAPABILITY_STATS != null) {
			Capabilities.CAPABILITY_STATS.getStorage()
					.readNBT(Capabilities.CAPABILITY_STATS, this.stats, null, compound);
		}
	}
}
