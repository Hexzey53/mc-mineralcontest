package fr.hexzey.mineralcontest;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.hexzey.mineralcontest.tools.PlayerFreeze;

public class Main extends JavaPlugin
{
	/////////////////////////
	// ATTRIBUTS STATIQUES //
	/////////////////////////
	private static Plugin plugin;
	public static Plugin getPlugin() { return Main.plugin; }
	
	private static Config configuration;
	public static Config getConfiguration() { return Main.configuration; }
	
	public static MineralGame mineralGame;
	public static PlayerFreeze playerFreeze;
	
	public static boolean mapGenerated;
	
	////////////////////////////
	// AU DEMARRAGE DU PLUGIN //
	////////////////////////////
	@Override
	public void onEnable()
	{
		Main.plugin = this;
		
		// charger la configuration
		Main.configuration = new Config();
		
		// créer une partie vide
		Main.mineralGame = new MineralGame();
		
		// initialiser le gestionnaire de freeze des joueurs
		Main.playerFreeze = new PlayerFreeze();
		
		// indiquer la map comme non chargée
		Main.mapGenerated = false;
		
		// enregistrement des commandes
		getCommand("mctp").setExecutor(new Commands());
		getCommand("map").setExecutor(new Commands());
		getCommand("game").setExecutor(new Commands());
		getCommand("arene").setExecutor(new Commands());
		// enregistrement des events
		getServer().getPluginManager().registerEvents(new Events(), this);

		// désactiver la sauvegarde des maps (sauf world)
		//Bukkit.getWorld("world_nether").setAutoSave(false); // inutile si le nether est désactivé dans la config du serveur
		Bukkit.getWorld("world_the_end").setAutoSave(false);
	}
	
	//////////////////////////////
	// A L'EXTINCTION DU PLUGIN //
	//////////////////////////////
	@Override
	public void onDisable()
	{
		// désactiver la sauvegarde de toutes les maps (sauf world)
		// Bukkit.unloadWorld(Bukkit.getWorld("world_nether"), false); // inutile si le nether est désactivé dans la config du serveur
		Bukkit.unloadWorld(Bukkit.getWorld("world_the_end"), false);
		Bukkit.unloadWorld(Bukkit.getWorld("mineralmap"), false);
	}
	
	public static void createCustomWorld(String worldName, ChunkGenerator generator)
	{
		/**
		 * Méthode pour créer un nouveau monde et le charger
		 */
		WorldCreator wc = new WorldCreator(worldName);
		wc.environment(World.Environment.NORMAL);
		wc.generateStructures(false);
		wc.generator(generator);
		Bukkit.createWorld(wc);
		Bukkit.getWorld(worldName).getWorldBorder().setCenter(0, 0);
		Bukkit.getWorld(worldName).getWorldBorder().setSize(Main.getConfiguration().getBorderSize());
		Bukkit.getWorld(worldName).getWorldBorder().setWarningDistance(75);
		Bukkit.getWorld(worldName).setSpawnLocation(Main.getConfiguration().getChestLocation());
		Bukkit.getWorld(worldName).setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		
	}
	
	public static void deleteDirectory(File directory) {

	    // if the file is directory or not
	    if(directory.isDirectory()) {
	      File[] files = directory.listFiles();

	      // if the directory contains any file
	      if(files != null) {
	        for(File file : files) {

	          // recursive call if the subdirectory is non-empty
	          deleteDirectory(file);
	        }
	      }
	    }
	    directory.delete();
	  }
}
