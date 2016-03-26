package kailari.rpgmod.common.stats.attributes;

/**
 * Instantiated, stateful, version of an attribute.
 */
class AttributeInstance {
	private int currentXP;
	private int currentLevel;

	/**
	 * Levels gained from perks etc. These don't affect xp calculations, but give bonuses anyhow.
	 */
	private int bonusLevels;

	public int getXP() {
		return currentXP;
	}

	public int getLevel() {
		return currentLevel;
	}

	public int getBonus() {
		return bonusLevels;
	}

	public void setXP(int currentXP) {
		this.currentXP = currentXP;
	}

	public void setLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	public void setBonus(int bonusLevels) {
		this.bonusLevels = bonusLevels;
	}

	public AttributeInstance(int currentXP, int currentLevel) {
		this.currentXP = currentXP;
		this.currentLevel = currentLevel;
		this.bonusLevels = 0;
	}
}
