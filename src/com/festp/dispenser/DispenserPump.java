package com.festp.dispenser;

import org.bukkit.block.Block;

public abstract class DispenserPump {
	DispenserPumpData data;
	DropActions pumpedInfo;
	
	public DispenserPump(DispenserPumpData disp) {
		this.data = disp;
		
	}
	
	public void setPumpedInfo(DropActions pumpedInfo) {
		this.pumpedInfo = pumpedInfo;
	}
	
	public abstract Block pump(Block start);
	
	protected boolean canPump(Block b) {
		return pumpedInfo.canPump(b);
	}
	protected boolean continuePump(Block b) {
		return DropActions.continuePump(b);
	}
}
