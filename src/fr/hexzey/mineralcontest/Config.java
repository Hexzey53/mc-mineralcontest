package fr.hexzey.mineralcontest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.hexzey.mineralcontest.enumerators.MineralTeam;

public class Config
{
	/**
	 * Cette classe contient tous les éléments liés à la configuration du plugin
	 */
    private File ConfigFile = new File("plugins/MineralContest/config.yml");
    private FileConfiguration config = YamlConfiguration.loadConfiguration(ConfigFile);
    
    //////////////////////
    // ATTRIBUTS PRIVES //
    //////////////////////
    private Location lobbyLocation;
    private int borderSize;
    
    private Location center;
    private Location corner1;
    private Location corner2;
    private Location chestLocation;
    
    private Location redCorner1;
    private Location redCorner2;
    private Location greenCorner1;
    private Location greenCorner2;
    private Location blueCorner1;
    private Location blueCorner2;
    private Location yellowCorner1;
    private Location yellowCorner2;
    
    private Location redSpawn;
    private Location greenSpawn;
    private Location blueSpawn;
    private Location yellowSpawn;
    
    private Location redArenaTp;
    private Location greenArenaTp;
    private Location blueArenaTp;
    private Location yellowArenaTp;
    
    private int gameDuration;
    private int border_start_reduction;
    private int border_end_reduction;
    private int startChickens;
    
    
    public Config()
    {
    	this.lobbyLocation = null;
    	this.borderSize = 700; // valeur par défaut
    	
    	this.center = null;
    	this.corner1 = null;
    	this.corner2 = null;
    	this.chestLocation = null;
    	
    	this.redCorner1 = null;
    	this.redCorner2 = null;
    	this.greenCorner1 = null;
    	this.greenCorner2 = null;
    	this.blueCorner1 = null;
    	this.blueCorner2 = null;
    	this.yellowCorner1 = null;
    	this.yellowCorner2 = null;
    	
    	this.redSpawn = null;
    	this.greenSpawn = null;
    	this.blueSpawn = null;
    	this.yellowSpawn = null;
    	
    	this.gameDuration = 1800;
    	this.border_start_reduction = 180;
    	this.border_end_reduction = 120;
    	this.startChickens = 90;
    	
    	this.loadFromFile();
    }
    
    ////////////////
    // ACCESSEURS //
    ////////////////
    public Location getLobbyLocation() { return this.lobbyLocation; }
    public int getBorderSize() { return this.borderSize; }
    public Location getChestLocation()
    {
    	World finalWorld = Bukkit.getWorld("mineralmap");
    	
    	int realX = (int)this.chestLocation.getX() - this.center.getBlockX();
		int realY = (int)this.chestLocation.getY() - (this.center.getBlockY() - (44+3));
		int realZ = (int)this.chestLocation.getZ() - this.center.getBlockZ();
    	
    	return new Location(finalWorld, realX, realY, realZ);
    }
    
    public int getGameDuration() { return this.gameDuration; }
    public int getBorderStartReductionTime() { return this.border_start_reduction; }
    public int getBorderEndReductionTime() { return this.border_end_reduction; }
    public int getChickensTime() { return this.startChickens; }
    
    //////////////////////
    // METHODES PRIVEES //
    //////////////////////
    private void loadFromFile()
    {
    	try
    	{
    		// 1. Configurer l'emplacement du lobby
	    	ConfigurationSection lobbyConfig = config.getConfigurationSection("lobby");
	    	
	    	String worldName = null;
	    	try {
	    		worldName = lobbyConfig.getString("world");
	    	} catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : monde du lobby non défini");
	    		return;
	    	}
	    	World world = null;
	    	try {
	    		world = Bukkit.getWorld(worldName);
	    		if(world == null) throw new Exception();
	    	} catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : le monde du lobby n'existe pas");
	    		return;
	    	}
	    	
	    	Double x = null;
	    	Double y = null;
	    	Double z = null;
	    	try { x = lobbyConfig.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du lobby est incorrecte");
	    		return;
	    	}
	    	try { y = lobbyConfig.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du lobby est incorrecte");
	    		return;
	    	}
	    	try { z = lobbyConfig.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du lobby est incorrecte");
	    		return;
	    	}
	    	this.lobbyLocation = new Location(world, x, y, z);
	    	
