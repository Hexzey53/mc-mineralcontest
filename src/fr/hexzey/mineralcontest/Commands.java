package fr.hexzey.mineralcontest;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.hexzey.mineralcontest.enumerators.GameState;
import fr.hexzey.mineralcontest.enumerators.MineralTeam;
import fr.hexzey.mineralcontest.generators.DefaultGenerator;

public class Commands implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("mctp"))
		{
			if(sender instanceof Player == false) {
				sender.sendMessage(ChatColor.RED + "Erreur : vous devez etre un joueur pour utiliser cette commande.");
				return false;
			}
			Player player = (Player)sender;
			
			// récupérer le monde cible
			String worldName = args[0];
			World w;
			try {
				w = Bukkit.getWorld(worldName);
			} catch(Exception e) {
				player.sendMessage(ChatColor.RED + "Téléportation impossible : ce monde n'existe pas.");
				return false;
			}
			
			// récupérer les coordonnées cibles
			Double x, y, z;
			try {
				x = Double.parseDouble(args[1]);
				y = Double.parseDouble(args[2]);
				z = Double.parseDouble(args[3]);
			} catch(Exception e) {
				player.sendMessage(ChatColor.RED + "Téléportation impossible : les coordonnées sont incorrectes.");
				return false;
			}
			
			Location loc = new Location(w, x, y ,z);
			player.teleport(loc);
			player.sendMessage(ChatColor.GREEN + "Vous avez été téléporté !");
		}
		else if(cmd.getName().equalsIgnoreCase("map")) {
			if(args[0].equalsIgnoreCase("create"))
			{
				if(Main.mineralGame.getGameState() != GameState.Waiting) {
					if(sender instanceof Player) {
						Player player = (Player)sender;
						player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
					}
					sender.sendMessage(ChatColor.RED + "Action impossible : une partie est en cours.");
					return false;
				}
				// procédure de création de la nouvelle map
				Main.mapGenerated = false;
				// 1. décharger la map si active
				sender.sendMessage(ChatColor.AQUA + "Suppression de l'ancienne carte...");
				for(World world : Bukkit.getWorlds())
				{
					if(world.getName().equalsIgnoreCase("mineralmap"))	
					{
						for(Player player : Bukkit.getOnlinePlayers())
						{
							if(player.getLocation().getWorld().equals(world)) { player.teleport(Main.getConfiguration().getLobbyLocation()); }
						}
						Bukkit.unloadWorld("mineralmap", false);
						break;
					}
				}
				// 2. supprimer la sauvegarde
				File oldMapDirectory = new File("mineralmap");
				Main.deleteDirectory(oldMapDirectory);
				sender.sendMessage(ChatColor.AQUA + "Ancienne carte supprimée.");
				for(Player player : Bukkit.getOnlinePlayers()) { if(sender.equals(player) == false) player.sendMessage(ChatColor.AQUA + "Génération de la nouvelle carte..."); }
				sender.sendMessage(ChatColor.AQUA + "Generation de la nouvelle carte...");
				// 3. générer la nouvelle map
				Main.createCustomWorld("mineralmap", new DefaultGenerator());
				for(Player player : Bukkit.getOnlinePlayers()) { if(sender.equals(player) == false) player.sendMessage(ChatColor.GREEN + "Génération terminée !"); }
				sender.sendMessage(ChatColor.GREEN + "Generation terminee !");
				for(Player player : Bukkit.getOnlinePlayers()) { if(sender.equals(player) == false) player.sendMessage(ChatColor.AQUA + "Préchargement de la carte..."); }
				sender.sendMessage(ChatColor.AQUA + "Prechargement de la carte...");
				// 2. définir les chunks min et max sur l'axe X et Y
				int borderSize = Main.getConfiguration().getBorderSize();
				int chunksPerSide = (int)Math.ceil(borderSize / 16);
				MineralMapPreloader.minChunkX = ((int)Math.ceil(chunksPerSide / 2) * -1) -1;
				MineralMapPreloader.maxChunkX = (int)Math.ceil(chunksPerSide / 2) +1;
				MineralMapPreloader.minChunkY = ((int)Math.ceil(chunksPerSide / 2) * -1) -1;
				MineralMapPreloader.maxChunkY = (int)Math.ceil(chunksPerSide / 2) +1;
				
				//MineralMapPreloader.loadChunk(MineralMapPreloader.minChunkX, MineralMapPreloader.minChunkY);
				MineralMapPreloader.buildSpawn();
				return false;
			}
		}
		else if(cmd.getName().equalsIgnoreCase("game")) {
			String action = null;
			try {
				action = args[0];
			} catch(Exception e) {
				sender.sendMessage(ChatColor.RED + "Commande inconnue. Utilisez '/game start' ou '/game end' ou '/game chest'.");
				return false;
			}
			if(action.equalsIgnoreCase("start")) {
				gameStart();
				return false;
			} else if(action.equalsIgnoreCase("end")) {
				if(Main.mineralGame.getGameState() == GameState.Waiting) {
					sender.sendMessage(ChatColor.RED + "Action impossible : aucune partie en cours.");
					return false;
				}
				gameEnd();
				return false;
			} else if(action.equalsIgnoreCase("chest")) {
				if(Main.mineralGame.getGameState() == GameState.Waiting) {
					sender.sendMessage(ChatColor.RED + "Action impossible : aucune partie en cours.");
					return false;
				}
				Main.mineralGame.setSecondsBeforeNextChest(11);
				return false;
			} else {
				sender.sendMessage(ChatColor.RED + "Commande inconnue. Utilisez '/game start' ou '/game end' ou '/game chest'.");
				return false;
			}
		}
		else if(cmd.getName().equalsIgnoreCase("arene")) {
			if(sender instanceof Player == false) {
				sender.sendMessage(ChatColor.RED + "Erreur : vous devez etre un joueur pour utiliser cette commande.");
				return false;
			}
			Player player = (Player)sender;
			MineralTeam team = Main.mineralGame.getMineralTeam(player);
			if(team == null || team == MineralTeam.Spectator) {
				sender.sendMessage(ChatColor.RED + "Erreur : vous devez être dans une équipe pour utiliser cette commande.");
				return false;
			}
			if(Main.mineralGame.canTpToArena() == false) {
				sender.sendMessage(ChatColor.RED + "Impossible de téléporter l'équipe dans l'arène maintenant.");
			}
			Main.mineralGame.tpTeamToArena(team);
			return false;
		}
		sender.sendMessage(ChatColor.RED + "Commande inconnue.");
		return false;
	}
	
	public void gameStart() {
		if(Main.mapGenerated == false) {
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
				player.sendMessage(ChatColor.RED + "Action impossible : la carte n'est pas générée. Utilisez /map create.");				
			}
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Action impossible : la carte n'est pas generee. Utilisez /map create.");
			return;
		} else if(Main.mineralGame.getGameState() != GameState.Waiting) {
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
				player.sendMessage(ChatColor.RED + "Action impossible : une partie est en cours.");			
			}
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Action impossible : une partie est en cours.");
			return;
		} else if(MineralMapPreloader.isLoading == true) {
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
				player.sendMessage(ChatColor.RED + "Action impossible : la carte est en cours de chargement.");		
			}
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Action impossible : la carte est en cours de chargement.");
			return;
		}
		try {
			World world = Bukkit.getWorld("mineralmap");
			if(world == null) throw new Exception();
		} catch(Exception e) {
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
				player.sendMessage(ChatColor.RED + "Action impossible : la carte n'est pas générée. Utilisez /map create.");	
			}
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Action impossible : la carte n'est pas generee. Utilisez /map create.");
			return;
		}
		
		// A FAIRE : AU MOINS 2 EQUIPES DE 1 JOUEUR
		// A FAIRE : AU MOINS 2 EQUIPES DE 1 JOUEUR
		// A FAIRE : AU MOINS 2 EQUIPES DE 1 JOUEUR
		
		// charger les corners pour la base de chaque équipe
		Main.mineralGame.setCornersForTeam(MineralTeam.Red, Main.getConfiguration().GetCornersFor(MineralTeam.Red));
		Main.mineralGame.setCornersForTeam(MineralTeam.Green, Main.getConfiguration().GetCornersFor(MineralTeam.Green));
		Main.mineralGame.setCornersForTeam(MineralTeam.Blue, Main.getConfiguration().GetCornersFor(MineralTeam.Blue));
		Main.mineralGame.setCornersForTeam(MineralTeam.Yellow, Main.getConfiguration().GetCornersFor(MineralTeam.Yellow));
		
		// charger les points de spawn pour chaque equipe
		Main.mineralGame.setSpawnPoint(MineralTeam.Red, Main.getConfiguration().getSpawnFor(MineralTeam.Red));
		Main.mineralGame.setSpawnPoint(MineralTeam.Green, Main.getConfiguration().getSpawnFor(MineralTeam.Green));
		Main.mineralGame.setSpawnPoint(MineralTeam.Blue, Main.getConfiguration().getSpawnFor(MineralTeam.Blue));
		Main.mineralGame.setSpawnPoint(MineralTeam.Yellow, Main.getConfiguration().getSpawnFor(MineralTeam.Yellow));
		
		// charger les points de tp vers l'arene pour chaque equipe
		Main.mineralGame.setArenaTpForTeam(MineralTeam.Red, Main.getConfiguration().getArenaTpFor(MineralTeam.Red));
		Main.mineralGame.setArenaTpForTeam(MineralTeam.Green, Main.getConfiguration().getArenaTpFor(MineralTeam.Green));
		Main.mineralGame.setArenaTpForTeam(MineralTeam.Blue, Main.getConfiguration().getArenaTpFor(MineralTeam.Blue));
		Main.mineralGame.setArenaTpForTeam(MineralTeam.Yellow, Main.getConfiguration().getArenaTpFor(MineralTeam.Yellow));
		
		// charger le point de spawn du coffre au centre de l'arène
		Main.mineralGame.setChestLocation(Main.getConfiguration().getChestLocation());
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
			player.sendMessage(ChatColor.GREEN + "Démarrage de la partie...");
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Demarrage de la partie...");
		Main.mineralGame.Prepare();
	}
	
	public void gameEnd() {
		for(Player player : Bukkit.getOnlinePlayers())
		{
			player.sendMessage(ChatColor.RED +  "La partie a été arrêtée de force.");
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "La partie a ete arretee de force.");
		Main.mineralGame.End();
	}
}

