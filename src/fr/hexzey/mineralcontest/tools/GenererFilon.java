package fr.hexzey.mineralcontest.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class GenererFilon
{
	public static void genererFilon(Location centre, Material blockType, int nbBlocs) {
		/**
		 * Générer un filon pour un type de bloc et un nombre de blocs donnés
		 */
		
		/**
		 * Améliorations à apporter:
		 * Récupérer le chunk du bloc, si il est différent de celui du bloc d'origine on essaie une autre direction
		 * 
		 * Patch nécessaire pour éviter de charger des chunks voisins qui chargeront des chunks voisins etc...
		 */
		
		World world = centre.getWorld();
		int X = centre.getBlockX();
		int Y = centre.getBlockY();
		int Z = centre.getBlockZ();
		
		if(world.getBlockAt(X, Y, Z).getType() == Material.STONE)
		{
			world.getBlockAt(X, Y, Z).setType(blockType, false);			
			for(int i=0; i<nbBlocs-1; i++) {
				ArrayList<Integer> directions = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5));
				boolean placed = false;
				while(directions.size() > 0 && placed == false) {
					Random random = new Random();
					Integer currentDir = directions.get(random.nextInt(directions.size()));
					int X2 = X; int Y2 = Y; int Z2 = Z;
					switch(currentDir) 
					{
						case 0: X2++; break;
						case 1: Y2++; break;
						case 2: Z2++; break;
						case 3: X2--; break;
						case 4: Y2--; break;
						case 5: Z2--; break;
					}
					if(Math.floor(X2/16) != Math.floor(X/16) || Math.floor(Z2/16) != Math.floor(Z/16)) {
						directions.remove(currentDir);
						continue;
					}
					else {
						placed = true;
						X = X2; Y = Y2; Z = Z2;
						if(world.getBlockAt(X, Y, Z).getType() != Material.BEDROCK && world.getBlockAt(X, Y, Z).getType() != Material.GRASS) {
							world.getBlockAt(X, Y, Z).setType(blockType, false);
						}
					}
				}
			}
		}
		return;
	}
}
