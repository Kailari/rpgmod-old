package kailari.rpgmod.api.client.actionlog;

import kailari.rpgmod.api.client.actionlog.entries.ActionLogEntryBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Provides features for logging and categorizing all sorts of actions.
 */
@SideOnly(Side.CLIENT)
public class ActionLog {

	private static final LinkedList<ActionLogEntryBase> logEntries = new LinkedList<ActionLogEntryBase>();

	public static void addEntry(ActionLogEntryBase entry) {
		logEntries.addFirst(entry);
	}

	public static List<ActionLogEntryBase> getEntries() {
		return logEntries;
	}

	public static List<ActionLogEntryBase> getEntriesNewerThan(long time) {
		ListIterator<ActionLogEntryBase> iterator = logEntries.listIterator();

		List<ActionLogEntryBase> result = new ArrayList<ActionLogEntryBase>();

		while (iterator.hasNext()) {
			ActionLogEntryBase entry = iterator.next();

			if (entry.getTimeCreated() >= time) {
				result.add(entry);
			}
		}

		return result;
	}
}
