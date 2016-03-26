package kailari.rpgmod.api.client.actionlog.entries;

import net.minecraft.item.ItemStack;

/**
 * Action log entry used to log gained items.
 * <p/>
 * FORMAT PARAMS:
 * <br/>
 * - %1$s -> item name
 * - %2$d -> stack size
 */
public class EntryItemAdded extends ActionLogEntryBase {

	private final ItemAddType actionType;
	private final ItemStack stack;

	public EntryItemAdded(ItemStack stack) {
		this(ItemAddType.ADD_ITEM, stack);
	}

	public EntryItemAdded(ItemAddType actionType, ItemStack stack) {
		this.actionType = actionType;
		this.stack = stack;
	}

	@Override
	public String getMessage() {
		return String.format(actionType.format, stack.getDisplayName(), stack.stackSize);
	}

	public enum ItemAddType {
		// TODO: .lang-ify

		/**
		 * Notifies of new picked up item player didn't previously have in inventory.
		 */
		PICK_ITEM_NEW("Picked up new item(s): %1$s x %2$d"),

		/**
		 * Notifies of new crafted item player didn't previously have in inventory.
		 */
		CRAFT_ITEM_NEW("Crafted new item(s): %1$s x %2$d"),

		/**
		 * Generic item added. Default.
		 */
		ADD_ITEM("+%2$d %1$s");

		private final String format;

		ItemAddType(String format) {
			this.format = format;
		}
	}
}
