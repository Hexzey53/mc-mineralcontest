package fr.hexzey.mineralcontest.tools;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class GenererGrotte
{
	private static final ArrayList<Material> overrideBlocks = new ArrayList<Material>() {{
		add(Material.STONE);
		add(Material.DIRT);
		add(Material.GRASS);
		add(Material.COAL_ORE);
		add(Material.IRON_ORE);
		add(Material.GOLD_ORE);
		add(Material.REDSTONE_ORE);
		add(Material.DIAMOND_ORE);
		add(Material.EMERALD_ORE);
	}};
	
	public static void genererGrotte(Location base, int length) {
		/**
		 * Générer une grotte d'une longueur donnée
		 */
		Location loc = base.clone();
		Random random = new Random();
		int radius = random.nextInt(2) + 2; // [0;2[ +2 = [2;4[
		for(int i=0; i<length; i++) {
			double distanceToCenter = Math.sqrt( Math.pow(loc.getX()*16, 2) + Math.pow(loc.getZ()*16, 2) );
			if(distanceToCenter < 65) return; // mettre fin à la grotte si elle s'approche trop du centre
			
			if(loc.getY() < 7) return; // mettre fin à a grotte si elle s'approche de la bedrock
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "La grotte se genere... (" + String.valueOf(i) + "/" + String.valueOf(length-1) + ")");
			
			// détruire les alentours en forme de cercle
			try {
				GenererFilon.genererFilon(loc, Material.GLOWSTONE, 20); // GLOWSTONE pour debug, sinon AIR
				//GenererGrotte.detruireAlentours(loc, radius);
			} catch(Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur.");
			}
			
			Location nextLoc = loc.clone();
			// choisir une direction aléatoire
			switch(random.nextInt(5)) { // aléatoire entre [0;4]
				case 0: // 0 = réduire y
					nextLoc.setY(loc.getY()-1);
					break;
				case 1: // 1 = augmenter x
					nextLoc.setX(loc.getX()+1);
					break;
				case 2: // 2 = réduire x
					nextLoc.setX(loc.getX()-1);
					break;
				case 3: // 3 = augmenter z
					nextLoc.setZ(loc.getZ()-1);
					break;
				case 4: // 4 = réduire z
					nextLoc.setZ(loc.getZ()-1);
					break;
				default:
					break;
			}
			loc = nextLoc;
			// choisir un rayon (-1, 0 ou +1 selon un random)
			int variation = random.nextInt(3) -1; // [0;2] -1 = [-1;1]
			radius = Math.max(2, radius+variation); // rayon minimum de 2 blocs
		}
		return;
	}
}
