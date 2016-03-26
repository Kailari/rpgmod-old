package kailari.rpgmod.common.stats;

import kailari.rpgmod.RPGMod;
import kailari.rpgmod.api.common.stats.ICharacterStats;
import kailari.rpgmod.api.common.stats.StatAttributeModifier;
import kailari.rpgmod.api.common.stats.Stats;
import kailari.rpgmod.api.common.stats.attributes.ICharacterAttributes;
import kailari.rpgmod.api.common.stats.attributes.xp.AttributeXPSource;
import kailari.rpgmod.api.common.stats.attributes.xp.AttributeXPSources;
import kailari.rpgmod.common.Capabilities;
import kailari.rpgmod.common.event.RPGModEventFactory;
import kailari.rpgmod.common.stats.attributes.CapabilityCharacterAttributes;
import kailari.rpgmod.common.stats.attributes.CharacterAttributes;
import kailari.rpgmod.util.CapHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.UUID;

/**
 * Listens to stat-related events and applies modifiers according to characters' current stats
 * TODO: Rename to something bit more describing
 */
public class StatsEventHandler {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Constants
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Radius in blocks inside which to grant players near the brewing stand XP
	 */
	private static final int BREWING_STAND_XP_RADIUS = 8;

	/**
	 * Player needs to walk this many meters to gain xp from walking
	 */
	private static final int WALKING_XP_GRANT_DISTANCE = 100;

