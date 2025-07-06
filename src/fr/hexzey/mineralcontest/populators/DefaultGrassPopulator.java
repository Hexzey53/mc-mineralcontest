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

public class DefaultGrassPopulator extends BlockPopulator
{
	private final HashMap<Biome, List<Material>> biomeGrass = new HashMap<Biome, List<Material>>() {{
        put(Biome.PLAINS, Arrays.asList(Material.GRASS, Material.OXEYE_DAISY));
        put(Biome.FOREST, Arrays.asList(Material.GRASS, Material.FERN));
    }};
    
	private final ArrayList<Material> baseForGrass = new ArrayList<Material>() {{
        add(Material.DIRT);
        add(Material.GRASS_BLOCK);
    }};
    
	@Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion)
	{
        for(int i = 1; i < 26; i++) // nombre maximum d'herbes sur le chunk
        {
    		int x = random.nextInt(16) + chunkX * 16;
            int z = random.nextInt(16) + chunkZ * 16;
            int y = 70;
            if(limitedRegion.getBiome(x, y, z) == Biome.SAVANNA_PLATEAU) continue;
            while(baseForGrass.contains(limitedRegion.getType(x, y, z)) == false && y >= 30) y--;
            if(y > 30)
            {
            	Location location = new Location(Bukkit.getWorld(worldInfo.getUID()), x, y+1, z);
                List<Material> grass = biomeGrass.getOrDefault(limitedRegion.getBiome(location), Arrays.asList(Material.GRASS, Material.FERN));
                
                if(limitedRegion.getBiome(location) == Biome.PLAINS)
                {
                	if(random.nextInt(100) < 80) // biome plaine = 80% d'herbe
                	{
                		if (grass.size() > 0 && baseForGrass.contains(limitedRegion.getType(x, y, z)))
                    	{
                			if(limitedRegion.getType(x, y+1, z) == Material.AIR) { limitedRegion.setType(x, y+1, z, grass.get(random.nextInt(grass.size()))); }
                        }
                	}
                }
                else
                {
                	if(random.nextInt(100) < 50) // autres biomes = 50% d'herbe
                	{
                    	if (grass.size() > 0 && baseForGrass.contains(limitedRegion.getType(x, y, z)))
                    	{
                            if(limitedRegion.getType(x, y+1, z) == Material.AIR) { limitedRegion.setType(x, y+1, z, grass.get(random.nextInt(grass.size()))); }
                        }
                	}
                }
            }
        }
    }
}
