package kailari.rpgmod.api.common.stats;

import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

/**
 * Used to hook vanilla entity attribute modifiers to stat variables.
 * Usage differs a bit from vanilla attribute modifiers, as a new instance must be on per-player
 * basis. Easiest way is to just create new instance every time modifier is applied.
 */
public class StatAttributeModifier extends AttributeModifier {
	private final StatVariable targetVariable;
	private final ICharacterStats stats;


	public StatAttributeModifier(UUID uuid, String name, StatVariable variable, ICharacterStats stats, int operationIn) {
		super(uuid, name, 1.0, operationIn);

		// These should never be saved, as that could potentially break some parts of
		// the attribute/stat variable NBT I/O
		setSaved(false);

		this.targetVariable = variable;
		this.stats = stats;
	}

	@Override
	public double getAmount() {
		return this.stats.get(targetVariable);
	}
}
