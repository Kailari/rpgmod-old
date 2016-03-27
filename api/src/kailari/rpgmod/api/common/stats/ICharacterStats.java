package kailari.rpgmod.api.common.stats;

import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

/**
 * Provides methods for storing and managing character stats. All stat variables are automatically
 * synchronized.
 * <p/>
 * Automatically initializes all stats to their default values upon player login. No need to do
 * any manual initialization. Yes, this is true even for any custom StatVariables. (see {@link StatVariable}
 * constructor for more details.)
 * <p/>
 * This interface is here just to provide access to the capability if needed. All logic behind
 * default StatVariables assumes that default implementation is used, thus making overriding it
 * a REALLY bad idea. So, yeah, don't do it. Really, just don't.
 */
public interface ICharacterStats {

	/**
	 * Gets the current value of given stat variable with modifiers applied.
	 *
	 * @param variable Variable which value to retrieve.
	 * @return Value of given variable
	 */
	float get(StatVariable variable);

	/**
	 * Returns the unmodified base-value of given stat variable.
	 *
	 * @param variable Variable which base-value to retrieve.
	 * @return Base value of given variable.
	 */
	float getBaseValue(StatVariable variable);

	/**
	 * Sets value of given stat variable
	 *
	 * @param variable Variable which value to change
	 * @param value    The new value
	 */
	void set(StatVariable variable, float value);

	/**
	 * Adds a new modifier to the given stat variable.
	 *
	 * @param variable Variable to which the modifier will be applied.
	 * @param modifier Modifier to add.
	 */
	void addModifier(StatVariable variable, AttributeModifier modifier);

	/**
	 * Removes a modifier from given stat variable.
	 * <br>Equivalent to calling <code>removeModifier(variable, modifier.getId())</code>
	 *
	 * @param variable Variable from which the modifier will be removed.
	 * @param modifier Modifier to remove.
	 */
	void removeModifier(StatVariable variable, AttributeModifier modifier);

	/**
	 * Removes a modifier from given stat variable.
	 *
	 * @param variable Variable from which the modifier will be removed.
	 * @param uuid     UUID of the modifier to remove.
	 */
	void removeModifier(StatVariable variable, UUID uuid);
}
