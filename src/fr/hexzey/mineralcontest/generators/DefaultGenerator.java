package fr.hexzey.mineralcontest.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import fr.hexzey.mineralcontest.populators.CavePopulator;
import fr.hexzey.mineralcontest.populators.DefaultGrassPopulator;
import fr.hexzey.mineralcontest.populators.DefaultTreePopulator;
import fr.hexzey.mineralcontest.populators.oreveins.CoalVeinsPopulator_High;
import fr.hexzey.mineralcontest.populators.oreveins.CoalVeinsPopulator_Low;
import fr.hexzey.mineralcontest.populators.oreveins.CoalVeinsPopulator_Medium;
import fr.hexzey.mineralcontest.populators.oreveins.CoalVeinsPopulator_VeryHigh;
import fr.hexzey.mineralcontest.populators.oreveins.DiamondVeinsPopulator;
import fr.hexzey.mineralcontest.populators.oreveins.EmeraldVeinsPopulator;
import fr.hexzey.mineralcontest.populators.oreveins.GoldVeinsPopulator;
import fr.hexzey.mineralcontest.populators.oreveins.IronVeinsPopulator_High;
import fr.hexzey.mineralcontest.populators.oreveins.IronVeinsPopulator_Low;
import fr.hexzey.mineralcontest.populators.oreveins.IronVeinsPopulator_Medium;
import fr.hexzey.mineralcontest.populators.oreveins.RedstoneVeinsPopulator;

public class DefaultGenerator extends ChunkGenerator
{
	private SimplexNoiseGenerator noiseGenerator;
	
	public DefaultGenerator()
	{
		this.noiseGenerator = new SimplexNoiseGenerator(new Random()); // random pour avoir un seed aléatoire
	}
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world)
	{
		List<BlockPopulator> populators = new ArrayList<>();
		
		// ici on peut ajouter des populators (modifications appliquées une fois le terrain généré, ex: arbres, minerais)
		populators.add((BlockPopulator)new DefaultTreePopulator());
		populators.add((BlockPopulator)new DefaultGrassPopulator());
		
		return populators;
	}
	
	@Override
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome)
	{
		ChunkData chunkData = createChunkData(world);
		// échelle pour ajuster l'apparence du terrain généré
		double scaleX = 0.013;
		double scaleY = 0.0055;
		// offset pour ajouter de la variété entre les chunks
		double offsetX = x*16*scaleX;
		double offsetZ = z*16*scaleY;
		
		// remplir le chunk avec l'algorithme du bruit de Perlin
		for(int i=0; i<16; i++)
		{
			for(int j=0; j<16; j++)
			{
				// obtenir la hauteur du bruit de perlin aux coordonnées
				double noiseValue = noiseGenerator.noise(offsetX+i*scaleX, offsetZ+j*scaleY);
				// convertir le bruit en hauteur dans le monde
				int height = (int) (noiseValue*10)+44;
				// zone plate au centre de la carte
				double distanceToCenter = Math.sqrt( Math.pow(x*16+i, 2) + Math.pow(z*16+j, 2) );
				if(distanceToCenter < 48) {
					height = 44;
					biome.setBiome(i, j, Biome.SAVANNA_PLATEAU);
				}
				else if(distanceToCenter < 48+10) {
					distanceToCenter -= 48;
					int heightDiff = height - 44;
					if(heightDiff > distanceToCenter) {
						height = 44 + (int)distanceToCenter;
					} else if(heightDiff < -1*distanceToCenter) {
						height = 44 - (int)distanceToCenter;
					}
					biome.setBiome(i, j, Biome.SAVANNA_PLATEAU);
				}
				
				// placer les blocs en fonction de la hauteur
				chunkData.setBlock(i, 0, j, Material.BEDROCK);
				for(int y=1; y<height; y++)
				{
					chunkData.setBlock(i, y, j, Material.STONE);
				}
				// placer une couche de dirt et d'herbe par dessus
				chunkData.setBlock(i, height, j, Material.DIRT);
				chunkData.setBlock(i, height+1, j, Material.DIRT);
				chunkData.setBlock(i, height+2, j, Material.GRASS_BLOCK);
			}
		}
		
		return chunkData;
	}
}
