package kailari.rpgmod.api.common.stats;

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
	 * Gets value of given stat variable
	 *
	 * @param variable Variable which value to retrieve.
	 * @return Value of given variable
	 */
	float get(StatVariable variable);


	/**
	 * Sets value of given stat variable
	 *
	 * @param variable Variable which value to change
	 * @param value    The new value
	 */
	void set(StatVariable variable, float value);
}
