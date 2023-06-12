package com.festp.dispenser;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import com.festp.utils.Vector3i;

public class DispenserPumpStrong extends DispenserPump {

	int maxDy = 50;
	int maxDxz = 50; // TODO: configurable pump area
	int minDxz = -maxDxz;
	int pumpArea = maxDxz * 2 + 1;

	public DispenserPumpStrong(DispenserPumpData disp) {
		super(disp);
	}

	public Block pump(Block block) {
		Block max_dist_block = null;
		if (continuePump(block))
		{
			int top_layer_dy = 0;
			List<LayerSet> layers = new ArrayList<>();
			LayerSet top_layer = new LayerSet(maxDxz);
			layers.add(top_layer);
			
			int dist = 0;
			List<Vector3i> unchecked = new ArrayList<>();
			List<Vector3i> next_unchecked = new ArrayList<>();
			next_unchecked.add(new Vector3i(0, 0, 0));
			while (next_unchecked.size() > 0)
			{
				dist++;
				unchecked = next_unchecked;
				next_unchecked = new ArrayList<>();
				for (Vector3i loc : unchecked)
				{
					int dx = loc.getX(), dy = loc.getY(), dz = loc.getZ();
					Block b = block.getRelative(dx, dy, dz);
					
					if (dy >= layers.size())
					{
						top_layer = new LayerSet(maxDxz);
						layers.add(top_layer);
						top_layer_dy++;
					}
					
					LayerSet cur_layer = layers.get(dy);
					
					if (canPump(b))
					{
						if (!cur_layer.isDefinedFarthest() || dist > cur_layer.max_distance)
						{
							cur_layer.farthest[0] = dx;
							cur_layer.farthest[1] = dz;
							cur_layer.max_distance = dist;
						}
					}
					
					cur_layer.setDistance(dx, dz, dist);
					
					Block rel;
					if (dy < maxDy) {
						rel = b.getRelative(0, 1, 0);
						if (continuePump(rel))
							if (dy >= top_layer_dy)
								next_unchecked.add(new Vector3i(dx, dy + 1, dz));
							else if (layers.get(dy + 1).isUnchecked(dx, dz)) {
								next_unchecked.add(new Vector3i(dx, dy + 1, dz));
								layers.get(dy + 1).setNext(dx, dz);
							}
					}
					if (dx < maxDxz) {
						rel = b.getRelative(1, 0, 0);
						if (continuePump(rel))
							if (cur_layer.isUnchecked(dx + 1, dz)) {
								next_unchecked.add(new Vector3i(dx + 1, dy, dz));
								cur_layer.setNext(dx + 1, dz);
							}
					}
					if (minDxz < dx) {
						rel = b.getRelative(-1, 0, 0);
						if (continuePump(rel))
							if (cur_layer.isUnchecked(dx - 1, dz)) {
								next_unchecked.add(new Vector3i(dx - 1, dy, dz));
								cur_layer.setNext(dx - 1, dz);
							}
					}
					if (dz < maxDxz) {
						rel = b.getRelative(0, 0, 1);
						if (continuePump(rel))
							if (cur_layer.isUnchecked(dx, dz + 1)) {
								next_unchecked.add(new Vector3i(dx, dy, dz + 1));
								cur_layer.setNext(dx, dz + 1);
							}
					}
					if (minDxz < dz) {
						rel = b.getRelative(0, 0, -1);
						if (continuePump(rel))
							if (cur_layer.isUnchecked(dx, dz - 1)) {
								next_unchecked.add(new Vector3i(dx, dy, dz - 1));
								cur_layer.setNext(dx, dz - 1);
							}
					}
				}
			}
			
			for (int dy = layers.size() - 1; dy >= 0; dy--) {
				LayerSet layer = layers.get(dy);
				if (layer.isDefinedFarthest())
				{
					int dx = layer.farthest[0];
					int dz = layer.farthest[1];
					max_dist_block = block.getRelative(dx, dy, dz);
					break;
				}
			}
			/*for (int dy = 0; dy < layers.size(); dy++)
			{
				LayerSet layer = layers.get(dy);
				layer.printScale(block.getY() + dy);
			}*/
		}
		return max_dist_block;
	}
}
