package kailari.rpgmod.common.stats.attributes;

import kailari.rpgmod.api.client.actionlog.ActionLog;
import kailari.rpgmod.api.client.actionlog.entries.EntryAttributeExperience;
import kailari.rpgmod.api.client.actionlog.entries.EntryAttributeLevel;
import kailari.rpgmod.api.common.stats.ICharacterStats;
import kailari.rpgmod.api.common.stats.StatVariable;
import kailari.rpgmod.api.common.stats.attributes.Attribute;
import kailari.rpgmod.api.common.stats.attributes.AttributeRegistry;
import kailari.rpgmod.api.common.stats.attributes.ICharacterAttributes;
import kailari.rpgmod.api.common.stats.attributes.link.IStatLink;
import kailari.rpgmod.common.Capabilities;
import kailari.rpgmod.common.networking.Netman;
import kailari.rpgmod.common.networking.messages.stats.attributes.SyncAttributeMessage;
import kailari.rpgmod.common.networking.messages.stats.attributes.SyncCharacterAttributesMessage;
import kailari.rpgmod.util.CapHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		set(attribute, instance.getXP() + amount, instance.getBonus());
	}


	@Override
	public void set(Attribute attribute, int xp, int bonus) {
		if (this.setInternal(attribute, xp, bonus) && !this.getPlayer().worldObj.isRemote) {
			syncAttribute(attribute);
		}
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

	public Entity getPlayer() {
		return this.player;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Internal implementation
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final Map<Attribute, AttributeInstance> attributes;
	private final EntityPlayer player;

	public CharacterAttributes(EntityPlayer player) {
		this.attributes = new HashMap<Attribute, AttributeInstance>();
		this.player = player;

		for (Attribute attribute : AttributeRegistry.getAll()) {
			this.attributes.put(attribute, new AttributeInstance(0, 0));
		}
	}

	private boolean setInternal(Attribute attribute, int xp, int bonus) {
		// Get the desired attribute
		AttributeInstance instance = this.attributes.get(attribute);

		int oldXp = instance.getXP();
		if (oldXp == xp && instance.getBonus() == bonus) {
			return false;
		}

		int newLevel = attribute.getLevel(xp);

		// Update attribute level. Modifiers to stats are applied here.
		updateLevel(
				attribute,
				CapHelper.getCapability(player, Capabilities.STATS),
				newLevel + bonus,
				instance.getLevel() + instance.getBonus());

		instance.setLevel(newLevel);
		instance.setXP(xp);
		instance.setBonus(bonus);

		// Update values in storage
		//this.attributes.put(attribute, instance); XXX: This should be unnecessary

		return true;
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


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Networking
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void doFullSync() {
		if (this.player.worldObj.isRemote) {
			throw new IllegalStateException("doFullSync should NEVER get called on remote!");
		}

		// Send all variables and new random seed to the client
		Netman.channel_0.sendTo(new SyncCharacterAttributesMessage(this), (EntityPlayerMP) this.player);
	}

	public void syncAttribute(Attribute attribute) {
		if (this.player.worldObj.isRemote) {
			throw new IllegalStateException("syncAttribute should NEVER get called on remote!");
		}

		Netman.channel_0.sendTo(
				new SyncAttributeMessage(
						attribute.getNBTKey(),
						this.getXP(attribute),
						this.getBonus(attribute)),
				(EntityPlayerMP) this.player);
	}

	@SideOnly(Side.CLIENT)
	public void receiveAttributeData(Attribute attribute, int xp, int bonus) {
		setInternal(attribute, xp, bonus);
	}
}
