package kailari.rpgmod.api.common.actionlog.entries;

/**
 * Action log entry. Has age and can be output to string.
 */
public abstract class ActionLogEntryBase implements Comparable<ActionLogEntryBase> {

	private static final long ENTRY_MAX_AGE = 5000L; // TODO: Replace with configurable variable

	public abstract String getMessage();

	private final long timeCreated;

	public ActionLogEntryBase() {
		this.timeCreated = System.currentTimeMillis();
	}

	public boolean hasExpired(long time) {
		return time > this.timeCreated + ENTRY_MAX_AGE;
	}

	@Override
	public int compareTo(ActionLogEntryBase o) {
		if (this.timeCreated < o.timeCreated) {
			return -1;
		} else if (this.timeCreated > o.timeCreated) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return this.getMessage();
	}

	public long getTimeCreated() {
		return this.timeCreated;
	}
}
