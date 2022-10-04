package com.festp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.festp.dispenser.PumpManager;

public class CraftManager implements Listener {
	public enum CraftTag { KEEP_DATA, ONLY_SPECIFIC };
	
	Server server;
	Main plugin;
	
	List<NamespacedKey> recipe_keys = new ArrayList<>();
	
	public CraftManager(Main plugin, Server server) {
		this.plugin = plugin;
		this.server = server;
	}
	
	public void addCrafts() {
		addSomeCrafts();
	}
	
	public void giveRecipe(Player p, String recipe) {
		Bukkit.getServer().dispatchCommand(p, "recipe give "+p.getName()+" "+recipe);
	}
	public void giveOwnRecipe(Player p, String recipe) {
		giveRecipe(p, plugin.getName().toLowerCase()+":"+recipe);
	}
	public void giveRecipe(HumanEntity player, NamespacedKey key) {
		player.discoverRecipe(key);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		for(NamespacedKey recipe_name : recipe_keys) {
			giveRecipe(p, recipe_name);
		}
	}
	
	public boolean addCraftbookRecipe(NamespacedKey key) {
		if (recipe_keys.contains(key))
			return false;
		recipe_keys.add(key);
		return true;
	}
	
	private void addSomeCrafts() {
		String name___pump_regular = "regular_pump";
		String name___pump_advanced = "advanced_pump";
    	NamespacedKey key___pump_regular = new NamespacedKey(plugin, name___pump_regular);
    	NamespacedKey key___pump_advanced = new NamespacedKey(plugin, name___pump_advanced);
		recipe_keys.add(key___pump_regular);
		recipe_keys.add(key___pump_advanced);
    	
    	// pumps
    	ItemStack reg_pump = new ItemStack(Material.BLAZE_ROD, 1);
    	ItemMeta reg_pump_meta = reg_pump.getItemMeta();
    	String RUS_reg_pump_name = "Обычная помпа";
    	String RUS_reg_pump_lore = "Обычный модуль помпы для раздатчика";
    	String ENG_reg_pump_name = "Regular Pump";
    	String ENG_reg_pump_lore = "Regular pump module for dispenser (pumps water and lava)";
    	reg_pump_meta.setDisplayName(ENG_reg_pump_name);
    	reg_pump_meta.setLore(Arrays.asList(ENG_reg_pump_lore));
    	reg_pump.setItemMeta(reg_pump_meta);
    	ShapedRecipe regular_pump = new ShapedRecipe(key___pump_regular, reg_pump);
    	regular_pump.shape(new String[]{"RPR", "PHP", "RPR"});
    	regular_pump.setIngredient('R', Material.REDSTONE);
    	regular_pump.setIngredient('P', Material.PISTON);
    	regular_pump.setIngredient('H', Material.HOPPER);
    	server.addRecipe(regular_pump);

    	ItemStack top_pump = new ItemStack(Material.BLAZE_ROD, 1);
    	ItemMeta top_pump_meta = top_pump.getItemMeta();
    	String RUS_top_pump_name = "Продвинутая помпа";
    	String RUS_top_pump_lore = "Продвинутый модуль помпы для раздатчика";
    	String ENG_top_pump_name = "Advanced Pump";
    	String ENG_top_pump_lore = "Advanced pump module for dispenser (pumps water and lava)";
    	top_pump_meta.setDisplayName(ENG_top_pump_name);
    	top_pump_meta.setLore(Arrays.asList(ENG_top_pump_lore));
    	top_pump.setItemMeta(top_pump_meta);
    	ShapedRecipe advanced_pump = new ShapedRecipe(key___pump_advanced, top_pump);
    	advanced_pump.shape(new String[]{"RMR", "MSM", "RMR"});
    	advanced_pump.setIngredient('R', Material.REDSTONE_BLOCK);
    	advanced_pump.setIngredient('M', Material.BLAZE_ROD);
    	advanced_pump.setIngredient('S', Material.NETHER_STAR);
    	server.addRecipe(advanced_pump);
	}
	
	@EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event)
	{
		CraftingInventory ci = event.getInventory();
		ItemStack[] matrix = ci.getMatrix();

		for (int i = 0; i < matrix.length; i++)
		{
			// pump can only be used in pump recipes
			if (PumpManager.isPump(matrix[i]))
			{
				// only weak pump can only be used in strong pump recipes
				if (PumpManager.isStrongPump(matrix[i]) || !PumpManager.isPump(ci.getResult()))
				{
					ci.setResult(null);
					return;
				}
			}
		}
		
		// craft grid test
		if (matrix.length == 9 && matrix[0] != null && matrix[1] != null && matrix[2] != null && matrix[3] != null
				&& matrix[5] != null && matrix[6] != null && matrix[7] != null && matrix[8] != null) {
			// advanced pump module
			if (matrix[0].getType().equals(Material.REDSTONE_BLOCK) && matrix[2].getType().equals(Material.REDSTONE_BLOCK)
					&& matrix[6].getType().equals(Material.REDSTONE_BLOCK) && matrix[8].getType().equals(Material.REDSTONE_BLOCK)
					&& matrix[1].getType().equals(Material.BLAZE_ROD) && matrix[3].getType().equals(Material.BLAZE_ROD)
					&& matrix[5].getType().equals(Material.BLAZE_ROD) && matrix[7].getType().equals(Material.BLAZE_ROD)
					&& matrix[4] != null && matrix[4].getType().equals(Material.NETHER_STAR))
			{
				if (PumpManager.isWeakPump(matrix[1]) && PumpManager.isWeakPump(matrix[3])
						&& PumpManager.isWeakPump(matrix[5]) && PumpManager.isWeakPump(matrix[7]))
				{
					// nothing to do
				}
				else
				{
					ci.setResult(null);
				}
			}
		}
	}
}
