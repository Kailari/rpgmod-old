package kailari.rpgmod.common.stats.attributes;

import kailari.rpgmod.api.common.stats.attributes.Attribute;
import kailari.rpgmod.api.common.stats.attributes.AttributeRegistry;
import kailari.rpgmod.api.common.stats.attributes.ICharacterAttributes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

/**
 * Provides capability to store character attributes in an entity.
 */
public class CapabilityCharacterAttributes {
	public static void register() {
		CapabilityManager.INSTANCE.register(
				ICharacterAttributes.class,

				// Default storage implementation delegates I/O to CharacterAttributes for clarity
				// (and easier access to raw variable values)
				new Capability.IStorage<ICharacterAttributes>() {
					@Override
					public NBTBase writeNBT(Capability<ICharacterAttributes> capability, ICharacterAttributes instance, EnumFacing side) {
						NBTTagCompound compound = new NBTTagCompound();
						writeToNBT(compound, instance);
						return compound;
					}

					@Override
					public void readNBT(Capability<ICharacterAttributes> capability, ICharacterAttributes instance, EnumFacing side, NBTBase base) {
						readFromNBT((NBTTagCompound) base, instance);
					}
				},
				new Callable<CharacterAttributes>() {
					@Override
					public CharacterAttributes call() throws Exception {
						return new CharacterAttributes(null);
					}
				}
		);
	}

	public static void writeToNBT(NBTTagCompound compound, ICharacterAttributes instance) {
		for (Attribute attribute : AttributeRegistry.getAll()) {
			NBTTagCompound attributeCompound = new NBTTagCompound();

			// Only the xp and bonus are needed, as everything else can be calculated from them
			attributeCompound.setInteger("xp", instance.getXP(attribute));
			attributeCompound.setInteger("bonus", instance.getBonus(attribute));

			// Add the attribute tag to the main compound
			compound.setTag(attribute.getNBTKey(), attributeCompound);
		}
	}

	public static void readFromNBT(NBTTagCompound compound, ICharacterAttributes instance) {
		for (Attribute attribute : AttributeRegistry.getAll()) {
			NBTTagCompound attributeCompound = compound.getCompoundTag(attribute.getNBTKey());

			instance.set(
					attribute,
					attributeCompound.getInteger("xp"),
					attributeCompound.getInteger("bonus"));
		}
	}
}