	    	// 2. Configurer la largeur de la bordure en jeu
	    	ConfigurationSection mapConfig = config.getConfigurationSection("map");
	    	
	    	int borderSize = 0;
	    	try {
	    		borderSize = mapConfig.getInt("border");
	    		if(borderSize < 1) throw new Exception();
	    	} catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la bordure de la carte est incorrecte");
	    		return;
	    	}
	    	this.borderSize = borderSize;
	    	
	    	// 3. Configurer le template utilisé pour générer le middle
	    	ConfigurationSection middleConfig = config.getConfigurationSection("middle");
	    	worldName = null;
	    	try {
	    		worldName = lobbyConfig.getString("world");
	    	} catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : monde du schema pour le middle non défini");
	    		return;
	    	}
	    	world = null;
	    	try {
	    		world = Bukkit.getWorld(worldName);
	    		if(world == null) throw new Exception();
	    	} catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : le monde du schema pour le middle n'existe pas");
	    		return;
	    	}
	    	// 3.1 Point central
	    	ConfigurationSection centerConfig = middleConfig.getConfigurationSection("center");
	    	x = null; y = null; z = null;
	    	try { x = centerConfig.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du center pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y = centerConfig.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du center pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z = centerConfig.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du center pour le middle est incorrecte");
	    		return;
	    	}
	    	this.center = new Location(world, x, y, z);
	    	// 3.2 Coin numéro 1/2
	    	ConfigurationSection corner1Config = middleConfig.getConfigurationSection("corner1");
	    	x = null; y = null; z = null;
	    	try { x = corner1Config.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du corner1 pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y = corner1Config.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du corner1 pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z = corner1Config.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du corner1 pour le middle est incorrecte");
	    		return;
	    	}
	    	this.corner1 = new Location(world, x, y, z);
	    	// 3.2 Coin numéro 2/2
	    	ConfigurationSection corner2Config = middleConfig.getConfigurationSection("corner2");
	    	x = null; y = null; z = null;
	    	try { x = corner2Config.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du corner2 pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y = corner2Config.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du corner2 pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z = corner2Config.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du corner2 pour le middle est incorrecte");
	    		return;
	    	}
	    	this.corner2 = new Location(world, x, y, z);
	    	// 3.3 Emplacement du coffre
	    	ConfigurationSection chestConfig = middleConfig.getConfigurationSection("chest");
	    	x = null; y = null; z = null;
	    	try { x = chestConfig.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du chest pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y = chestConfig.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du chest pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z = chestConfig.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du chest pour le middle est incorrecte");
	    		return;
	    	}
	    	this.chestLocation = new Location(world, x, y, z);
	    	// 3.4 Corners de la base rouge (équipe 1/4)
	    	ConfigurationSection redConfig = middleConfig.getConfigurationSection("red");
	    	Double x1 = null; Double y1 = null; Double z1 = null;
	    	Double x2 = null; Double y2 = null; Double z2 = null;
	    	try { x1 = redConfig.getDouble("x1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X1 de la base rouge pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y1 = redConfig.getDouble("y1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y1 de la base rouge pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z1 = redConfig.getDouble("z1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z1 de la base rouge pour le middle est incorrecte");
	    		return;
	    	}
	    	try { x2 = redConfig.getDouble("x2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X2 de la base rouge pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y2 = redConfig.getDouble("y2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y2 de la base rouge pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z2 = redConfig.getDouble("z2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z2 de la base rouge pour le middle est incorrecte");
	    		return;
	    	}
	    	this.redCorner1 = new Location(world, x1, y1, z1);
	    	this.redCorner2 = new Location(world, x2, y2, z2);
	    	// 3.4 Corners de la base verte (équipe 2/4)
	    	ConfigurationSection greenConfig = middleConfig.getConfigurationSection("green");
	    	x1 = null; y1 = null; z1 = null; x2 = null; y2 = null; z2 = null;
	    	try { x1 = greenConfig.getDouble("x1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X1 de la base verte pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y1 = greenConfig.getDouble("y1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y1 de la base verte pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z1 = greenConfig.getDouble("z1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z1 de la base verte pour le middle est incorrecte");
	    		return;
	    	}
	    	try { x2 = greenConfig.getDouble("x2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X2 de la base verte pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y2 = greenConfig.getDouble("y2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y2 de la base verte pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z2 = greenConfig.getDouble("z2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z2 de la base verte pour le middle est incorrecte");
	    		return;
	    	}
	    	this.greenCorner1 = new Location(world, x1, y1, z1);
	    	this.greenCorner2 = new Location(world, x2, y2, z2);
	    	// 3.4 Corners de la base bleue (équipe 3/4)
	    	ConfigurationSection blueConfig = middleConfig.getConfigurationSection("blue");
	    	x1 = null; y1 = null; z1 = null; x2 = null; y2 = null; z2 = null;
	    	try { x1 = blueConfig.getDouble("x1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X1 de la base bleue pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y1 = blueConfig.getDouble("y1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y1 de la base bleue pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z1 = blueConfig.getDouble("z1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z1 de la base bleue pour le middle est incorrecte");
	    		return;
	    	}
	    	try { x2 = blueConfig.getDouble("x2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X2 de la base bleue pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y2 = blueConfig.getDouble("y2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y2 de la base bleue pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z2 = blueConfig.getDouble("z2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z2 de la base bleue pour le middle est incorrecte");
	    		return;
	    	}
	    	this.blueCorner1 = new Location(world, x1, y1, z1);
	    	this.blueCorner2 = new Location(world, x2, y2, z2);
	    	// 3.4 Corners de la base jaune (équipe 4/4)
	    	ConfigurationSection yellowConfig = middleConfig.getConfigurationSection("yellow");
	    	x1 = null; y1 = null; z1 = null; x2 = null; y2 = null; z2 = null;
	    	try { x1 = yellowConfig.getDouble("x1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X1 de la base jaune pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y1 = yellowConfig.getDouble("y1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y1 de la base jaune pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z1 = yellowConfig.getDouble("z1"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z1 de la base jaune pour le middle est incorrecte");
	    		return;
	    	}
	    	try { x2 = yellowConfig.getDouble("x2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X2 de la base jaune pour le middle est incorrecte");
	    		return;
	    	}
	    	try { y2 = yellowConfig.getDouble("y2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y2 de la base jaune pour le middle est incorrecte");
	    		return;
	    	}
	    	try { z2 = yellowConfig.getDouble("z2"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z2 de la base jaune pour le middle est incorrecte");
	    		return;
	    	}
	    	this.yellowCorner1 = new Location(world, x1, y1, z1);
	    	this.yellowCorner2 = new Location(world, x2, y2, z2);
	    	// 3.5 Spawn de l'équipe rouge (équipe 1/4)
	    	ConfigurationSection redSpawn = redConfig.getConfigurationSection("spawnpoint");
	    	x = null; y = null; z = null; Float yaw = null;
	    	try { x = redSpawn.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du spawn de l'équipe rouge est incorrecte");
	    		return;
	    	}
	    	try { y = redSpawn.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du spawn de l'équipe rouge est incorrecte");
	    		return;
	    	}
	    	try { z = redSpawn.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du spawn de l'équipe rouge est incorrecte");
	    		return;
	    	}
	    	try { z = redSpawn.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du spawn de l'équipe rouge est incorrecte");
	    		return;
	    	}
	    	try { yaw = Float.valueOf(redSpawn.getString("yaw")); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée YAW du spawn de l'équipe rouge est incorrecte");
	    		return;
	    	}
	    	this.redSpawn = new Location(world, x, y, z, yaw, 0f);
	    	// 3.5 Spawn de l'équipe verte (équipe 2/4)
	    	ConfigurationSection greenSpawn = greenConfig.getConfigurationSection("spawnpoint");
	    	x = null; y = null; z = null; yaw = null;
	    	try { x = greenSpawn.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du spawn de l'équipe verte est incorrecte");
	    		return;
	    	}
	    	try { y = greenSpawn.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du spawn de l'équipe verte est incorrecte");
	    		return;
	    	}
	    	try { z = greenSpawn.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du spawn de l'équipe verte est incorrecte");
	    		return;
	    	}
	    	try { z = greenSpawn.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du spawn de l'équipe verte est incorrecte");
	    		return;
	    	}
	    	try { yaw = Float.valueOf(greenSpawn.getString("yaw")); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée YAW du spawn de l'équipe verte est incorrecte");
	    		return;
	    	}
	    	this.greenSpawn = new Location(world, x, y, z, yaw, 0f);
	    	// 3.5 Spawn de l'équipe bleue (équipe 3/4)
	    	ConfigurationSection blueSpawn = blueConfig.getConfigurationSection("spawnpoint");
	    	x = null; y = null; z = null; yaw = null;
	    	try { x = blueSpawn.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du spawn de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { y = blueSpawn.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du spawn de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { z = blueSpawn.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du spawn de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { z = blueSpawn.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du spawn de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { yaw = Float.valueOf(blueSpawn.getString("yaw")); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée YAW du spawn de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	this.blueSpawn = new Location(world, x, y, z, yaw, 0f);
	    	// 3.5 Spawn de l'équipe jaune (équipe 4/4)
	    	ConfigurationSection yellowSpawn = yellowConfig.getConfigurationSection("spawnpoint");
	    	x = null; y = null; z = null; yaw = null;
	    	try { x = yellowSpawn.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du spawn de l'équipe jaune est incorrecte");
	    		return;
	    	}
	    	try { y = yellowSpawn.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du spawn de l'équipe jaune est incorrecte");
	    		return;
	    	}
	    	try { z = yellowSpawn.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du spawn de l'équipe jaune est incorrecte");
	    		return;
	    	}
	    	try { z = yellowSpawn.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du spawn de l'équipe jaune est incorrecte");
	    		return;
	    	}
	    	try { yaw = Float.valueOf(yellowSpawn.getString("yaw")); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée YAW du spawn de l'équipe jaune est incorrecte");
	    		return;
	    	}
	    	this.yellowSpawn = new Location(world, x, y, z, yaw, 0f);
	    	// 3.6 Teleportation dans l'arène de l'équipe rouge (équipe 1/4)
	    	ConfigurationSection redArenaTp = redConfig.getConfigurationSection("arena");
	    	x = null; y = null; z = null; yaw = null;
	    	try { x = redArenaTp.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du point d'arène de l'équipe rouge est incorrecte");
	    		return;
	    	}
	    	try { y = redArenaTp.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du point d'arène de l'équipe rouge est incorrecte");
	    		return;
	    	}
	    	try { z = redArenaTp.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du point d'arène de l'équipe rouge est incorrecte");
	    		return;
	    	}
	    	try { z = redArenaTp.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du point d'arène de l'équipe rouge est incorrecte");
	    		return;
	    	}
	    	try { yaw = Float.valueOf(redArenaTp.getString("yaw")); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée YAW du point d'arène de l'équipe rouge est incorrecte");
	    		return;
	    	}
	    	this.redArenaTp = new Location(world, x, y, z, yaw, 0f);
	    	// 3.6 Teleportation dans l'arène de l'équipe verte (équipe 2/4)
	    	ConfigurationSection greenArenaTp = greenConfig.getConfigurationSection("arena");
	    	x = null; y = null; z = null; yaw = null;
	    	try { x = greenArenaTp.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du point d'arène de l'équipe verte est incorrecte");
	    		return;
	    	}
	    	try { y = greenArenaTp.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du point d'arène de l'équipe verte est incorrecte");
	    		return;
	    	}
	    	try { z = greenArenaTp.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du point d'arène de l'équipe verte est incorrecte");
	    		return;
	    	}
	    	try { z = greenArenaTp.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du point d'arène de l'équipe verte est incorrecte");
	    		return;
	    	}
	    	try { yaw = Float.valueOf(greenArenaTp.getString("yaw")); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée YAW du point d'arène de l'équipe verte est incorrecte");
	    		return;
	    	}
	    	this.greenArenaTp = new Location(world, x, y, z, yaw, 0f);
	    	// 3.6 Teleportation dans l'arène de l'équipe bleue (équipe 3/4)
	    	ConfigurationSection blueArenaTp = blueConfig.getConfigurationSection("arena");
	    	x = null; y = null; z = null; yaw = null;
	    	try { x = blueArenaTp.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du point d'arène de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { y = blueArenaTp.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du point d'arène de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { z = blueArenaTp.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du point d'arène de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { z = blueArenaTp.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du point d'arène de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { yaw = Float.valueOf(blueArenaTp.getString("yaw")); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée YAW du point d'arène de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	this.blueArenaTp = new Location(world, x, y, z, yaw, 0f);
	    	// 3.6 Teleportation dans l'arène de l'équipe jaune (équipe 4/4)
	    	ConfigurationSection yellowArenaTp = yellowConfig.getConfigurationSection("arena");
	    	x = null; y = null; z = null; yaw = null;
	    	try { x = yellowArenaTp.getDouble("x"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée X du point d'arène de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { y = yellowArenaTp.getDouble("y"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Y du point d'arène de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { z = yellowArenaTp.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du point d'arène de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { z = yellowArenaTp.getDouble("z"); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée Z du point d'arène de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	try { yaw = Float.valueOf(yellowArenaTp.getString("yaw")); } catch(Exception e) {
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : la coordonnée YAW du point d'arène de l'équipe bleue est incorrecte");
	    		return;
	    	}
	    	this.yellowArenaTp = new Location(world, x, y, z, yaw, 0f);
    	}
    	catch(Exception e)
    	{
    		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Echec du chargement de la configuration");
    		e.printStackTrace();
    	}
    	// 3.7. Configurer la partie (temps des événements)
    	ConfigurationSection gameConfig = config.getConfigurationSection("game");
    	try {
    		this.gameDuration = gameConfig.getInt("duration");
    	} catch(Exception e) {
    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : le paramètre 'game.duration' est incorrect");
    		return;
    	}
    	try {
    		this.border_start_reduction = gameConfig.getInt("border_start_reduction");
    	} catch(Exception e) {
    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : le paramètre 'game.border_start_reduction' est incorrect");
    		return;
    	}
    	try {
    		this.border_end_reduction = gameConfig.getInt("border_end_reduction");
    	} catch(Exception e) {
    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : le paramètre 'game.border_end_reduction' est incorrect");
    		return;
    	}
    	try {
    		this.startChickens = gameConfig.getInt("start_chickens");
    	} catch(Exception e) {
    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : le paramètre 'game.start_chickens' est incorrect");
    		return;
    	}
    	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Configuration chargee !");
    }
    ////////////////////////
    // METHODES PUBLIQUES //
    ////////////////////////
    public void placeMiddle()
    {
    	if(this.corner1 == null || this.corner2 == null) return;
    	World world = this.corner1.getWorld();
    	World finalWorld = Bukkit.getWorld("mineralmap");
    	// définir les minimums et maximums
    	int minX = (int)Math.min(this.corner1.getX(), this.corner2.getX());
    	int minY = (int)Math.min(this.corner1.getY(), this.corner2.getY());
    	int minZ = (int)Math.min(this.corner1.getZ(), this.corner2.getZ());
    	
    	int maxX = (int)Math.max(this.corner1.getX(), this.corner2.getX());
    	int maxY = (int)Math.max(this.corner1.getY(), this.corner2.getY());
    	int maxZ = (int)Math.max(this.corner1.getZ(), this.corner2.getZ());
    	
    	for(Player player : Bukkit.getOnlinePlayers()) { player.sendMessage(ChatColor.AQUA + "Placement du middle..."); }
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Placement du middle...");
    	
    	for(int x=minX; x<=maxX; x++) {
    		for(int y=minY; y<=maxY; y++) {
    			for(int z=minZ; z<=maxZ; z++) {
    				Block source = world.getBlockAt(x, y, z);
    				Material sourceMaterial = source.getType();
    				BlockData sourceData = source.getBlockData();
    				
    				int realX = x - this.center.getBlockX();
    				int realY = y - (this.center.getBlockY() - (44+3));
    				int realZ = z - this.center.getBlockZ();
    				
    				finalWorld.getBlockAt(realX, realY, realZ).setType(sourceMaterial);
    				finalWorld.getBlockAt(realX, realY, realZ).setBlockData(sourceData);
				}
    		}
    	}
    	
    	for(Player player : Bukkit.getOnlinePlayers()) { player.sendMessage(ChatColor.GREEN + "Placement terminé..."); }
    	for(Player player : Bukkit.getOnlinePlayers()) { player.sendMessage(ChatColor.GREEN + "Un administrateur peut démarrer une partie en utilisant '/game start'."); }
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Placement termine !");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Un administrateur peut demarrer une partie en utilisant '/game start'.");
    }
    
    public ArrayList<Location> GetCornersFor(MineralTeam team)
    {
    	if(team == null || team == MineralTeam.Spectator) return null;
    	Location source1 = null; Location source2 = null;
    	// 1. Récupérer les corners en fonction de l'équipe
    	if(team == MineralTeam.Red) {
    		source1 = this.redCorner1;
    		source2 = this.redCorner2;
    	} else if(team == MineralTeam.Green) {
    		source1 = this.greenCorner1;
    		source2 = this.greenCorner2;
    	} else if(team == MineralTeam.Blue) {
    		source1 = this.blueCorner1;
    		source2 = this.blueCorner2;
    	} else if(team == MineralTeam.Yellow) {
    		source1 = this.yellowCorner1;
    		source2 = this.yellowCorner2;
    	}
    	
    	// 2. Adapter les coordonnées pour correspondre à la map (centre 0,0)
    	World world = Bukkit.getWorld("mineralmap");
    	int x1 = (int)source1.getX() - this.center.getBlockX();
    	int y1 = (int)source1.getY() - (this.center.getBlockY() - (44+3));
    	int z1 = (int)source1.getZ() - this.center.getBlockZ();
    	int x2 = (int)source2.getX() - this.center.getBlockX();
    	int y2 = (int)source2.getY() - (this.center.getBlockY() - (44+3));
    	int z2 = (int)source2.getZ() - this.center.getBlockZ();
    	
    	Location loc1 = new Location(world, x1, y1, z1);
    	
    	Location loc2 = new Location(world, x2, y2, z2);
    	
    	ArrayList<Location> result = new ArrayList<Location>();
    	result.add(loc1); result.add(loc2);
    	return result;
    }
    
    public Location getSpawnFor(MineralTeam team) {
    	if(team == null || team == MineralTeam.Spectator) return null;
    	// 1. Récupérer le point de spawn en fonction de l'équipe
    	Location source = null;
    	if(team == MineralTeam.Red) {
    		source = this.redSpawn;
    	} else if(team == MineralTeam.Green) {
    		source = this.greenSpawn;
    	} else if(team == MineralTeam.Blue) {
    		source = this.blueSpawn;
    	} else if(team == MineralTeam.Yellow) {
    		source = this.yellowSpawn;
    	}
    	// 2. Adapter les coordonnées pour correspondre à la map (centre 0,0)
    	World world = Bukkit.getWorld("mineralmap");
    	Double x = source.getX() - this.center.getBlockX();
    	Double y = source.getY() - (this.center.getBlockY() - (44+3));
    	Double z = source.getZ() - this.center.getBlockZ();
    	Float yaw = source.getYaw();
    	return new Location(world, x, y, z, yaw, 0f);
    }
    
    public Location getArenaTpFor(MineralTeam team) {
    	if(team == null || team == MineralTeam.Spectator) return null;
    	Location source = null;
    	if(team == MineralTeam.Red) {
    		source = this.redArenaTp;
    	} else if(team == MineralTeam.Green) {
    		source = this.greenArenaTp;
    	} else if(team == MineralTeam.Blue) {
    		source = this.blueArenaTp;
    	} else if(team == MineralTeam.Yellow) {
    		source = this.yellowArenaTp;
    	}
    	// 2. Adapter les coordonnées pour correspondre à la map (centre 0,0)
    	World world = Bukkit.getWorld("mineralmap");
    	Double x = source.getX() - this.center.getBlockX();
    	Double y = source.getY() - (this.center.getBlockY() - (44+3));
    	Double z = source.getZ() - this.center.getBlockZ();
    	Float yaw = source.getYaw();
    	return new Location(world, x, y, z, yaw, 0f);
    }
    
    /*
    public HashMap<String, Integer> getCavesConfig() {
    
    }
    */
}
