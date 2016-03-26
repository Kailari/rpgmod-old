package kailari.rpgmod.common.stats;

import kailari.rpgmod.api.common.stats.ICharacterStats;
import kailari.rpgmod.api.common.stats.StatRegistry;
import kailari.rpgmod.api.common.stats.StatVariable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

/**
 * Provides capability to store character stats in an entity.
 */
public class CapabilityCharacterStats {
	public static void register() {
		CapabilityManager.INSTANCE.register(
				ICharacterStats.class,
				new Capability.IStorage<ICharacterStats>() {
					@Override
					public NBTBase writeNBT(Capability<ICharacterStats> capability, ICharacterStats instance, EnumFacing side) {
						NBTTagCompound compound = new NBTTagCompound();
						writeToNBT(compound, instance);

						return compound;
					}

					@Override
					public void readNBT(Capability<ICharacterStats> capability, ICharacterStats instance, EnumFacing side, NBTBase base) {
						NBTTagCompound compound = (NBTTagCompound) base;
						readFromNBT(compound, instance);
					}
				},
				new Callable<ICharacterStats>() {
					@Override
					public ICharacterStats call() throws Exception {
						return new CharacterStats(null);
					}
				}
		);
	}

	protected static void writeToNBT(final NBTTagCompound compound, final ICharacterStats instance) {
		for (StatVariable var : StatRegistry.getAll()) {
			compound.setFloat(var.getNBTKey(), instance.get(var));
		}
	}

	protected static void readFromNBT(NBTTagCompound compound, ICharacterStats instance) {
		for (StatVariable var : StatRegistry.getAll()) {
			instance.set(var, compound.getFloat(var.getNBTKey()));
		}
	}
}
