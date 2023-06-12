package com.festp.dispenser;

import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PumpManager {
	public static final Material PUMP_MATERIAL = Material.BLAZE_ROD;
	public static final Enchantment bottomless_bucket_metaench = Enchantment.ARROW_INFINITE;

	enum PumpState {READY, MODULE, NONE};
	enum PumpType {NONE, REGULAR, ADVANCED};
	
	public static PumpState test(Dispenser d, ItemStack dropped) {
		DispenserPumpData dd = getDispenserData(d, dropped);
		if (dd.moduleIndex >= -1) {
			if (dd.bucketIndex >= -1 || (dd.multybucketIndex >= -1 && dd.nullIndex >= 0) || dd.pipeIndex >= -1) {
				return PumpState.READY;
			}
			return PumpState.MODULE;
		}
		return PumpState.NONE;
	}
	
	public static DispenserPump getPump(Dispenser d) {
		DispenserPumpData dd = getDispenserData(d, null);
		if (dd.moduleIndex >= 0 && ( dd.bucketIndex >= 0 || dd.pipeIndex >= 0 || (dd.nullIndex >= 0 && dd.multybucketIndex >= 0)) ) {
			if (dd.pumpType == PumpType.REGULAR)
				return new DispenserPumpWeak(dd);
			else if (dd.pumpType == PumpType.ADVANCED)
				return new DispenserPumpStrong(dd);
		}
		return null;
	}
	
	private static DispenserPumpData getDispenserData(Dispenser d, ItemStack dropped) {
		Inventory inv = d.getInventory();
		PumpType pump_type = PumpType.NONE;
		int bucket_index = -2, module_index = -2;
		int multybucket_index = -2, pipe_index = -2, null_index = -2;
		ItemStack is;
		for (int i = dropped == null ? 0 : -1; i < 9; i++) {
			if (i < 0) is = dropped;
			else is = inv.getItem(i);
			if (is != null)
			{
				if (module_index < 0 && is.getType() == Material.BLAZE_ROD
						&& is.hasItemMeta() && is.getItemMeta().hasLore()) {
					if (PumpManager.isWeakPump(is)) {
						module_index = i;
						pump_type = PumpType.REGULAR;
						if (bucket_index >= 0) break;
					}
					else if (PumpManager.isStrongPump(is)) {
						module_index = i;
						pump_type = PumpType.ADVANCED;
						if (bucket_index >= 0) break;
					}
				}
				else if ( is.getType() == Material.BUCKET ) {
					if (is.getEnchantmentLevel(PumpManager.bottomless_bucket_metaench) > 0) // TODO
						bucket_index = 9;
					else if (is.getAmount() == 1 && bucket_index < -1)
						bucket_index = i;
					else {
						if (multybucket_index < 0)
							multybucket_index = i;
						if (null_index < 0) continue;
					}
					if (module_index >= -1) break;
				}
				else if (is.getType() == Material.NETHER_BRICK_FENCE)
					pipe_index = i;
			}
			else if (null_index < 0) null_index = i;
		}
		//System.out.println("TEST: "+ module_index+" "+bucket_index+" "+multybucket_index+" "+null_index);
		return new DispenserPumpData(d, pump_type, module_index, pipe_index, multybucket_index, bucket_index, null_index);
	}
	
	public static boolean isPump(ItemStack is) {
		if (is == null || is.getType() != PUMP_MATERIAL || !is.hasItemMeta() || !is.getItemMeta().hasLore()) {
			return false;
		}
		String lore = is.getItemMeta().getLore().get(0).toLowerCase(Locale.ENGLISH);
		if (lore.contains("pump") || lore.contains("помп")) {
			return true;
		}
		return false;
	}
	public static boolean isWeakPump(ItemStack is) {
		if (is == null || is.getType() != PUMP_MATERIAL || !is.hasItemMeta() || !is.getItemMeta().hasLore()) {
			return false;
		}
		String lore = is.getItemMeta().getLore().get(0).toLowerCase(Locale.ENGLISH);
		return lore.contains("regular") || lore.contains("обычн");
	}
	public static boolean isStrongPump(ItemStack is) {
		if (is == null || is.getType() != PUMP_MATERIAL || !is.hasItemMeta() || !is.getItemMeta().hasLore()) {
			return false;
		}
		String lore = is.getItemMeta().getLore().get(0).toLowerCase(Locale.ENGLISH);
		return lore.contains("advanced") || lore.contains("продвинут");
	}
}
