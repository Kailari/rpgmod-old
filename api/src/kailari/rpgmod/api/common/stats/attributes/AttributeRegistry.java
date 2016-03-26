package kailari.rpgmod.api.common.stats.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Storage and access for attributes
 */
public class AttributeRegistry {

	public static List<Attribute> getAll() {
		return new ArrayList<Attribute>(attributeRegistry.values());
	}

	public static Attribute get(String key) {
		return attributeRegistry.get(key);
	}

	public static int getCount() {
		return attributeRegistry.size();
	}


	private static final Map<String, Attribute> attributeRegistry = new HashMap<String, Attribute>();

	protected static void register(Attribute attribute) {
		attributeRegistry.put(attribute.getNBTKey(), attribute);
	}
}
