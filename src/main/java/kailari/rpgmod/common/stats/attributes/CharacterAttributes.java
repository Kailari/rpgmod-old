package kailari.rpgmod.common.stats.attributes;

import kailari.rpgmod.api.common.stats.ICharacterStats;
import kailari.rpgmod.api.common.stats.StatVariable;
import kailari.rpgmod.api.common.stats.attributes.Attribute;
import kailari.rpgmod.api.common.stats.attributes.AttributeRegistry;
import kailari.rpgmod.api.common.stats.attributes.ICharacterAttributes;
import kailari.rpgmod.api.common.stats.attributes.link.IStatLink;
import kailari.rpgmod.common.Capabilities;
import kailari.rpgmod.common.networking.Netman;
import kailari.rpgmod.common.networking.messages.attributes.SyncAttributeMessage;
import kailari.rpgmod.common.networking.messages.attributes.SyncCharacterAttributesMessage;
import kailari.rpgmod.util.CapHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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

	private boolean firstSyncDone = false; // Syncing too early causes shitload of errors and won't work.

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
				newLevel + bonus);

		instance.setLevel(newLevel);
		instance.setXP(xp);
		instance.setBonus(bonus);

		return true;
	}

	private void updateLevel(Attribute attribute, ICharacterStats stats, int newLevel) {
		// Apply all stat links
		for (Map.Entry<StatVariable, IStatLink> entry : attribute.getLinkedStats().entrySet()) {

			// Calculate how much the modifier should change
			float amount = entry.getValue().getModifier(newLevel);

			// Remove existing modifier
			stats.removeModifier(entry.getKey(), attribute.getUUID());

			// Add new modifier
			stats.addModifier(
					entry.getKey(),
					new AttributeModifier(
							attribute.getUUID(),
							attribute.getUnlocalizedName(),
							amount,
							entry.getValue().getOperation()
					).setSaved(false));

			// TODO: NBT I/O -> read from vanilla attribute tags (verify that it happens correctly)
		}
	}

	private float calculateModifierDeltaBetweenLevels(IStatLink link, int from, int to) {
		float min = link.getModifier(Math.min(from, to));
		float max = link.getModifier(Math.max(from, to));

		return max - min;
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

		this.firstSyncDone = true;
	}

	public void syncAttribute(Attribute attribute) {
		if (this.player.worldObj.isRemote) {
			throw new IllegalStateException("syncAttribute should NEVER get called on remote!");
		}

		if (!this.firstSyncDone) {
			return;
		}

		Netman.channel_0.sendTo(
				new SyncAttributeMessage(
						attribute.getUnlocalizedName(),
						this.getXP(attribute),
						this.getBonus(attribute)),
				(EntityPlayerMP) this.player);
	}

	@SideOnly(Side.CLIENT)
	public void receiveAttributeData(Attribute attribute, int xp, int bonus) {
		setInternal(attribute, xp, bonus);
	}
}
