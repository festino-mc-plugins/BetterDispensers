package com.festp.dispenser;


import org.bukkit.block.Dispenser;
import org.bukkit.inventory.Inventory;

import com.festp.dispenser.PumpManager.PumpType;

public class DispenserPumpData {
	Dispenser dispenser;
	Inventory inv;
	PumpType pumpType = PumpType.NONE;
	int moduleIndex = -1, pipeIndex = -1;
	int bucketIndex = -1, nullIndex = -1, multybucketIndex = -1;
	
	public DispenserPumpData(Dispenser d, PumpType pumpType, int moduleIndex, int pipeIndex, int multybucketIndex, int bucketIndex, int nullIndex) {
		this.dispenser = d;
		this.inv = d.getInventory();
		this.pumpType = pumpType;
		this.moduleIndex = moduleIndex;
		this.pipeIndex = pipeIndex;
		this.multybucketIndex = multybucketIndex;
		this.bucketIndex = bucketIndex;
		this.nullIndex = nullIndex;
	}
}
