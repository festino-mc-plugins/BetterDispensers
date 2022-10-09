package com.festp;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.dispenser.DropActions;

public class Main extends JavaPlugin implements Listener
{
	private CraftManager craftManager;
	
	public void onEnable() {
		Logger.setLogger(getLogger());
    	PluginManager pm = getServer().getPluginManager();
    	
		getServer().getPluginManager().registerEvents(this, this);
		
    	craftManager = new CraftManager(this, getServer());

    	DropActions dropActions = new DropActions(this);
    	pm.registerEvents(dropActions, this);
    	
    	craftManager.addCrafts();
    	pm.registerEvents(craftManager, this);
    	
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
			new Runnable() {
				public void run() {
					// pump liquids
					dropActions.onTick();
				}
			}, 0L, 1L);
		
	}
	
	public CraftManager getCraftManager()
	{
		return craftManager;
	}
}
