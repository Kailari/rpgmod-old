package kailari.rpgmod.api.common.stats.attributes;

import kailari.rpgmod.api.common.stats.ICharacterStats;
import net.minecraft.entity.Entity;

/**
 * Defines container capability for attributes. Same rules as for {@link ICharacterStats} apply here.
 * If that was not enough, here, have it again: <b>DO NOT MAKE YOUR OWN IMPLEMENTATIONS OF THIS INTERFACE</b>
 */
public interface ICharacterAttributes {

	/**
	 * Returns getLevel() + getBonus()
	 */
	int getTotalLevel(Attribute attribute);

	int getLevel(Attribute attribute);

	int getBonus(Attribute attribute);

	int getXP(Attribute attribute);


	void receiveXP(Attribute attribute, int amount);

	void set(Attribute attribute, int xp, int bonusLevels);

	Entity getPlayer();
}
