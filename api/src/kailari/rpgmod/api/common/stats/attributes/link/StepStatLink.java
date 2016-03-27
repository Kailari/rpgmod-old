package kailari.rpgmod.api.common.stats.attributes.link;

/**
 * IStatLink implementation that applies fixed size modifier at every n-th level.
 */
public class StepStatLink implements IStatLink {

	private final int stepSize;
	private final int firstStepLevel;
	private final float modifierPerStep;

	/**
	 * Creates a new stat link instance.
	 *
	 * @param stepSize           Size of the step. After first step level, modifier changes at every stepSize-th level.
	 * @param firstStepLevel     Level at which the first modifier change is applied.
	 * @param modifierPerStep    How much modifier should change at each step.
	 */
	public StepStatLink(int stepSize, int firstStepLevel, float modifierPerStep) {
		this.stepSize = stepSize;
		this.firstStepLevel = firstStepLevel;
		this.modifierPerStep = modifierPerStep;
	}

	@Override
	public int getOperation() {
		return 0; // Additive operation
	}

	@Override
	public boolean changesAtLevel(int level) {
		level -= this.firstStepLevel;
		return level >= 0 && ((level % this.stepSize) == 0);
	}

	@Override
	public float getModifier(int level) {
		float modifier = 0.0f;
		for (int i = 0; i < level; i++) {
			if (this.changesAtLevel(i)) {
				modifier += this.modifierPerStep;
			}
		}

		return modifier;
	}
}
