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
	 * Bulk list of all registered StatVariables
	 */
	public static List<StatVariable> getAll() {
		return new ArrayList<StatVariable>(variableRegistry);
	}

	/**
	 * List of stat variables that have vanilla attributes that need registering
	 */
	public static List<StatVariable> getVarsNeedingRegistering() {
		return new ArrayList<StatVariable>(needRegistering);
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Internals
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static final List<StatVariable> variableRegistry = new ArrayList<StatVariable>();
	private static final List<StatVariable> needRegistering = new ArrayList<StatVariable>();

	/**
	 * Adds a StatVariable to the registry, called automatically in StatVariable constructor
	 */
	protected static void register(StatVariable variable, boolean registered) {
		variableRegistry.add(variable);

		if (!registered) {
			needRegistering.add(variable);
		}
	}
}
