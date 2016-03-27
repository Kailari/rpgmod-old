package kailari.rpgmod.util;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Capability helper. Few helper methods for easy accessing of capabilities.
 */
public class CapHelper {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Entity Helpers
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Helper for determining if capability exists and is valid.
	 */
	public static <T> boolean isValid(Capability<T> capability) {
		return capability != null;
	}

	/**
	 * Helper for determining if entity has a capability
	 */
	public static <T> boolean entityHasCapability(Entity entity, Capability<T> capability) {
		return entity != null && isValid(capability) && entity.hasCapability(capability, null);
	}

	/**
	 * Helper for getting capability of an entity
	 */
	public static <T> T getCapability(Entity entity, Capability<T> capability) {
		return entityHasCapability(entity, capability) ? entity.getCapability(capability, null) : null;
	}
}
