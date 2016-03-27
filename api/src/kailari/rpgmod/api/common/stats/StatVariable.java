package kailari.rpgmod.api.common.stats;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

/**
 * Wrapper for IAttribute. Handles auto-registration to player attribute maps via StatRegistry, and
 * offers a helper-constructor for initializing Attributes behind-the-scenes. Looks nicer than juts
 * tossing IAttributes around.
 * <p/>
 * Quite pointless at the moment, now that most workload was moved to the vanilla attributes system.
 * Removing this could be an option, but that would require changing the registration system to non-
 * automatic. (which in turn would add shitload of StatRegistry.register(...) lines, but would fix
 * the no-reference-not-initialized -problem which this system currently has)
 */
public final class StatVariable {
	private final IAttribute targetAttribute;

	/**
	 * Creates a new stat variable and a new attribute that is specifically bound to this variable.
	 * Result should be saved to a constant for easy access.
	 *
	 * @param unlocalizedName Unlocalized name of the attribute to create.
	 * @param defaultValue    Default value of the attribute.
	 * @param minValue        Minimum value of the attribute.
	 * @param maxValue        Maximum value of the attribute.
	 */
	public StatVariable(String unlocalizedName, float defaultValue, float minValue, float maxValue) {
		this(new RangedAttribute(null, unlocalizedName, defaultValue, minValue, maxValue), false);
	}

	/**
	 * Creates a new stat variable and binds it to given attribute.
	 * Result should be saved to a constant for easy access.
	 *
	 * @param attribute    Attribute to bind to.
	 * @param registered   Is the attribute registered to the entity. If false,
	 *                     attribute is automatically registered to the entity.
	 *                     If true, attribute must be manually registered. True
	 *                     should only be used for vanilla attributes or attributes
	 *                     on custom entities added by other mods.
	 */
	public StatVariable(IAttribute attribute, boolean registered) {
		this.targetAttribute = attribute;

		StatRegistry.register(this, registered);
	}

	public IAttribute getTarget() {
		return this.targetAttribute;
	}
}
