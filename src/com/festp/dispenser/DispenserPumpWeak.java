package com.festp.dispenser;

import org.bukkit.block.Block;

public class DispenserPumpWeak extends DispenserPump {

	public DispenserPumpWeak(DispenserPumpData disp) {
		super(disp);
	}

	public Block pump(Block block) {
		int max_distance = 0;
		Block max_dist_block = null;
		if (!continuePump(block))
			return null;
		
		if (canPump(block.getRelative(2, 0, 0)) && continuePump(block.getRelative(1, 0, 0))) {
			max_dist_block = block.getRelative(2, 0, 0);
			max_distance = 3;
		} else if (max_distance < 1 && canPump(block.getRelative(1, 0, 0))) {
			max_dist_block = block.getRelative(1, 0, 0);
			max_distance = 1;
		}
		if (canPump(block.getRelative(0, 0, 2)) && continuePump(block.getRelative(0, 0, 1))) {
			max_dist_block = block.getRelative(0, 0, 2);
			max_distance = 3;
		} else if (max_distance < 1 && canPump(block.getRelative(0, 0, 1))) {
			max_dist_block = block.getRelative(0, 0, 1);
			max_distance = 1;
		}
		if (canPump(block.getRelative(-2, 0, 0)) && continuePump(block.getRelative(-1, 0, 0))) {
			max_dist_block = block.getRelative(-2, 0, 0);
			max_distance = 3;
		} else if (max_distance < 1 && canPump(block.getRelative(-1, 0, 0))) {
			max_dist_block = block.getRelative(-1, 0, 0);
			max_distance = 1;
		}
		if (canPump(block.getRelative(0, 0, -2)) && continuePump(block.getRelative(0, 0, -1))) {
			max_dist_block = block.getRelative(0, 0, -2);
			max_distance = 3;
		} else if (max_distance < 1 && canPump(block.getRelative(0, 0, -1))) {
			max_dist_block = block.getRelative(0, 0, -1);
			max_distance = 1;
		}
		if (max_distance < 2 && canPump(block.getRelative(1, 0, 1)) && (continuePump(block.getRelative(1, 0, 0)) || continuePump(block.getRelative(0, 0, 1))))
			max_dist_block = block.getRelative(1, 0, 1);
		else if (max_distance < 2 && canPump(block.getRelative(-1, 0, 1)) && (continuePump(block.getRelative(-1, 0, 0)) || continuePump(block.getRelative(0, 0, 1)))) 
			max_dist_block = block.getRelative(-1, 0, 1);
		else if (max_distance < 2 && canPump(block.getRelative(-1, 0, -1)) && (continuePump(block.getRelative(-1, 0, 0)) || continuePump(block.getRelative(0, 0, -1)))) 
			max_dist_block = block.getRelative(-1, 0, -1);
		else if (max_distance < 2 && canPump(block.getRelative(1, 0, -1)) && (continuePump(block.getRelative(1, 0, 0)) || continuePump(block.getRelative(0, 0, -1)))) 
			max_dist_block = block.getRelative(1, 0, -1);
		if (max_distance == 0 && canPump(block)) {
			max_dist_block = block;
		}
		return max_dist_block;
	}
}