	/**
	 * Player needs to sprint this many meters to gain xp from sprinting
	 */
	private static final int SPRINTING_XP_GRANT_DISTANCE = 100;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Player stat injection (RELIES ON DEFAULT IMPLEMENTATION)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.getEntity() instanceof EntityPlayer) {
			
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			injectFoodStats(player);
			
			injectEntityAttributeChanges(player);
			
			tryDoSync(player);
		}
	}
	
	private void injectFoodStats(EntityPlayer player) {
		/*
			-- Inject modified FoodStats to player entity --

			This is a really dirty way of modifying vanilla logic,
			and probably breaks compatibility to any other mods that
			affect food logic in any way, but as there are no hooks
			or whatsoever in FoodStats, there just is no other clean
			option.

			Basically what this does, it simply takes copy-paste
			version of the FoodStats with some variables added and
			change EntityPlayer's foodStats-field value to point to
			the modified one. As the desired field has protected
			access in EntityPlayer, accessing the setter has to be
			done via reflection.
		 */
		
		FoodStats foodStats = new FoodStats(player, player.getFoodStats());
		
		// EntityPlayer.foodStats => field_71100_bB
		try {
			ObfuscationReflectionHelper.setPrivateValue(
					EntityPlayer.class,
					player,
					foodStats,
					"foodStats", "field_71100_bB");
			
			RPGMod.logger.warn("MODIFIED VERSION OF CLASS \"FoodStats\" SUCCESSFULLY INJECTED!");
		} catch (Exception e) {
			RPGMod.logger.error("\"FoodStats\" INJECTION FAILED!");
		}
	}
	
	private void injectEntityAttributeChanges(EntityPlayer player) {
		ICharacterStats stats = CapHelper.getCapability(player, Capabilities.CAPABILITY_STATS);
		
		if (stats != null) {
			player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
					.setBaseValue(stats.get(Stats.HP_MAX));
		}
	}
	
	private void tryDoSync(EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			CharacterStats stats = (CharacterStats) CapHelper.getCapability(player, Capabilities.CAPABILITY_STATS);
			
			if (stats != null) {
				stats.doFullSync();

				CharacterAttributes attrs = (CharacterAttributes) CapHelper.getCapability(player, Capabilities.CAPABILITY_ATTRIBUTES);

				if (attrs != null) {
					attrs.doFullSync();
				}
			}
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Stat/Attribute handling events
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	PlayerEvent.Clone
//  -----------------
//	Handles copying player stats and attributes over to the clone so that stats persist over deaths.
//
//	Handles following XP sources:
//	 - Misc.RESPAWNING
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		// Only handle these on the server
		if (event.getEntity().worldObj.isRemote) {
			return;
		}
		
		// Get stats for copying
		ICharacterStats originalStats = CapHelper.getCapability(event.getOriginal(), Capabilities.CAPABILITY_STATS);
		ICharacterStats cloneStats = CapHelper.getCapability(event.getEntityPlayer(), Capabilities.CAPABILITY_STATS);
		
		if (originalStats != null && cloneStats != null) {
			
			NBTTagCompound compound = new NBTTagCompound();
			
			// Copy stats to the clone
			CapabilityCharacterStats.writeToNBT(compound, originalStats);
			CapabilityCharacterStats.readFromNBT(compound, cloneStats);


			// Copy attributes to the clone
			ICharacterAttributes originalAttrs = CapHelper.getCapability(event.getOriginal(), Capabilities.CAPABILITY_ATTRIBUTES);
			ICharacterAttributes cloneAttrs = CapHelper.getCapability(event.getOriginal(), Capabilities.CAPABILITY_ATTRIBUTES);
			
			if (originalAttrs != null && cloneAttrs != null) {
				
				// Reset the compound
				compound = new NBTTagCompound();

				CapabilityCharacterAttributes.writeToNBT(compound, originalAttrs);
				CapabilityCharacterAttributes.readFromNBT(compound, cloneAttrs);
				
				if (event.isWasDeath()) {
					// Why the fuck not? :D
					AttributeXPSources.Misc.RESPAWNING.grantXP(cloneAttrs);
				}
			}
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	LivingHurtEvent
//  ---------------
//	Handles applying damage multipliers from stats. Automagically determines whether to
//	use ranged or melee multiplier. Applies damage amount as gained xp amount.
//
//  TODO: Apply armor modifiers (could possibly be applied as special armor too, instead of applying here)
//
//	Executes at EventPriority.LOWEST to ensure damage amount we get is the final value.
//
//	Handles following XP sources:
//	 - Damage.DEALING
//	 - Damage.DEALING_MELEE
//	 - Damage.DEALING_RANGED
//	 - Damage.TAKING
//	 - Damage.TAKING_MELEE
//	 - Damage.TAKING_RANGED
//	 - Damage.BLOCKING
//	 - Damage.BLOCKING_MELEE
//	 - Damage.BLOCKING_RANGED
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingHurt(LivingHurtEvent event) {
		// Only handle these on the server
		if (event.getEntity().worldObj.isRemote) {
			return;
		}
		
		Entity attackerEntity = event.getSource().getSourceOfDamage();
		if (attackerEntity != null && attackerEntity instanceof EntityPlayer) {

			EntityPlayer attacker = (EntityPlayer) attackerEntity;

			// Make sure that damage source entity has stat capabilities
			ICharacterStats attackerStats = CapHelper.getCapability(attacker, Capabilities.CAPABILITY_STATS);
			
			if (attackerStats != null) {
				
				ICharacterAttributes attackerAttrs = CapHelper.getCapability(attacker, Capabilities.CAPABILITY_ATTRIBUTES);
				ICharacterAttributes targetAttrs = null;
				
				ICharacterStats targetStats = CapHelper.getCapability(event.getEntity(), Capabilities.CAPABILITY_STATS);
				if (targetStats != null) {
					targetAttrs = CapHelper.getCapability(event.getEntity(), Capabilities.CAPABILITY_ATTRIBUTES);
				}
				
				// Ranged damage
				if (event.getSource() instanceof EntityDamageSourceIndirect) {
					event.setAmount(event.getAmount() * attackerStats.get(Stats.DAMAGE_MULT_RANGED));
					
					handleLivingHurtXPRanged(event, attackerAttrs, targetAttrs, event.getAmount());
				}
				// Most likely melee damage
				else /* (event.source instanceof EntityDamageSource) */ {
					// TODO: Should damage from thorns behave differently? ((EntityDamageSource)(event.source).getIsThornsDamage())
					event.setAmount(event.getAmount() * attackerStats.get(Stats.DAMAGE_MULT_MELEE));
					
					handleLivingHurtXPMelee(event, attackerAttrs, targetAttrs, event.getAmount());
				}
				
				// Handle granting XP if necessary
				handleLivingHurtXPGeneric(event, attackerAttrs, targetAttrs, event.getAmount());
			}
		}
	}
	
	private void handleLivingHurtXPGeneric(LivingHurtEvent event, ICharacterAttributes attackerAttrs, ICharacterAttributes targetAttrs, float amount) {
		// If attacker has attribute capabilities, grant XP
		if (attackerAttrs != null) {
			AttributeXPSources.Damage.DEALING.grantXP(attackerAttrs, damageAmountToXP(amount));
		}
		
		// If target has attribute capabilities, grant XP
		if (targetAttrs != null) {
			AttributeXPSources.Damage.TAKING.grantXP(targetAttrs, damageAmountToXP(amount));
			
			// If target is blocking, grant XP
			if (event.getEntityLiving().isActiveItemStackBlocking()) {
				AttributeXPSources.Damage.BLOCKING.grantXP(targetAttrs, damageAmountToXP(amount));
			}
		}
	}
	
	private void handleLivingHurtXPRanged(LivingHurtEvent event, ICharacterAttributes attackerAttrs, ICharacterAttributes targetAttrs, float amount) {
		// If attacker has attribute capabilities, grant XP
		if (attackerAttrs != null) {
			AttributeXPSources.Damage.DEALING_RANGED.grantXP(attackerAttrs, damageAmountToXP(amount));
		}
		
		// If target has attribute capabilities, grant XP
		if (targetAttrs != null) {
			AttributeXPSources.Damage.TAKING_RANGED.grantXP(targetAttrs, damageAmountToXP(amount));
			
			// If target is blocking, grant XP
			if (event.getEntityLiving().isActiveItemStackBlocking()) {
				RPGMod.logger.info("Damage should always be at zero at this point! Damage: " + event.getAmount());
				AttributeXPSources.Damage.BLOCKING_RANGED.grantXP(targetAttrs, damageAmountToXP(amount));
			}
		}
	}
	
	private void handleLivingHurtXPMelee(LivingHurtEvent event, ICharacterAttributes attackerAttrs, ICharacterAttributes targetAttrs, float amount) {
		// If attacker has attribute capabilities, grant XP
		if (attackerAttrs != null) {
			AttributeXPSources.Damage.DEALING_MELEE.grantXP(attackerAttrs, damageAmountToXP(amount));
		}
		
		// If target has attribute capabilities, grant XP
		if (targetAttrs != null) {
			AttributeXPSources.Damage.TAKING_MELEE.grantXP(targetAttrs, damageAmountToXP(amount));
			
			// If target is blocking, grant XP
			if (event.getEntityLiving().isActiveItemStackBlocking()) {
				AttributeXPSources.Damage.BLOCKING_MELEE.grantXP(targetAttrs, damageAmountToXP(amount));
			}
		}
	}
	
	private int damageAmountToXP(float amount) {
		return Math.round(amount);
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	LivingAttackEvent
//  -----------------
//	Handles applying evasion and miss-chance to attacks.
//
//	RELIES ON DEFAULT IMPLEMENTATION
//
//	Handles following XP sources:
//	 - Damage.DODGING
//	 - Damage.DODGING_MELEE
//	 - Damage.DODGING_RANGED
//	 - Damage.MISSING
//	 - Damage.MISSING_MELEE
//	 - Damage.MISSING_RANGED
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// TODO: refactor/clean this. This method looks just horrifying.

	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event) {
		Entity entity = event.getSource().getSourceOfDamage();
		if (entity != null && entity instanceof EntityPlayer) {
			
			// Make sure that damage source entity has stat capabilities
			ICharacterStats attackerStats = CapHelper.getCapability(entity, Capabilities.CAPABILITY_STATS);
			
			if (attackerStats != null) {

				float targetEvasion = 0.0f;
				
				// If targeted entity has stat capabilities, adjust evasion accordingly
				ICharacterStats targetStats = CapHelper.getCapability(event.getEntity(), Capabilities.CAPABILITY_STATS);
				
				if (targetStats != null) {
					targetEvasion = targetStats.get(Stats.EVASION);
				}
				
				// Cancel event if attack missed
				if (((CharacterStats) attackerStats).doesAttackMiss(event.getSource(), targetEvasion)) {

					// Get amount of xp to grant
					int amount = Math.round(event.getAmount());

					// Grant dodging exp to target
					ICharacterAttributes targetAttrs = CapHelper.getCapability(event.getEntity(), Capabilities.CAPABILITY_ATTRIBUTES);
					grantTargetDodgingXP(targetAttrs, event.getSource(), amount);
					
					// Grant missing xp to attacker
					ICharacterAttributes attackerAttrs = CapHelper.getCapability(entity, Capabilities.CAPABILITY_ATTRIBUTES);
					grantAttackerMissingXP(attackerAttrs, event.getSource(), amount);
					
					RPGMod.logger.info("Attack missed!");

					// Only add messages on players' missed attacks
					if (entity.worldObj.isRemote) {
						entity.addChatMessage(new TextComponentString("Attack missed!"));
					}
					event.setCanceled(true);

					float totalEvasion = attackerStats.get(Stats.MISS_CHANCE) + targetEvasion;
					RPGModEventFactory.onPlayerAttackMissed((EntityPlayer) entity, event.getEntity(), totalEvasion, event.getAmount());
				}
			}
		}
	}

	private void grantTargetDodgingXP(ICharacterAttributes targetAttrs, DamageSource source, int amount) {
		if (targetAttrs != null && !targetAttrs.getPlayer().worldObj.isRemote) {
			AttributeXPSources.Damage.DODGING.grantXP(targetAttrs, amount);

			if (source instanceof EntityDamageSourceIndirect) {
				AttributeXPSources.Damage.DODGING_RANGED.grantXP(targetAttrs, amount);
			} else {
				AttributeXPSources.Damage.DODGING_MELEE.grantXP(targetAttrs, amount);
			}
		}
	}

	private void grantAttackerMissingXP(ICharacterAttributes attackerAttrs, DamageSource source, int amount) {
		if (attackerAttrs != null && !attackerAttrs.getPlayer().worldObj.isRemote) {
			AttributeXPSources.Damage.MISSING.grantXP(attackerAttrs, amount);

			if (source instanceof EntityDamageSourceIndirect) {
				AttributeXPSources.Damage.MISSING_RANGED.grantXP(attackerAttrs, amount);
			} else {
				AttributeXPSources.Damage.MISSING_MELEE.grantXP(attackerAttrs, amount);
			}
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	LivingExperienceDropEvent
//  -------------------------
//	Handles granting XP upon kills. Applies dropped exp amounts as granted XP amount.
//
//	Executes at EventPriority.LOWEST to ensure that the experience value we get is the final experience value.
//
//	Handles following XP sources:
//	 - Killing.KILLING
//	 - Killing.ANIMALS
//	 - Killing.MONSTERS
//	 - Killing.VILLAGERS
//	 - Killing.PLAYERS
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingExperienceDrop(LivingExperienceDropEvent event) {
		// Only handle these on the server
		if (event.getEntity().worldObj.isRemote) {
			return;
		}
		
		EntityPlayer player = event.getAttackingPlayer();
		
		// We can't add xp to player if it doesn't support attributes
		ICharacterAttributes attackerAttributes = CapHelper.getCapability(player, Capabilities.CAPABILITY_ATTRIBUTES);
		
		if (attackerAttributes != null) {
			
			// Grant generic killing XP
			AttributeXPSources.Killing.KILLING.grantXP(attackerAttributes, event.getDroppedExperience());
			
			// Determine what we killed and grant XP
			AttributeXPSource xpSource = getKillingXPSourceByEntityType(event.getEntityLiving());
			
			if (xpSource != null) {
				xpSource.grantXP(attackerAttributes, event.getDroppedExperience());
			}
		}
	}

	private AttributeXPSource getKillingXPSourceByEntityType(EntityLivingBase entity) {
		if (entity instanceof EntityAnimal || entity instanceof EntityWaterMob || entity instanceof EntityAmbientCreature) {
			return AttributeXPSources.Killing.ANIMALS;
		} else if (entity instanceof EntityMob || entity instanceof EntityGolem) {
			return AttributeXPSources.Killing.MONSTERS;
		} else if (entity instanceof EntityVillager) {
			return AttributeXPSources.Killing.VILLAGERS;
		} else if (entity instanceof EntityPlayer) {
			return AttributeXPSources.Killing.PLAYERS;
		} else {
			RPGMod.logger.warn("Could not find suitable xp source for killing: "
					+ entity.getClass().getSimpleName());
			return null;
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	BlockEvent.BreakEvent
//  ---------------------
//	Handles granting XP after breaking blocks.
//
//	Executes at EventPriority.LOWEST to ensure that the event isn't cancelled and block really gets destroyed.
//
//	Handles following XP sources:
//	 - Mining.BREAKING_BLOCKS
//	 - Mining.DIGGING
//	 - Mining.MINING
//	 - Mining.SHOVELING
//	 - Mining.WOODCUTTING
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// TODO: Interface for custom block XP, so that block doesn't have to drop vanilla xp to define attribute xp

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBreakBlock(BlockEvent.BreakEvent event) {
		// Only handle these on the server
		if (event.getPlayer().worldObj.isRemote) {
			return;
		}
		
		EntityPlayer player = event.getPlayer();
		
		ICharacterAttributes attributes = CapHelper.getCapability(player, Capabilities.CAPABILITY_ATTRIBUTES);
		
		if (attributes != null) {

			handleBlockBreakXP(attributes, event);

			handleHarvestingXP(attributes, event);
		}
	}

	private void handleBlockBreakXP(ICharacterAttributes attributes, BlockEvent.BreakEvent event) {
		int amount = event.getExpToDrop();

		// Grant generic block-breaking xp
		AttributeXPSources.Mining.BREAKING_BLOCKS.grantXP(attributes, amount);

		// Get the currently equipped itemStack in active hand
		ItemStack equippedStack = event.getPlayer().getActiveItemStack();

		// Make sure there is a item equipped
		if (equippedStack != null) {

			// Make sure equipped item is a tool
			if (equippedStack.getItem() instanceof ItemTool) {

				// Determine targeted block's effective harvest-tool and check if it is
				// effective for breaking the block.
				ItemTool tool = (ItemTool) equippedStack.getItem();
				String harvestTool = event.getState().getBlock().getHarvestTool(event.getState());
				if (tool.getToolClasses(equippedStack).contains(harvestTool)) {

					// Determine what tool was used and grant xp accordingly
					if (harvestTool.equalsIgnoreCase("pickaxe")) {
						AttributeXPSources.Mining.MINING.grantXP(attributes, amount);
					} else if (harvestTool.equalsIgnoreCase("axe")) {
						AttributeXPSources.Mining.WOODCUTTING.grantXP(attributes, amount);
					} else if (harvestTool.equalsIgnoreCase("shovel")) {
						AttributeXPSources.Mining.SHOVELING.grantXP(attributes, amount);
					}
				}
			}
		}
		// Breaking the block by hand, grant digging xp
		else {
			AttributeXPSources.Mining.DIGGING.grantXP(attributes, amount);
		}
	}

	private void handleHarvestingXP(ICharacterAttributes attributes, BlockEvent.BreakEvent event) {
		if (attributes.equals(event)) { // suppress warnings
			System.out.println("wow, something REALLY messed up is going on.");
		}
		// TODO: Create interface and helper class for determining harvesting xp
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	PlayerEvent.BreakSpeed
//  ----------------------
//	Handles modifying block breaking speed based on player stats.
//
//	Does not handle any XP sources.
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SubscribeEvent
	public void onPlayerEvent(PlayerEvent.BreakSpeed event) {
		
		// We can't apply stats if they don't exist
		ICharacterStats stats = CapHelper.getCapability(event.getEntity(), Capabilities.CAPABILITY_STATS);
		if (stats != null) {
			
			ItemStack equippedStack = event.getEntityPlayer().getActiveItemStack();
			float bonus = stats.get(Stats.MINING_SPEED_BONUS);
			float multiplier = stats.get(Stats.MINING_SPEED_MULT);
			
			// Check if we have anything equipped
			if (equippedStack != null) {
				
				// Check if item we are holding is a tool
				if (equippedStack.getItem() instanceof ItemTool) {
					
					// Figure out what kind of tool the item is
					ItemTool tool = (ItemTool) equippedStack.getItem();
					
					// Figure out what is effective tool for block we are going to mine
					String harvestTool = event.getState().getBlock().getHarvestTool(event.getState());
					
					// Check if currently equipped tool is effective for block we are mining
					// and apply bonus and multiplier accordingly.
					if (tool.getToolClasses(equippedStack).contains(harvestTool)) {
						if (harvestTool.equalsIgnoreCase("pickaxe")) {
							bonus += stats.get(Stats.MINING_SPEED_BONUS_PICK);
							multiplier += stats.get(Stats.MINING_SPEED_MULT_PICK);
						} else if (harvestTool.equalsIgnoreCase("axe")) {
							bonus += stats.get(Stats.MINING_SPEED_BONUS_AXE);
							multiplier += stats.get(Stats.MINING_SPEED_MULT_AXE);
						} else if (harvestTool.equalsIgnoreCase("shovel")) {
							bonus += stats.get(Stats.MINING_SPEED_BONUS_SHOVEL);
							multiplier += stats.get(Stats.MINING_SPEED_MULT_SHOVEL);
						}
					}
				}
			}
			// Nothing was equipped, we are digging by hand.
			else {
				bonus += stats.get(Stats.MINING_SPEED_BONUS_HAND);
				multiplier += stats.get(Stats.MINING_SPEED_MULT_HAND);
			}
			
			// Apply the bonus and the multiplier
			event.setNewSpeed((event.getNewSpeed() + bonus) * multiplier);
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	AnvilRepairEvent
//  ----------------
//	Handles granting XP from using anvil.
//
//	Handles following XP sources:
//	 - Forging.FORGING
//	 - Forging.TOOLS
//	 - Forging.WEAPONS
//	 - Forging.ARMOR
//	 - Forging.BOOKS
//	 - Forging.REPAIRING
//	 - Forging.ENCHANTING
//	 - Forging.NAMING
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// TODO: Refactor & clean, this thing is almost unreadable

	@SubscribeEvent
	public void onAnvilUsed(AnvilRepairEvent event) {
		// Only handle these on the server
		if (event.getEntity().worldObj.isRemote) {
			return;
		}

		ICharacterStats stats = CapHelper.getCapability(event.getEntity(), Capabilities.CAPABILITY_STATS);
		if (stats == null) {
			return;
		}

		// Modify anvil break chance based on stats
		event.setBreakChance((event.getBreakChance() + stats.get(Stats.ANVIL_BREAK_CHANCE_BONUS)) * stats.get(Stats.ANVIL_BREAK_CHANCE_MULT));

		// Grant xp from anvil operating if attributes capability is available
		ICharacterAttributes attributes = CapHelper.getCapability(event.getEntity(), Capabilities.CAPABILITY_ATTRIBUTES);
		if (attributes == null) {
			return;
		}

		// Grant generic anvil usage xp
		AttributeXPSources.Forging.FORGING.grantXP(attributes);


		Item item = event.getOutput().getItem();

		// Determine if we are working on tools or weapons
		if (item.isItemTool(event.getOutput())) {

			// Assume that all weapons are either swords or bows
			// TODO: Create interfaces IWeapon and ITool so that custom items can easily be distinguished
			if (item instanceof ItemSword || item instanceof ItemBow) {
				AttributeXPSources.Forging.WEAPONS.grantXP(attributes);
			} else {
				AttributeXPSources.Forging.TOOLS.grantXP(attributes);
			}
		}
		// Are we working on an piece of armor?
		else if (isItemArmor(event.getOutput(), item)) {
			AttributeXPSources.Forging.ARMOR.grantXP(attributes);
		}


		// Combining enchanted books
		if (item == Items.enchanted_book) {
			AttributeXPSources.Forging.BOOKS.grantXP(attributes);
		}
		// Repairing
		else if (event.getOutput().getItemDamage() < event.getLeft().getItemDamage()) {
			AttributeXPSources.Forging.REPAIRING.grantXP(attributes);
		}
		// Applying enchantments
		else if (event.getRight().getItem() == Items.enchanted_book) {
			AttributeXPSources.Forging.ENCHANTING.grantXP(attributes);
		}
		// Naming
		else if (event.getLeft().hasDisplayName() != event.getOutput().hasDisplayName()
				|| !event.getLeft().getDisplayName().equals(event.getOutput().getDisplayName())) {
			AttributeXPSources.Forging.NAMING.grantXP(attributes);
		}
		// Error.
		else {
			RPGMod.logger.warn("Could not determine type of anvil operation!");
		}
	}
	
	private boolean isItemArmor(ItemStack stack, Item item) {
		boolean isHelmet = item.isValidArmor(stack, EntityEquipmentSlot.HEAD, null);
		boolean isChest = item.isValidArmor(stack, EntityEquipmentSlot.CHEST, null);
		boolean isLegs = item.isValidArmor(stack, EntityEquipmentSlot.LEGS, null);
		boolean isBoots = item.isValidArmor(stack, EntityEquipmentSlot.FEET, null);
		
		return isHelmet || isChest || isLegs || isBoots;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	PlayerEvent.ItemSmeltedEvent
//  ----------------------------
//	Handles granting XP from furnace operating.
//
//	Handles following XP sources:
//	 - Furnace.OPERATING
//	 - Furnace.COOKING
//	 - Furnace.SMELTING
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SubscribeEvent
	public void onItemSmelted(ItemSmeltedEvent event) {
		// Only handle these on the server
		if (event.player.worldObj.isRemote) {
			return;
		}
		
		ICharacterAttributes attributes = CapHelper.getCapability(event.player, Capabilities.CAPABILITY_ATTRIBUTES);
		if (attributes == null) {
			return;
		}

		// TODO: Do some research on how smelting xp works and do something more fancy with this one to get sensible numbers
		int amount = (int) (10 * FurnaceRecipes.instance().getSmeltingExperience(event.smelting));

		AttributeXPSources.Furnace.OPERATING.grantXP(attributes, amount);

		if (event.smelting.getItem() instanceof ItemFood) {
			AttributeXPSources.Furnace.COOKING.grantXP(attributes, amount);
		} else {
			AttributeXPSources.Furnace.SMELTING.grantXP(attributes, amount);
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	PlayerEvent.ItemCraftedEvent
//  ----------------------------
//	Handles granting XP from crafting things.
//
//	Handles following XP sources:
//	 - Crafting.CRAFTING
//	 - Crafting.REPAIRING
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// TODO: Some cleanup on repair detection could be nice

	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event) {
		// Handle these only on server
		if (event.player.worldObj.isRemote) {
			return;
		}

		// We can't grant xp if there are no attribute capability on the player
		ICharacterAttributes attributes = CapHelper.getCapability(event.player, Capabilities.CAPABILITY_ATTRIBUTES);
		if (attributes != null) {

			boolean incompatibleItemFound = false;
			int nFound = 0;
			for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
				ItemStack stack = event.craftMatrix.getStackInSlot(i);
				if (stack != null) {
					if (stack.getItem() == event.crafting.getItem()) {
						RPGMod.logger.info("stackDMG: " + stack.getItemDamage() + ", craftingDMG: " + event.crafting.getItemDamage());
						nFound++;
					}
					// We found something that is an item, but not the same as crafted item
					else {
						incompatibleItemFound = true;
					}
				}
			}

			// If we only two items in the crafting grid and both of those were the same item
			// as the crafted, we can assume a repair took place.
			if (!incompatibleItemFound && nFound == 2) {
				AttributeXPSources.Crafting.REPAIRING.grantXP(attributes);
			}
			// Crafting process probably was not repairing, grant regular crafting xp
			else {
				AttributeXPSources.Crafting.CRAFTING.grantXP(attributes);
			}
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	PotionBrewEvent.Post
//  --------------------
//	Handles granting XP from operating brewing stand.
//
//	Handles following XP sources:
//	 - Brewing.BREWING
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SubscribeEvent
	public void onBrewingComplete(PotionBrewEvent.Post event) {
		// Only handle these on the server
		if (FMLCommonHandler.instance().getMinecraftServerInstance() == null) {
			return;
		}

		/*
		  Short version:

		  Determining accurately which player brewed the product is impossible, thus granting
		  xp depends on if a player is in vicinity of a brewing stand when the process completes.
		  This means, it doesn't matter if player is near the exact brewing stand where brewing
		  occurred. Also, all players that are near a brewing stand gain the XP.


		  Long version:

		  Due to the fact how brewing stands operate, it is mostly impossible to tell which
		  player initiated the brewing process. With Furnaces the output slot is one-way making
		  (players cannot place anything to the output slot) it possible to determine which
		  player picks up the product. With brewing stands this is not the case as output slots
		  also act as input slots for new potions.

		  If brewing XP would be granted for extracting items from potion slots, that'd
		  mean one could just repeatedly insert/extract the same potion from that slot, gaining
		  ridiculous amounts of XP quickly.

		  Thus, player-aware event of brewing process completion does not exist, and one
		  cannot be made in any sensible way. Also, events do not contain any information about
		  on which brewing stand the brewing process completed. This could be fixed by passing
		  at least the position of the brewing stand in the events, but as use cases are quite
		  narrow, it is unlikely a pull request would make it trough.

		  Only remaining sensible option is to grant xp to all players that are near any brewing
		  stand. This makes brewing xp arguably easy to get, but that's better option than to
		  ditch it completely.
		 */
		
		
		// 1. Get all players (Must be MP for getting WorldServer in order to call .getTileEntitiesIn(...))
		List<EntityPlayerMP> players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList();
		
		// 2. For each player, make sure player has required caps and then get all nearby tile-entities
		for (EntityPlayerMP player : players) {
			
			// 2.1. Make sure the player has attribute capabilities
			ICharacterAttributes attributes = CapHelper.getCapability(player, Capabilities.CAPABILITY_ATTRIBUTES);
			
			// Calculate region inside which to grant XP
			int minX = player.getPosition().getX() - BREWING_STAND_XP_RADIUS;
			int minY = player.getPosition().getY() - BREWING_STAND_XP_RADIUS;
			int minZ = player.getPosition().getZ() - BREWING_STAND_XP_RADIUS;
			
			int maxX = player.getPosition().getX() + BREWING_STAND_XP_RADIUS;
			int maxY = player.getPosition().getY() + BREWING_STAND_XP_RADIUS;
			int maxZ = player.getPosition().getZ() + BREWING_STAND_XP_RADIUS;
			
			// 2.2. Get nearby tile-entities
			List<TileEntity> tileEntities = player.getServerForPlayer().getTileEntitiesIn(minX, minY, minZ, maxX, maxY, maxZ);
			
			// 4. if at least one of found tile-entities is a TileEntityBrewingStand, grant XP
			for (TileEntity tileEntity : tileEntities) {
				if (tileEntity instanceof TileEntityBrewingStand) {
					AttributeXPSources.Brewing.BREWING.grantXP(attributes);

					// TODO: Investigate possibility of creating some form of BrewingRecipeRegistry and/or potion item extension for xp amounts
					// XXX: If adv. brewing system gets in, this could be made part of it. Just define some constant for regular potions and use interface for adv. ones
					
					// Avoid granting XP to same player multiple times
					break;
				}
			}
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	UseHoeEvent
//  -----------
//	Handles granting XP from tilling soil with a hoe.
//
//	Handles following XP sources:
//	 - Farming.TILLING
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUseHoe(UseHoeEvent event) {
		// Only handle these on the server
		if (event.getWorld().isRemote) {
			return;
		}
		
		ICharacterAttributes attributes = CapHelper.getCapability(event.getEntity(), Capabilities.CAPABILITY_ATTRIBUTES);
		
		if (attributes != null) {
			AttributeXPSources.Farming.TILLING.grantXP(attributes);
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	BonemealEvent
//  -------------
//	Handles granting XP from fertilizing plants with bonemeal.
//
//	Executes at EventPriority.LOWEST to ensure that the event isn't cancelled or result set to DENY.
//
//	Handles following XP sources:
//	 - Farming.FERTILIZING
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBonemeal(BonemealEvent event) {
		// Only handle these on the server, and only if event isn't denied
		if (event.getWorld().isRemote || event.getResult() == Event.Result.DENY) {
			return;
		}
		
		ICharacterAttributes attributes = CapHelper.getCapability(event.getEntity(), Capabilities.CAPABILITY_ATTRIBUTES);
		
		if (attributes != null) {
			AttributeXPSources.Farming.FERTILIZING.grantXP(attributes);
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	BlockEvent.PlaceEvent
//  ---------------------
//	Handles granting XP from planting.
//
//	Executes at EventPriority.LOWEST to ensure that the event isn't cancelled.
//
//	Handles following XP sources:
//	 - Farming.PLANTING
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlaceBlock(BlockEvent.PlaceEvent event) {
		// Only handle these on the server
		if (event.getWorld().isRemote) {
			return;
		}
		
		ICharacterAttributes attributes = CapHelper.getCapability(event.getPlayer(), Capabilities.CAPABILITY_ATTRIBUTES);
		
		if (attributes != null) {

			System.out.println("Block placed!");
			if (event.getItemInHand().getItem() instanceof IPlantable) {
				System.out.println("It was IPlantable!");
				AttributeXPSources.Farming.PLANTING.grantXP(attributes);

				// TODO: IPlantable extension that allows defining xp amounts
			}
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	TickEvent.PlayerTickEvent
//  -------------------------
//	Handles applying movement speed bonuses based on player stats.
//
//	RELIES ON DEFAULT IMPLEMENTATION
//
//	Handles following XP sources:
//	 - Maneuvers.SPRINTING
//	 - Maneuvers.WALKING
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// TODO: Cleanup

	private static final UUID sprintSpeedModifierUUID = UUID.fromString("fe9f3dbd-f6aa-49dd-8296-7a1725740a18");

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		// Only handle these on the server
		if (event.player.getEntityWorld().isRemote) {
			return;
		}

		EntityPlayer player = event.player;

		// Get attributes and stats for targeted player
		ICharacterAttributes attributes = CapHelper.getCapability(player, Capabilities.CAPABILITY_ATTRIBUTES);
		CharacterStats stats = (CharacterStats) CapHelper.getCapability(player, Capabilities.CAPABILITY_STATS);

		if (stats != null && attributes != null) {

			IAttributeInstance vanillaAttribute = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

			// If player just started sprinting, apply modifier
			if (player.isSprinting() && !stats.wasSprinting()) {
				addSprintModifier(vanillaAttribute, stats);
			}
			// if player just stopped sprinting, remove modifier
			else if (!player.isSprinting() && stats.wasSprinting()) {
				// We do not have exact instance, so remove by UUID
				vanillaAttribute.removeModifier(sprintSpeedModifierUUID);
				stats.setWasSprinting(false);
			}

			// Distance to last position where we granted XP
			double distanceSq = player.getDistanceSq(stats.getPreviousDistanceXPPos());

			// If player has moved enough, grant XP
			if (event.player.isSprinting() && distanceSq >= SPRINTING_XP_GRANT_DISTANCE) {
				AttributeXPSources.Maneuvers.SPRINTING.grantXP(attributes);
				stats.setPreviousDistanceXPPos(player.getPosition());
			} else if (distanceSq >= WALKING_XP_GRANT_DISTANCE) {
				AttributeXPSources.Maneuvers.WALKING.grantXP(attributes);
				stats.setPreviousDistanceXPPos(player.getPosition());
			}
		}
	}

	private void addSprintModifier(IAttributeInstance instance, CharacterStats stats) {
		instance.applyModifier(new StatAttributeModifier(
				sprintSpeedModifierUUID,
				"Sprint speed multiplier",
				Stats.SPRINT_SPEED_MULT,
				stats,
				1 // Base value multiplication operation
		));

		stats.setWasSprinting(true);
	}
}
