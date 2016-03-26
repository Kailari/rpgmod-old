package kailari.rpgmod.api.common.stats.attributes.xp;

import kailari.rpgmod.api.common.stats.attributes.Attribute;
import kailari.rpgmod.api.common.stats.attributes.ICharacterAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes behavior of an attribute XP source
 */
public class AttributeXPSource {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Public interface
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Grants default amounts of xp from this source to character's attributes.
	 *
	 * @param attributes Attributes to grant the XP to.
	 */
	public void grantXP(ICharacterAttributes attributes) {
		for (LinkedAttribute linked : this.linkedAttributes) {
			this.grantXP(attributes, linked.attribute, linked.defaultAmount);
		}
	}

	/**
	 * Grants given amount of xp from this source to character's attributes.
	 *
	 * @param attributes Attributes to grant the XP to.
	 * @param amount     How much xp should be granted if default amount is not forced.
	 */
	public void grantXP(ICharacterAttributes attributes, int amount) {
		for (LinkedAttribute linked : this.linkedAttributes) {
			// Determine amount of xp granted
			amount = linked.forceDefaultValue ? linked.defaultAmount : Math.round(amount * linked.multiplier);

			this.grantXP(attributes, linked.attribute, amount);
		}
	}

	/**
	 * Links this source to an attribute.
	 *
	 * @param attribute         Attribute to link to
	 * @param defaultAmount     Default amount of xp to grant
	 * @param multiplier        Multiplier to use if amount is specified
	 * @param forceDefaultValue Ignore all amounts and always use the default amount
	 */
	public void linkAttribute(Attribute attribute, int defaultAmount, float multiplier, boolean forceDefaultValue) {
		this.linkedAttributes.add(new LinkedAttribute(attribute, forceDefaultValue, defaultAmount, multiplier));
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Internal implementation
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final List<LinkedAttribute> linkedAttributes;

	public AttributeXPSource() {
		this.linkedAttributes = new ArrayList<LinkedAttribute>();
	}

	private void grantXP(ICharacterAttributes attributes, Attribute attribute, int amount) {
		attributes.receiveXP(attribute, amount);
	}


	private static class LinkedAttribute {
		private final int defaultAmount;
		private final float multiplier;
		private final boolean forceDefaultValue;
		private final Attribute attribute;

		public LinkedAttribute(Attribute attribute, boolean forceDefaultAmount, int defaultAmount, float multiplier) {
			this.attribute = attribute;
			this.forceDefaultValue = forceDefaultAmount;
			this.defaultAmount = defaultAmount;
			this.multiplier = multiplier;
		}
	}
}
