package shadows.apotheosis.ench.table;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public interface IEnchantableItem {

	/**
	 * Handles the enchanting of an item.<br>
	 * The item becomes responsible for changing the target item, and applying the Enchantments to the item.<br>
	 * @param stack The ItemStack being enchanted.
	 * @param enchantments The enchantments being applied.
	 * @return The newly-enchanted itemstack.
	 */
	default ItemStack onEnchantment(ItemStack stack, List<EnchantmentInstance> enchantments) {
		boolean isBook = stack.is(Items.BOOK);
		if (isBook) {
			ItemStack enchBook = new ItemStack(Items.ENCHANTED_BOOK);
			CompoundTag tag = stack.getTag();
			if (tag != null) {
				stack.setTag(tag.copy());
			}
			stack = enchBook;
		}

		for (EnchantmentInstance inst : enchantments) {
			if (isBook) {
				EnchantedBookItem.addEnchantment(stack, inst);
			} else {
				stack.enchant(inst.enchantment, inst.level);
			}
		}
		return stack;
	}

	/**
	 * Allow the Item to perform manipulations to the selected list of enchantments.<br>
	 * Note that this method must be deterministic respective to the random seed, and should always produce the same results for the same seed.<br>
	 * @param builtList The Enchantment List, as generated by {@link RealEnchantmentHelper#buildEnchantmentList}
	 * @param rand The pre-seeded random
	 * @param stack The stack being enchanted (which is not currently enchanted)
	 * @param level The table level
	 * @param quanta The quanta value
	 * @param arcana The arcana value
	 * @param treasure If treasure enchantments can be selected or not.
	 * @return The list of enchantments selected, given the context.
	 */
	default List<EnchantmentInstance> selectEnchantments(List<EnchantmentInstance> builtList, RandomSource rand, ItemStack stack, int level, float quanta, float arcana, boolean treasure) {
		return builtList;
	}

	/**
	 * Normally, the {@link Enchantment} has final say in if it can be applied to an item.
	 * This allows an item to opt-in to always being able to receive specific enchantments.
	 * @param stack The item being enchanted.
	 * @param enchantment The enchantment being queried against.
	 * @return If the enchantment is allowed on this itemstack, overriding standard rules.
	 */
	default boolean forciblyAllowsTableEnchantment(ItemStack stack, Enchantment enchantment) {
		return stack.is(Items.BOOK) && enchantment.isAllowedOnBooks();
	}

	/**
	 * Normally, allowance of treasure enchantments is determined externally. This can change that.
	 * @param stack The stack being enchanted.
	 * @param wasTreasureAllowed If treasure was previously allowed.
	 * @return If treasure enchantments are allowed.
	 */
	default boolean isTreasureAllowed(ItemStack stack, boolean wasTreasureAllowed) {
		return wasTreasureAllowed;
	}

}
