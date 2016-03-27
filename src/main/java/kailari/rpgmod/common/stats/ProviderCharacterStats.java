package kailari.rpgmod.common.stats;

import kailari.rpgmod.common.Capabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Capability provider for character stats
 */
public class ProviderCharacterStats implements ICapabilityProvider {
	private final CharacterStats stats;

	public ProviderCharacterStats(EntityPlayer player) {
		this.stats = new CharacterStats(player);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return Capabilities.STATS != null && capability == Capabilities.STATS;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == Capabilities.STATS ? (T) stats : null;
	}
}
