package com.festp.dispenser;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.festp.Main;
import com.festp.utils.Utils;
import com.festp.utils.UtilsType;
import com.festp.dispenser.PumpManager.PumpState;

public class DropActions implements Listener
{
	Main pl;
	
	// dispensers to pump the water
	List<Dispenser> dispsPump = new ArrayList<>();
	List<Block> pumpingBlocks = new ArrayList<>();
	
	public DropActions(Main plugin) {
		this.pl = plugin;
	}
	
	public void onTick() {
		for (int i = dispsPump.size()-1; i >= 0; i--) {
			dispenserPump(dispsPump.get(i));
			dispsPump.remove(i);
		}
		pumpingBlocks.clear();
	}
	
	@EventHandler
	public void onBlockDispense(BlockDispenseEvent event)
	{
		if (event.getItem() == null || event.getBlock().getType() != Material.DISPENSER)
			return;
		
		Dispenser dispenser = (Dispenser)event.getBlock().getState();
		PumpState pr = PumpManager.test(dispenser, event.getItem());
		if (pr == PumpState.READY) {
			dispsPump.add(dispenser);
			event.setCancelled(true);
		}
		else if (pr == PumpState.MODULE) {
			event.setCancelled(true);
		}
	}
	
	public void dispenserPump(Dispenser d) {
		DispenserPump pump = PumpManager.getPump(d);
		pump.setPumpedInfo(this);

		Block blockToPump = travelPipeAndPump(pump);
		
		DispenserPumpData data = pump.data;
		boolean willPump = data.bucketIndex >= 0 || ( data.nullIndex >= 0 && data.multybucketIndex >= 0);
		if (!willPump)
			return;
		
		if (blockToPump == null)
			blockToPump = pump.pump(Utils.getActionBlock(d));
		
		if (blockToPump != null)
		{
			pumpingBlocks.add(blockToPump);
			Material pumped = pump(blockToPump);
			if (pumped == null)
				return;

			Inventory inv = data.inv;
			if (data.bucketIndex < 9) {
				if (data.bucketIndex < 0) {
					inv.getItem(data.multybucketIndex).setAmount(inv.getItem(data.multybucketIndex).getAmount() - 1);
					data.bucketIndex = data.nullIndex;
				}
				if (pumped == Material.LAVA)
					inv.setItem(data.bucketIndex, new ItemStack(Material.LAVA_BUCKET));
				else if (pumped == Material.WATER)
					inv.setItem(data.bucketIndex, new ItemStack(Material.WATER_BUCKET));
			}
		}
	}
	
	private static Block travelPipeAndPump(DispenserPump pump) {
		//  _.._        ____          ____
		// |    |      |    |        |    |
		// |____|      |    = ll     |_.._|
		//             |____| ll       ll
		//
		//   up       horizontal      down
		//
		// '..' or '=' is the dispenser hole, 'll' is pipe block
		Dispenser d = pump.data.dispenser;
		Inventory inv = d.getInventory();
		Block block = Utils.getActionBlock(d);
		int dy = block.getY() - d.getY();
		
		if (dy > 0) // can't use pipes
			return block;
		
		Block blockToPump = null;
		int pipes = 0;
		// scroll all placed pipe blocks
		while (block.getType() == Material.NETHER_BRICK_FENCE || block.getType() == Material.NETHER_BRICKS) {
			block = block.getRelative(0, -1, 0);
			pipes += 1;
		}
		int pipeIndex = -1;
		Block testLiquid = block;
		// test available liquid below pipe
		while (UtilsType.isAir(testLiquid.getType()) /*|| UtilsType.isFlowingLiquid(testLiquid)*/ 
				|| testLiquid.getType() == Material.NETHER_BRICK_FENCE|| testLiquid.getType() == Material.NETHER_BRICKS) {
			/*if (testLiquid.isLiquid()) {
				blockToPump = findBlockToPump_regular(testLiquid);
				if (blockToPump != null)
					break;
			}*/
			testLiquid = testLiquid.getRelative(0, -1, 0);
		}
		blockToPump = pump.pump(testLiquid);
		// remove fences if will not be able to pump
		if (blockToPump == null) {
			if (pipes > 0) {
				// find slot to add 1 fence
				for (int i = 0; i < 9; i++) {
					ItemStack is;
					is = inv.getItem(i);
					if (is != null && is.getType() == Material.NETHER_BRICK_FENCE && is.getAmount() < 64)
					{
						pipeIndex = i;
						is.setAmount(is.getAmount() + 1);
						block.getRelative(0, 1, 0).setType(Material.AIR);
						break;
					}
				}
				int nullIndex = pump.data.nullIndex;
				if (pipeIndex < 0 && nullIndex >= 0) {
					inv.setItem(nullIndex, new ItemStack(Material.NETHER_BRICK_FENCE, 1));
					block.getRelative(0, 1, 0).setType(Material.AIR);
				}
			}
			return null;
		}
		// place fences
		if (block.getY() > blockToPump.getY()) {
			pipeIndex = pump.data.pipeIndex;
			if (pipeIndex >= 0) {
				ItemStack pipe = inv.getItem(pipeIndex);
				pipe.setAmount(pipe.getAmount() - 1);
				block.setType(Material.NETHER_BRICK_FENCE);
			}
			return null;
		}
		return blockToPump;
	}
	
	/** @return Liquid material: Material.LAVA or Material.WATER */
	private static Material pump(Block b)
	{
		Material material = b.getType();
		if (material == Material.BUBBLE_COLUMN) {
			material = Material.WATER;
		}
		
		if (material == Material.LAVA || material == Material.WATER)
		{
			b.setType(Material.AIR);
			
			return material;
		}
		
		BlockData data = b.getBlockData();
		if (data instanceof Waterlogged)
		{
			Waterlogged waterlogged = (Waterlogged) data;
			if (waterlogged.isWaterlogged())
			{
				waterlogged.setWaterlogged(false);
				b.setBlockData(waterlogged);
				b.getState().update(); // destroy lily pads and etc
				return Material.WATER;
			}
		}
		else if (UtilsType.isWaterPlant(material))
		{
			b.breakNaturally();
			return Material.WATER;
		}
		
		return null;
	}
	
	public boolean canPump(Block b)
	{
		if (pumpingBlocks.contains(b))
			return false;
		return isPumpable(b);
	}

	public static boolean continuePump(Block b)
	{
		if (b.getY() >= b.getWorld().getMaxHeight())
			return false;
		if (isPumpable(b))
			return true;
		if (b.isLiquid())
			return true;
		return false;
	}
	
	public static boolean isPumpable(Block b)
	{
		if (UtilsType.isStationaryLiquid(b))
			return true;
		BlockData block_data = b.getBlockData();
		Material material = b.getType();
		if (block_data instanceof Waterlogged)
			return ((Waterlogged) block_data).isWaterlogged();
		if (UtilsType.isWaterPlant(material))
			return true;
		return false;
	}
}