class MineralMapPreloader
{
	public static boolean isLoading = false;
	
	public static int minChunkX = 0;
	public static int maxChunkX = 0;
	public static int minChunkY = 0;
	public static int maxChunkY = 0;
	public static int chunkX = 0;
	public static int chunkY = 0;
	private static int nbChunksGeneres = 0;
	
	public static void loadChunk(int x, int y)
	{
		nbChunksGeneres++;
		isLoading = true;
		int nbMaxChunk = (int)Math.ceil((maxChunkX*2+1)*(maxChunkY*2+1));
		for(Player player : Bukkit.getOnlinePlayers()) { player.sendMessage(ChatColor.AQUA + "Pré-chargement en cours. " + String.valueOf(nbChunksGeneres) + "/" + String.valueOf(nbMaxChunk)); }
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Pre-chargement en cours. " + String.valueOf(nbChunksGeneres) + "/" + String.valueOf(nbMaxChunk));
		Bukkit.getWorld("mineralmap").getChunkAt(x, y);
		y++;
		if(y > maxChunkY) {
			y=minChunkY;
			x++;
		}
		if(x > maxChunkX) {
			for(Player player : Bukkit.getOnlinePlayers()) { player.sendMessage(ChatColor.GREEN + "Pré-chargement terminé !"); }
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Pre-chargement termine !");
			isLoading = false;
			nbChunksGeneres = 0;
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> MineralMapPreloader.buildSpawn(), 1L);
			return;
		}
		MineralMapPreloader.chunkX = x;
		MineralMapPreloader.chunkY = y;
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> MineralMapPreloader.loadChunk(MineralMapPreloader.chunkX, MineralMapPreloader.chunkY), 1L);
	}
	
	public static void buildSpawn()
	{
		Main.getConfiguration().placeMiddle();
		Main.mapGenerated = true;
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.teleport(Main.getConfiguration().getChestLocation());
		}
	}
}
