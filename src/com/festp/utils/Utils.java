package com.festp.utils;

import java.text.DecimalFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.util.Vector;

public class Utils
{
	/** @return 3 if full, 0 if empty or invalid bd*/
	public static double getCauldronLevel(BlockData bd) {
		if (bd == null || bd.getMaterial() != Material.WATER_CAULDRON) {
			return 0;
		}
		Levelled cauldron = (Levelled) bd;
		return cauldron.getLevel();
	}
	public static double getCauldronWater(Block cauldron) {
		Levelled caul = (Levelled)cauldron.getBlockData();
		return caul.getLevel() / (double)caul.getMaximumLevel();
	}
	public static boolean lowerCauldronWater(Block cauldron) {
		Levelled caul = (Levelled)cauldron.getBlockData();
		if (caul.getLevel() == 0)
			return false;
		if (caul.getLevel() == 1)
		{
			cauldron.setType(Material.CAULDRON);
			return true;
		}
		caul.setLevel(caul.getLevel() - 1);
		cauldron.setBlockData(caul);
		return true;
	}
	public static boolean fullCauldronWater(Block cauldron) {
		if (cauldron.getType() == Material.CAULDRON) {
			cauldron.setType(Material.WATER_CAULDRON);
		}
		Levelled caul = (Levelled)cauldron.getBlockData();
		if (caul.getLevel() == caul.getMaximumLevel())
			return false;
		caul.setLevel(caul.getMaximumLevel());
		cauldron.setBlockData(caul);
		return true;
	}
	
	public static String toString(Vector v) {
		if (v == null)
			return "(null)";
		DecimalFormat dec = new DecimalFormat("#0.00");
		return ("("+dec.format(v.getX())+"; "
				  +dec.format(v.getY())+"; "
				  +dec.format(v.getZ())+")")
				.replace(',', '.');
	}
	public static String toString(Location l) {
		if (l == null) return toString((Vector)null);
		return toString(new Vector(l.getX(), l.getY(), l.getZ()));
	}
	public static String toString(Block b) {
		if (b == null) return toString((Location)null);
		return toString(b.getLocation());
	}
	
	public static boolean contains(Object[] list, Object find) {
		for (Object m : list)
			if (m == find)
				return true;
		return false;
	}
}
