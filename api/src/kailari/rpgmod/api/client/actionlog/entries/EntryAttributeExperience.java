package kailari.rpgmod.api.client.actionlog.entries;

import kailari.rpgmod.api.common.stats.attributes.Attribute;

/**
 * Action log entry used to log attribute experience gains.
 * <p/>
 * FORMAT PARAMS:
 * <br/>
 * - %1$d -> amount of xp
 * - %2$s -> attribute name
 */
public class EntryAttributeExperience extends ActionLogEntryBase {

	// TODO: .lang
	private static final String FORMAT = "Gained +%1$d %2$s";

	private final Attribute attribute;
	private final int amount;

	public EntryAttributeExperience(Attribute attribute, int amount) {
		this.attribute = attribute;
		this.amount = amount;
	}

	@Override
	public String getMessage() {
		return String.format(FORMAT, this.amount, this.attribute.getDisplayName());
	}
}
