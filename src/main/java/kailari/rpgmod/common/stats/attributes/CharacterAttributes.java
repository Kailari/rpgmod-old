package kailari.rpgmod.common.stats.attributes;

import kailari.rpgmod.api.common.actionlog.ActionLog;
import kailari.rpgmod.api.common.actionlog.entries.EntryAttributeLevel;
import kailari.rpgmod.api.common.stats.ICharacterStats;
import kailari.rpgmod.api.common.stats.StatVariable;
import kailari.rpgmod.api.common.stats.attributes.Attribute;
import kailari.rpgmod.api.common.stats.attributes.AttributeRegistry;
import kailari.rpgmod.api.common.stats.attributes.ICharacterAttributes;
import kailari.rpgmod.api.common.stats.attributes.link.IStatLink;
import kailari.rpgmod.common.Capabilities;
import kailari.rpgmod.util.CapHelper;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Storage for character attributes.
 */
public class CharacterAttributes implements ICharacterAttributes {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ICharacterAttributes
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void receiveXP(Attribute attribute, int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Tried to grant negative amount of xp!");
		}

		AttributeInstance instance = this.attributes.get(attribute);

		int levelBefore = instance.getLevel() + instance.getBonus();

		set(attribute, instance.getXP() + amount, instance.getBonus());

		int levelAfter = instance.getLevel() + instance.getBonus();

		if (levelAfter > levelBefore) {
			ActionLog.addEntry(new EntryAttributeLevel(
					attribute,
					instance.getLevel(),
					instance.getBonus()));
		}
	}


	@Override
	public void set(Attribute attribute, int xp, int bonusLevels) {
		// Get the desired attribute
		AttributeInstance instance = this.attributes.get(attribute);
		int newLevel = attribute.getLevel(xp);

		// Update attribute level. Modifiers to stats are applied here.
		updateLevel(
				attribute,
				this.stats,
				newLevel + bonusLevels,
				instance.getLevel() + instance.getBonus());

		instance.setLevel(newLevel);
		instance.setXP(xp);
		instance.setBonus(bonusLevels);

		// Update values in storage
		this.attributes.put(attribute, instance);
	}


	@Override
	public int getTotalLevel(Attribute attribute) {
		return this.getLevel(attribute) + this.getBonus(attribute);
	}

	@Override
	public int getLevel(Attribute attribute) {
		return this.attributes.get(attribute).getLevel();
	}

	@Override
	public int getBonus(Attribute attribute) {
		return this.attributes.get(attribute).getBonus();
	}

	@Override
	public int getXP(Attribute attribute) {
		return this.attributes.get(attribute).getXP();
	}

	@Override
	public Entity getTarget() {
		return this.target;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Internal implementation
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final Map<Attribute, AttributeInstance> attributes;
	private final Entity target;
	private final ICharacterStats stats;

	public CharacterAttributes(Entity entity) {
		this.attributes = new HashMap<Attribute, AttributeInstance>();
		this.target = entity;

		for (Attribute attribute : AttributeRegistry.getAll()) {
			this.attributes.put(attribute, new AttributeInstance(0, 0));
		}

		this.stats = CapHelper.getCapability(entity, Capabilities.CAPABILITY_STATS);

		if (this.stats == null) {
			throw new IllegalStateException("Tried to add attribute capabilities to an entity without stat capability!");
		}
	}

	private void updateLevel(Attribute attribute, ICharacterStats stats, int newLevel, int oldLevel) {
		// Don't change anything if level didn't change
		if (newLevel == oldLevel) {
			return;
		}

		// Apply all stat links
		for (Map.Entry<StatVariable, IStatLink> entry : attribute.getLinkedStats().entrySet()) {

			// Calculate how much the modifier should change
			float delta = calculateModifierDeltaBetweenLevels(entry.getValue(), oldLevel, newLevel);
			float value = stats.get(entry.getKey());

			if (newLevel > oldLevel) {
				value += delta;
			} else {
				value -= delta;
			}

			// Update stat value
			stats.set(entry.getKey(), value);
		}
	}

	private float calculateModifierDeltaBetweenLevels(IStatLink link, int from, int to) {
		float total = 0.0f;
		for (int level = Math.min(from, to); level <= Math.max(from, to); level++) {
			if (link.changesAtLevel(level)) {
				total += link.getModifier(level);
			}
		}

		return total;
	}
}
