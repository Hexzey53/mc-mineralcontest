package fr.hexzey.mineralcontest.populators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import fr.hexzey.mineralcontest.Main;
import fr.hexzey.mineralcontest.tools.GenererGrotte;

public class CavePopulator extends BlockPopulator
{
	/**
	 * Générateur de grottes souterraines
	 */
	@Override
    public void populate(final World world, final Random random, final Chunk source) {
		/*
        GCRandom gcRandom = new GCRandom(source);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 55; y >= 5; y--) {
                    if (gcRandom.isInGiantCave(x, y, z)) {
                        Block block = source.getBlock(x, y, z);
                        block.setType(Material.AIR, false);
                    }
                }
            }
        }
        */
    }
}