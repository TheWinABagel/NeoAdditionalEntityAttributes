package de.dafuqs.additionalentityattributes;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(AdditionalEntityAttributes.MOD_ID)
public class AdditionalEntityAttributes {

	public static final String MOD_ID = "additionalentityattributes";
	
	/*
	 * For testing, default vanilla commands can be used:
	 * /attribute @s additionalentityattributes:player.critical_bonus_damage modifier add 135e1f1e-755d-4cfe-82da-3648626eeba2 test 1 multiply_base
	 * /attribute @s additionalentityattributes:player.lava_visibility modifier add 135e1f1e-755d-4cfe-82da-3648626eeba2 test 10 add
	 * /attribute @s additionalentityattributes:generic.lava_speed modifier add 135e1f1e-755d-4cfe-82da-3648626eeba2 test -1 multiply
	 * /attribute @s additionalentityattributes:generic.water_speed modifier add 135e1f1e-755d-4cfe-82da-3648626eeba2 test 0.5 multiply_base
	 * /attribute @s additionalentityattributes:player.water_visibility modifier add 135e1f1e-755d-4cfe-82da-3648626eeba2 test -0.5 multiply
	 * /attribute @s additionalentityattributes:player.water_visibility modifier add 135e1f1e-755d-4cfe-82da-3eeee26eeba2 test 300 add
	 */

