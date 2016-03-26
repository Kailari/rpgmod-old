package kailari.rpgmod.api.client.actionlog.entries;

import kailari.rpgmod.api.common.stats.attributes.Attribute;

/**
 * Action log entry used to log attribute level-ups.
 * <p/>
 * FORMAT PARAMS:
 * <br/>
 * - %1$d -> level
 * - %2$d -> bonus levels
 * - %3$s -> attribute name
 */
public class EntryAttributeLevel extends ActionLogEntryBase {
	// TODO: .lang
	private static final String FORMAT = "Level up! %3$s is now lvl %1$d (+%2$d)";

	private final Attribute attribute;
	private final int level;
	private final int bonus;

	public EntryAttributeLevel(Attribute attribute, int level, int bonus) {
		this.attribute = attribute;
		this.level = level;
		this.bonus = bonus;
	}

	@Override
	public String getMessage() {
		return String.format(FORMAT, this.level, this.bonus, this.attribute.getDisplayName());
	}
}
