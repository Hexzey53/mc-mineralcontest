package fr.hexzey.mineralcontest.populators.oreveins;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import fr.hexzey.mineralcontest.Main;
import fr.hexzey.mineralcontest.tools.GenererFilon;

public class DiamondVeinsPopulator extends BlockPopulator
{
	@Override
	public void populate(World world, Random random, Chunk chunk)
	{
		int X, Y, Z;
		for(int i = 1; i < 5; i++) // nombre max de filons par chunk
		{
		    if(random.nextInt(100) < 50) // probabilité de générer un filon
		    {
		    	// The chance of spawning
				X = chunk.getX()*16 + random.nextInt(16);
				Z = chunk.getZ()*16 +random.nextInt(16);
				Y = random.nextInt(8)+1; // hauteur aléatoire
				
				// pas de génération en dehors de la carte
				if(X < Main.getConfiguration().getBorderSize()/2*-1 || X > Main.getConfiguration().getBorderSize()/2) return;
				if(Z < Main.getConfiguration().getBorderSize()/2*-1 || Z > Main.getConfiguration().getBorderSize()/2) return;
				
				if(world.getBlockAt(X, Y, Z).getType() == Material.STONE)
				{
					int longueurFilon = random.nextInt(4) + 1; // [0;4[ +1 = [1;5[
					GenererFilon.genererFilon(new Location(world, X, Y, Z), Material.DIAMOND_ORE, longueurFilon);
				}
		    }
		}
	}
}
