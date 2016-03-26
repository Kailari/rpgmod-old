package kailari.rpgmod.api.common.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for storing StatVariables
 */
public final class StatRegistry {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Public interface
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Searches for StatVariable registered with given key.
	 *
	 * @param key key to look for
	 * @return Corresponding StatVariable if found, null otherwise.
	 */
	public static StatVariable get(String key) {
		return variableRegistry.get(key);
	}

	/**
	 * Bulk list of all registered StatVariables
	 */
	public static List<StatVariable> getAll() {
		return new ArrayList<StatVariable>(variableRegistry.values());
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Internals
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static final Map<String, StatVariable> variableRegistry = new HashMap<String, StatVariable>();

	/**
	 * Adds a StatVariable to the registry, called automatically in StatVariable constructor
	 */
	protected static void register(StatVariable variable) {
		variableRegistry.put(variable.getNBTKey(), variable);
	}
}
