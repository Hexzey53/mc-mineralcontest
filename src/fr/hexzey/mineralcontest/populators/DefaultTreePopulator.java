package fr.hexzey.mineralcontest.populators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

public class DefaultTreePopulator extends BlockPopulator
{
	private final HashMap<Biome, List<TreeType>> biomeTrees = new HashMap<Biome, List<TreeType>>() {{
        put(Biome.PLAINS, Arrays.asList(TreeType.CHERRY, TreeType.JUNGLE_BUSH));
        put(Biome.FOREST, Arrays.asList(TreeType.TREE, TreeType.BIG_TREE, TreeType.BIRCH, TreeType.JUNGLE_BUSH));
        put(Biome.DARK_FOREST, Arrays.asList(TreeType.DARK_OAK));
        // biomes jungle
        put(Biome.JUNGLE, Arrays.asList(TreeType.JUNGLE, TreeType.SMALL_JUNGLE, TreeType.JUNGLE_BUSH));
        put(Biome.BAMBOO_JUNGLE, Arrays.asList(TreeType.JUNGLE, TreeType.SMALL_JUNGLE, TreeType.JUNGLE_BUSH));
        // biomes froids/montagnes
        put(Biome.TAIGA, Arrays.asList(TreeType.REDWOOD, TreeType.TALL_REDWOOD));
        put(Biome.SNOWY_TAIGA, Arrays.asList(TreeType.REDWOOD, TreeType.TALL_REDWOOD));
        put(Biome.SNOWY_PLAINS, Arrays.asList(TreeType.REDWOOD, TreeType.TALL_REDWOOD));
        put(Biome.SNOWY_BEACH, Arrays.asList(TreeType.REDWOOD, TreeType.TALL_REDWOOD));
        // biomes marécages
        put(Biome.SWAMP, Arrays.asList(TreeType.MANGROVE));
        put(Biome.MANGROVE_SWAMP, Arrays.asList(TreeType.MANGROVE));
    }};
    
    private final HashMap<Biome, Double> biomeTreeProbability = new HashMap<Biome, Double>() {{
    	put(Biome.PLAINS, 3d);
    	put(Biome.FOREST, 50d);
    	put(Biome.DARK_FOREST, 65d);
    	// biomes junlge
    	put(Biome.JUNGLE, 100d);
    	put(Biome.BAMBOO_JUNGLE, 100d);
    	// biomes froids/montagnes
    	put(Biome.TAIGA, 60d);
    	put(Biome.SNOWY_TAIGA, 60d);
    	put(Biome.SNOWY_PLAINS, 15d);
    	put(Biome.SNOWY_BEACH, 15d);
    	// biomes marécages
    	put(Biome.SWAMP, 25d);
    	put(Biome.MANGROVE_SWAMP, 25d);
    }};
    
    private final ArrayList<Material> baseForTrees = new ArrayList<Material>() {{
        add(Material.DIRT);
        add(Material.GRASS_BLOCK);
    }};
    
	@Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion)
	{
        for(int i = 1; i < 13; i++) // nombre maximum d'arbres sur le chunk
        {
        	if(random.nextInt(100) < 75) // probabilité d'ajouter un arbre
		    {
        		int x = random.nextInt(16) + chunkX * 16;
                int z = random.nextInt(16) + chunkZ * 16;
                int y = 70;
                if(limitedRegion.getBiome(x, y, z) == Biome.SAVANNA_PLATEAU) continue;
                while(baseForTrees.contains(limitedRegion.getType(x, y, z)) == false && y >= 30) y--;
                if(y > 30)
                {
                	Location location = new Location(Bukkit.getWorld(worldInfo.getUID()), x, y+1, z);
                    List<TreeType> trees = biomeTrees.getOrDefault(limitedRegion.getBiome(location), Arrays.asList(TreeType.TREE, TreeType.BIRCH));
                    
                    if(random.nextInt(100) < this.biomeTreeProbability.getOrDefault(limitedRegion.getBiome(location), 50d))
                    {
                    	if (trees.size() > 0 && baseForTrees.contains(limitedRegion.getType(x, y, z)))
                    	{
                            limitedRegion.generateTree(location, random, trees.get(random.nextInt(trees.size())));
                        }
                    }
                }
		    }
        }
    }
}