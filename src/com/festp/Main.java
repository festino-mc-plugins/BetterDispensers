package com.festp;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.dispenser.DropActions;

public class Main extends JavaPlugin implements Listener
{
	private CraftManager craft_manager;
	
	public void onEnable() {
		Logger.setLogger(getLogger());
    	PluginManager pm = getServer().getPluginManager();
    	
		getServer().getPluginManager().registerEvents(this, this);
		
    	craft_manager = new CraftManager(this, getServer());

    	DropActions drop_actions = new DropActions(this);
    	pm.registerEvents(drop_actions, this);
    	
    	craft_manager.addCrafts();
    	pm.registerEvents(craft_manager, this);
    	
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
			new Runnable() {
				public void run() {
					//fill cauldrons, feed animals and pump liquids
					drop_actions.onTick();
				}
			}, 0L, 1L);
		
	}
	
	public CraftManager getCraftManager()
	{
		return craft_manager;
	}
}