	private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, MOD_ID);

	/**
	 * Controls the bonus damage dealt when dealing critical hits
	 * By default, critical hits deal 1.5 times the damage, so the base value of this attribute is 0.5.
	 * Adding a flat value of 0.5 will bump that value up to make critical hits deal 2x the damage, for example
	 * Multiplying this attribute's value with a modifier value of 0.5 will increase the critical hit damage
	 * by 50%, meaning it will add 50% of the base 50% bonus damage on top, resulting in a critical hit damage
	 * multiplier of 75% (1.75x damage).
	 */
	public static final DeferredHolder<Attribute, Attribute> CRITICAL_BONUS_DAMAGE = register("player.critical_bonus_damage", 0.5, -1.0, 1024.0);
	
	/**
	 * Controls the speed of the player when in water
	 * The base value of this Holder<Attribute> is always set dynamically, therefore setting it via a command will have no effect.
	 * For the sake of maneuverability and server performance it is capped at 1.
	 * Stacks with dolphins grace and depth strider, albeit the latter has little felt effect at higher speeds.
	 */
	@Deprecated
	public static final Holder<Attribute> WATER_SPEED = NeoForgeMod.SWIM_SPEED;

	/**
	 * Controls the vision of the player when in water by adjusting the fog distance
	 */
	public static final DeferredHolder<Attribute, Attribute> WATER_VISIBILITY = register("player.water_visibility", 96.0, 0, 1024.0);

	/**
	 * Controls the maximum amount of air the entity can have, measured in ticks
	 * This value modifies the natural value of Entity.getMaxAir()
	 */
	public static final DeferredHolder<Attribute, Attribute> LUNG_CAPACITY = register("generic.lung_capacity", 0.0, -40000, 40000);
	/**
	 * Controls the speed of the player when in lava
	 * The base value of this attribute is always set dynamically, therefore setting it via a command will have no effect.
	 * For the sake of maneuverability and server performance it is capped at 1.
	 * Negative values will make the player even slower with -1.0 resulting in being almost unable to move
	 */
	public static final DeferredHolder<Attribute, Attribute> LAVA_SPEED = register("generic.lava_speed", 0.5, 0, 1);
	
	/**
	 * Controls the vision of the player when in lava by adjusting the fog distance
	 */
	public static final DeferredHolder<Attribute, Attribute> LAVA_VISIBILITY = register("player.lava_visibility", 1.0, 0, 1024.0);

	/**
	 * Controls the dig speed of the player
	*/
	public static final DeferredHolder<Attribute, Attribute> DIG_SPEED = register("player.dig_speed", 0.0D, 0.0D, 2048.0D);
	
	/**
	 * Controls the drops the player gets when using enchantments, such as looting or fortune
	 * (more precise: everything that uses the ApplyBonusLootFunction to increase drops based on an enchantments level)
	 * Each full +1 on this stat will roll the bonus count another time. Highest one is kept.
	 */
	public static final DeferredHolder<Attribute, Attribute> BONUS_LOOT_COUNT_ROLLS = register("player.bonus_loot_count_rolls", 0.0D, 0.0D, 128.0);

	/**
	 * If a loot table that does not have 100% chance (RandomChanceLootCondition, RandomChanceWithLootingLootCondition),
	 * increases the chance to get that drop up to the point that drop is guaranteed. Will not increase drop count.
	 * Example: Zombies have a ~1% chance to drop 1 Iron Ingot. BONUS_RARE_LOOT_ROLLS increases that 1% chance (but will not make it drop 2 ingots)
	 *
	 * A value of 1.0 will result in 1 additional roll with the original chance. So if a drop has a 10% chance:
	 * - `BONUS_RARE_LOOT_ROLLS=1.0` will roll another time with 10% chance
	 * - `BONUS_RARE_LOOT_ROLLS = 2.0` will result in another roll with 20% chance
	 * - `BONUS_RARE_LOOT_ROLLS = 0.5` will result in another roll with 5% chance
	 */
	public static final DeferredHolder<Attribute, Attribute> BONUS_RARE_LOOT_ROLLS = register("player.bonus_rare_loot_rolls", 0.0D, 0.0D, 128.0);

	/**
	 * Controls the jump height of the player.
	 * By default, the player jumps at a height of 0.42.
	 */
	public static final DeferredHolder<Attribute, Attribute> JUMP_HEIGHT = register("generic.jump_height", 0.0D, -1024.0, 1024.0);
	
	/**
	 * Modifies the experience dropped from mining blocks and killing mobs.
	 * The default of 1.0 equals the vanilla drop amount, 0.0 will result in no xp drops altogether.
	 */
	public static final DeferredHolder<Attribute, Attribute> DROPPED_EXPERIENCE = register("player.dropped_experience", 1.0D, 0.0D, 1024.0D);

	/**
	 * Reduces the amount of magic damage taken.
	 * By default, the player has 0 points, and each point of reduces the damage taken by 1.
	 */
	public static final DeferredHolder<Attribute, Attribute> MAGIC_PROTECTION = register("generic.magic_protection", 0.0D, 0.0D, 1024.0D);

	/**
	 * Increases the range to collect items, blocks .
	 * By default, the player has 0 points, and each point of reduces the damage taken by 1.
	 */
	public static final DeferredHolder<Attribute, Attribute> COLLECTION_RANGE = register("player.collection_range", 0.0D, 0.0D, 64.0D);


	private static DeferredHolder<Attribute, Attribute> register(final String name, double base, double min, double max) {
		Attribute attribute = new RangedAttribute("attribute.name." + MOD_ID + '.' + name, base, min, max).setSyncable(true);
        return ATTRIBUTES.register(name, () -> attribute);
	}

	public AdditionalEntityAttributes(IEventBus modBus) {
		ATTRIBUTES.register(modBus);
		modBus.register(this);
		NeoForge.EVENT_BUS.register(new AdditionalEntityAttributesEvents());
	}

	@SubscribeEvent
	public void registerPlayerAttributes(EntityAttributeModificationEvent e) {
		e.add(EntityType.PLAYER, AdditionalEntityAttributes.WATER_VISIBILITY.get());
		e.add(EntityType.PLAYER, AdditionalEntityAttributes.LAVA_VISIBILITY.get());
		e.add(EntityType.PLAYER, AdditionalEntityAttributes.CRITICAL_BONUS_DAMAGE.get());
		e.add(EntityType.PLAYER, AdditionalEntityAttributes.DIG_SPEED.get());
		e.add(EntityType.PLAYER, AdditionalEntityAttributes.BONUS_LOOT_COUNT_ROLLS.get());
		e.add(EntityType.PLAYER, AdditionalEntityAttributes.BONUS_RARE_LOOT_ROLLS.get());
		e.add(EntityType.PLAYER, AdditionalEntityAttributes.DROPPED_EXPERIENCE.get());
		e.add(EntityType.PLAYER, AdditionalEntityAttributes.COLLECTION_RANGE.get());
	}

	@SubscribeEvent
	public void registerLivingEntityAttributes(EntityAttributeModificationEvent e) {
		for (EntityType<? extends LivingEntity> living : e.getTypes()) {
			e.add(living, AdditionalEntityAttributes.LAVA_SPEED.get());
			e.add(living, AdditionalEntityAttributes.LUNG_CAPACITY.get());
			e.add(living, AdditionalEntityAttributes.JUMP_HEIGHT.get());
			e.add(living, AdditionalEntityAttributes.MAGIC_PROTECTION.get());
		}
	}
}