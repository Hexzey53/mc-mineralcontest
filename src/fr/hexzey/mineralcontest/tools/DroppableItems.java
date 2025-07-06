package fr.hexzey.mineralcontest.tools;

import java.util.ArrayList;

import org.bukkit.Material;

public class DroppableItems
{
	public static ArrayList<Material> getDroppableItems() {
		ArrayList<Material> materials = new ArrayList<Material>();
		
		// ARMURES EN FER
		materials.add(Material.IRON_HELMET); materials.add(Material.IRON_CHESTPLATE);
		materials.add(Material.IRON_LEGGINGS); materials.add(Material.IRON_BOOTS);
		// ARMURES EN OR
		materials.add(Material.GOLDEN_HELMET); materials.add(Material.GOLDEN_CHESTPLATE);
		materials.add(Material.GOLDEN_LEGGINGS); materials.add(Material.GOLDEN_BOOTS);
		// ARMURES EN DIAMANT
		materials.add(Material.DIAMOND_HELMET); materials.add(Material.DIAMOND_CHESTPLATE);
		materials.add(Material.DIAMOND_LEGGINGS); materials.add(Material.DIAMOND_BOOTS);
		
		// OUTILS EN PIERRE
		materials.add(Material.STONE_PICKAXE); materials.add(Material.STONE_SWORD); materials.add(Material.STONE_AXE);
		// OUTILS EN FER
		materials.add(Material.IRON_PICKAXE); materials.add(Material.IRON_SWORD); materials.add(Material.IRON_AXE);
		// OUTILS EN OR
		materials.add(Material.GOLDEN_PICKAXE); materials.add(Material.GOLDEN_SWORD); materials.add(Material.GOLDEN_AXE);
		// OUTILS EN DIAMANT
		materials.add(Material.DIAMOND_PICKAXE); materials.add(Material.DIAMOND_SWORD); materials.add(Material.DIAMOND_AXE);
		
		// ARC ET FLECHES
		materials.add(Material.BOW); materials.add(Material.ARROW);
		
		// NOURRITURE
		materials.add(Material.COOKED_BEEF); materials.add(Material.COOKED_CHICKEN); materials.add(Material.COOKED_MUTTON);
		materials.add(Material.COOKED_PORKCHOP); materials.add(Material.COOKED_RABBIT);
		
		// RESSOURCES
		// charbon
		materials.add(Material.COAL); materials.add(Material.COAL_BLOCK);
		// fer
		materials.add(Material.RAW_IRON); materials.add(Material.IRON_INGOT); materials.add(Material.IRON_BLOCK); materials.add(Material.IRON_NUGGET);
		// or
		materials.add(Material.RAW_GOLD); materials.add(Material.GOLD_INGOT); materials.add(Material.GOLD_BLOCK); materials.add(Material.GOLD_NUGGET);
		// redstone
		materials.add(Material.REDSTONE); materials.add(Material.REDSTONE_BLOCK);
		// diamant
		materials.add(Material.DIAMOND); materials.add(Material.DIAMOND_BLOCK);
		// émeraude
		materials.add(Material.EMERALD); materials.add(Material.EMERALD_BLOCK);
		
		return materials;
	}
}
