package fr.hexzey.mineralcontest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.hexzey.mineralcontest.enumerators.GameState;
import fr.hexzey.mineralcontest.enumerators.MineralTeam;
import fr.hexzey.mineralcontest.enumerators.ScoreboardBuilder;
import fr.hexzey.mineralcontest.tools.DroppableItems;
import fr.hexzey.mineralcontest.tools.ItemBuilder;

public class MineralGame
{
	//////////////////////
	// ATTRIBUTS PRIVES //
	//////////////////////
	// corners de chaque base
	private HashMap<MineralTeam, ArrayList<Location>> teamBaseCorners;
	private HashMap<MineralTeam, Location> teamSpawnPoint;
	private HashMap<MineralTeam, Location> teamArenaTp;
	private HashMap<MineralTeam, ArrayList<Player>> teamPlayers;
	private HashMap<MineralTeam, Double> teamScore;
	private HashMap<MineralTeam, Scoreboard> teamBoards;
	
	
	private GameState gameState;
	private Location chestLocation;
	
	private int timer;
	private int DUREE_PARTIE;
	private int secondesRestantes;
	private boolean canTpToArena;
	private BossBar bossbar;
	
	private int secondsBeforeNextChest;
	private Entity minecartChestToUnlock;
	private int unlockTimer;
	private int currentUnlockProgression;
	private Player unlocker;
	
	//////////////////
	// CONSTRUCTEUR //
	//////////////////
	public MineralGame()
	{
		this.gameState = GameState.Waiting;
		
		this.chestLocation = null;
		this.unlocker = null;
		this.unlockTimer = 0;
		this.currentUnlockProgression = 0;
		this.minecartChestToUnlock = null;
		
		this.teamBaseCorners = new HashMap<MineralTeam, ArrayList<Location>>();
		this.teamBaseCorners.put(MineralTeam.Red, new ArrayList<Location>());
		this.teamBaseCorners.put(MineralTeam.Green, new ArrayList<Location>());
		this.teamBaseCorners.put(MineralTeam.Blue, new ArrayList<Location>());
		this.teamBaseCorners.put(MineralTeam.Yellow, new ArrayList<Location>());
		
		this.teamSpawnPoint = new HashMap<MineralTeam, Location>();
		this.teamArenaTp = new HashMap<MineralTeam, Location>();
		
		this.teamPlayers = new HashMap<MineralTeam, ArrayList<Player>>();
		this.teamPlayers.put(MineralTeam.Spectator, new ArrayList<Player>());
		this.teamPlayers.put(MineralTeam.Red, new ArrayList<Player>());
		this.teamPlayers.put(MineralTeam.Green, new ArrayList<Player>());
		this.teamPlayers.put(MineralTeam.Blue, new ArrayList<Player>());
		this.teamPlayers.put(MineralTeam.Yellow, new ArrayList<Player>());
		
		this.teamScore = new HashMap<MineralTeam, Double>();
		this.teamScore.put(MineralTeam.Red, 0d);
		this.teamScore.put(MineralTeam.Green, 0d);
		this.teamScore.put(MineralTeam.Blue, 0d);
		this.teamScore.put(MineralTeam.Yellow, 0d);
		
		this.teamBoards = new HashMap<MineralTeam, Scoreboard>();
		this.teamBoards.put(MineralTeam.Spectator, null);
		this.teamBoards.put(MineralTeam.Red, null);
		this.teamBoards.put(MineralTeam.Green, null);
		this.teamBoards.put(MineralTeam.Blue, null);
		this.teamBoards.put(MineralTeam.Yellow, null);
		
		this.timer = 0;
		this.DUREE_PARTIE = Main.getConfiguration().getGameDuration();
		this.secondesRestantes = this.DUREE_PARTIE;
		this.canTpToArena = false;
		this.bossbar = Bukkit.createBossBar("text", BarColor.PURPLE, BarStyle.SOLID);
		
		this.secondsBeforeNextChest = -1;
		this.minecartChestToUnlock = null;
	}
	
	////////////////
	// ACCESSEURS //
	////////////////
	public int getRemainingSeconds() { return this.secondesRestantes; }
	
	public void setCornersForTeam(MineralTeam team, ArrayList<Location> corners)
	{
		if(team == null || corners == null || corners.size() != 2) return;
    	this.teamBaseCorners.put(team, corners);
	}
	public ArrayList<Location> getCornersForTeam(MineralTeam team) { return this.teamBaseCorners.get(team); }
	
	public void setArenaTpForTeam(MineralTeam team, Location loc) {
		if(team == null || team == MineralTeam.Spectator || loc == null) return;
		this.teamArenaTp.put(team, loc);
	}
	
	
	
	public void setGameState(GameState state) { if(state != null) this.gameState = state; }
	public GameState getGameState() { return this.gameState; }
	
	
	
	public Double getScore(MineralTeam team) {
		if(team == null || team == MineralTeam.Spectator) return 0d;
		return this.teamScore.get(team);
	}
	
	
	public void setPlayerTeam(MineralTeam team, Player player)
	{
		if(team == null) return;
		
		for(MineralTeam t : this.teamPlayers.keySet())
		{
			if(this.teamPlayers.get(t).contains(player)) this.teamPlayers.get(t).remove(player);
		}
		
		this.teamPlayers.get(team).add(player);
		
		
		
		String msg = player.getName() + " a rejoint l'équipe ";
		if(team == MineralTeam.Spectator) {
			msg += ChatColor.LIGHT_PURPLE + "Spectateur";
			player.setDisplayName(ChatColor.GRAY + player.getName());
			player.setPlayerListName(ChatColor.GRAY + player.getName());
		}
		else if(team == MineralTeam.Red) {
			msg += ChatColor.RED + "Rouge";
			player.setDisplayName(ChatColor.RED + player.getName());
			player.setPlayerListName(ChatColor.RED + player.getName());
		}
		else if(team == MineralTeam.Green) {
			msg += ChatColor.GREEN + "Verte";
			player.setDisplayName(ChatColor.GREEN + player.getName());
			player.setPlayerListName(ChatColor.GREEN + player.getName());
		}
		else if(team == MineralTeam.Blue) {
			msg += ChatColor.BLUE + "Bleue";
			player.setDisplayName(ChatColor.BLUE + player.getName());
			player.setPlayerListName(ChatColor.BLUE + player.getName());
		}
		else if(team == MineralTeam.Yellow) {
			msg += ChatColor.YELLOW + "Jaune";
			player.setDisplayName(ChatColor.YELLOW + player.getName());
			player.setPlayerListName(ChatColor.YELLOW + player.getName());
		}
		for(Player p : Bukkit.getOnlinePlayers()) p.sendMessage(msg);
		
		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
		this.giveLobbyInventoryFor(player);
	}
	public MineralTeam getMineralTeam(Player player)
	{
		for(MineralTeam team : this.teamPlayers.keySet()) {
			if(this.teamPlayers.get(team).contains(player)) { return team; }
		}
		return null;
	}
	public HashMap<MineralTeam, ArrayList<Player>> getAllTeams() { return this.teamPlayers; }
	
	
	public void setSpawnPoint(MineralTeam team, Location loc) {
		if(team == null || team == MineralTeam.Spectator) return;
		this.teamSpawnPoint.put(team, loc);
	}
	public Location getSpawnPoint(Player player) {
		MineralTeam team = this.getMineralTeam(player);
		return this.getSpawnPoint(team);
	}
	public Location getSpawnPoint(MineralTeam team) {
		if(team == null || team == MineralTeam.Spectator) return null;
		return this.teamSpawnPoint.get(team);
	}
	
	
	
	public void setChestLocation(Location loc) {
		if(loc == null) return;
		this.chestLocation = loc;
	}
	public void setSecondsBeforeNextChest(int time) {
		if(time < 1) return;
		if(this.minecartChestToUnlock != null) return;
		this.secondsBeforeNextChest = time;
	}
	
	
	
	public boolean canTpToArena() { return this.canTpToArena; }
	public void openArenaTp() {
		this.canTpToArena = true;
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> this.closeArenaTp(), 15*20L);
		String msg = ChatColor.DARK_AQUA + "L'" + ChatColor.AQUA + "arène " + ChatColor.DARK_AQUA + "est ouverte.";
		msg += " Utilisez " + ChatColor.AQUA + "/arene " + ChatColor.DARK_AQUA + "pour y téléporter votre équipe.";
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(msg);
		}
	}
	public void closeArenaTp() {
		this.canTpToArena = false;
		String msg = ChatColor.DARK_AQUA + "Délai de téléportation dans l'" + ChatColor.AQUA + "arène " + ChatColor.DARK_AQUA + "dépassé.";
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(msg);
		}
	}
	
	
	
	public void spawnMinecartChestToUnlock() {
		if(this.minecartChestToUnlock != null) return;
		this.minecartChestToUnlock = Bukkit.getWorld("mineralmap").spawnEntity(chestLocation, EntityType.MINECART_CHEST);
		this.minecartChestToUnlock.setInvulnerable(true);
		String title = ChatColor.DARK_AQUA + "Un coffre est apparu";
		String subtitle = ChatColor.WHITE + "Crochetez-le au centre de l'arène";
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(title, subtitle, 1, 3*20, 10);
			player.sendMessage(title + ". " + subtitle + " !");
			player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
		}
		if(this.secondesRestantes >= Main.getConfiguration().getBorderEndReductionTime()) { this.openArenaTp(); }
	}
	public Entity getMinecartChestToUnlock() { return this.minecartChestToUnlock; }
	public int getUnlockProgression() { return this.currentUnlockProgression; }
	public Player getUnlocker() { return this.unlocker; }
	
	
	
	public boolean startUnlocking(Player player) {
		if(this.minecartChestToUnlock == null) return false; // action impossible: rien à crocheter
		if(this.unlocker != null) {
			player.sendMessage(ChatColor.RED + "Action impossible: un autre joueur crochète déjà ce coffre !");
			return false; // action impossible: un autre joueur crochète déjà le coffre
		}
		if(player == this.unlocker) return true; // action impossible mais on considère que c'est ok: le joueur actuel crochète déjà le coffre
		
		// arrêter le crochetage en cours
		this.cancelUnlocking();
		// démarrer un nouveau crochetage
		this.currentUnlockProgression = 0;
		this.unlocker = player;
		
		this.unlockTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
		    public void run() {
		        unlockingTime();
		    }}, 1*20L, 1*20L); // appeller la méthode time() toutes les secondes (1*20L)
		
		// ouvrir le menu de crochetage
		Inventory menu = Bukkit.createInventory(null, 9, "Crochetage...");
		ItemBuilder builder = new ItemBuilder(Material.RED_WOOL).displayName(ChatColor.WHITE + "Crochetage en cours...");
		menu.setItem(0, builder.build());
		menu.setItem(1, builder.build());
		menu.setItem(2, builder.build());
		menu.setItem(3, builder.build());
		menu.setItem(4, builder.build());
		builder = new ItemBuilder(Material.CHEST).displayName(ChatColor.WHITE + "Crochetage en cours...");
		menu.setItem(5, builder.build());
		player.openInventory(menu);
		
		return true;
	}
	
	
	
	public void cancelUnlocking() {
		if(this.unlocker == null) return;
		// arrêter la tâche
		Bukkit.getServer().getScheduler().cancelTask(this.unlockTimer);
		// envoyer un message au joueur en train de crocheter
		Player oldUnlocker = this.unlocker;
		this.unlocker = null;
		oldUnlocker.sendMessage(ChatColor.RED + "Crochetage annulé !");
		oldUnlocker.playSound(oldUnlocker.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
		// retirer le crocheteur et libérer le slot
		oldUnlocker.closeInventory();
		// réinitialiser la progression
		this.currentUnlockProgression = 0;
	}
	
	
	
	public void unlockingTime() {
		if(this.unlocker == null) {
			Bukkit.getServer().getScheduler().cancelTask(this.unlockTimer);
			return;
		}
		this.currentUnlockProgression+=1;
		if(this.currentUnlockProgression > 5) {
			this.finishUnlocking();
			return;
		}
		this.unlocker.playSound(this.unlocker.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
		ItemBuilder builder = new ItemBuilder(Material.LIME_WOOL).displayName(ChatColor.WHITE + "Crochetage en cours...");
		this.unlocker.getOpenInventory().setItem(this.currentUnlockProgression-1, builder.build());
	}
	
	
	
	public void finishUnlocking() {
		if(this.unlocker == null) return;
		Player player = this.unlocker;
		// retirer le crocheteur et libérer le slot
		this.unlocker = null;
		// arrêter la tâche
		Bukkit.getServer().getScheduler().cancelTask(this.unlockTimer);
		// détruire le minecart
		this.minecartChestToUnlock.remove();
		this.minecartChestToUnlock = null;
		// donner les items au joueur et lui envoyer un message
		player.closeInventory();
		Random random = new Random();
		int nbCoal = random.nextInt(11)+4; // [0;10] +5 = [5;15]
		int nbIron = random.nextInt(9)+3; // [0;8] +4 = [4;12]
		int nbGold = random.nextInt(7)+1; // [0;6] +2 = [2;8]
		int nbDiamond = random.nextInt(4); // [0;3]
		if(nbCoal > 0) player.getInventory().addItem(new ItemBuilder(Material.COAL).amount(nbCoal).build());
		if(nbIron > 0) player.getInventory().addItem(new ItemBuilder(Material.IRON_INGOT).amount(nbIron).build());
		if(nbGold > 0) player.getInventory().addItem(new ItemBuilder(Material.GOLD_INGOT).amount(nbGold).build());
		if(nbDiamond > 0) player.getInventory().addItem(new ItemBuilder(Material.DIAMOND).amount(nbDiamond).build());
		player.sendMessage(ChatColor.GREEN + "Vous avez crocheté le coffre !");
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
		for(Player p : Bukkit.getOnlinePlayers()) {
			// informer les autres joueurs que le coffre a été ouvert
			p.sendMessage(ChatColor.AQUA + "Le coffre a été ouvert !");
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
		}
		// réinitialiser la progression
		this.currentUnlockProgression = 0;
		
		// faire apparaître le prochain coffre dans un délai aléatoire
		this.secondsBeforeNextChest = random.nextInt(240) + 420; // [0;240] + 420 = [420;660]
	}
	
	
	
	////////////////////////
	// METHODES PUBLIQUES //
	////////////////////////
	public void Prepare() {
		/**
		 * Cette méthode précède la méthode Start()
		 * Elle permet de préparer le début de la partie (ex: tp les joueurs)
		 */
		this.teamScore.put(MineralTeam.Red, 0d);
		this.teamScore.put(MineralTeam.Green, 0d);
		this.teamScore.put(MineralTeam.Blue, 0d);
		this.teamScore.put(MineralTeam.Yellow, 0d);
		
		this.secondesRestantes = this.DUREE_PARTIE;
		this.bossbar = Bukkit.createBossBar("text", BarColor.PURPLE, BarStyle.SOLID);
		this.bossbar.setTitle("Temps restant : --:--");
		this.bossbar.setProgress(0);
		
		this.teamBoards.put(MineralTeam.Spectator, this.updateScoreboard(MineralTeam.Spectator));
		this.teamBoards.put(MineralTeam.Red, this.updateScoreboard(MineralTeam.Red));
		this.teamBoards.put(MineralTeam.Green, this.updateScoreboard(MineralTeam.Green));
		this.teamBoards.put(MineralTeam.Blue, this.updateScoreboard(MineralTeam.Blue));
		this.teamBoards.put(MineralTeam.Yellow, this.updateScoreboard(MineralTeam.Yellow));
		for(Player player : Bukkit.getOnlinePlayers())
		{
			// vider l'inventaire
			player.getInventory().clear();
			// afficher la bossbar
			this.bossbar.addPlayer(player);
		}
		
		for(Player player : this.teamPlayers.get(MineralTeam.Spectator)) {
			player.setGameMode(GameMode.SPECTATOR);
			player.teleport(new Location(Bukkit.getWorld("mineralmap"), 0d, 60d, 0d));
		}
		for(Player player : this.teamPlayers.get(MineralTeam.Red)) {
			player.setExp(0);
			player.teleport(this.getSpawnPoint(MineralTeam.Red));
			Main.playerFreeze.freeze(player);
		}
		for(Player player : this.teamPlayers.get(MineralTeam.Green)) {
			player.setExp(0);
			player.teleport(this.getSpawnPoint(MineralTeam.Green));
			Main.playerFreeze.freeze(player);
		}
		for(Player player : this.teamPlayers.get(MineralTeam.Blue)) {
			player.setExp(0);
			player.teleport(this.getSpawnPoint(MineralTeam.Blue));
			Main.playerFreeze.freeze(player);
		}
		for(Player player : this.teamPlayers.get(MineralTeam.Yellow)) {
			player.setExp(0);
			player.teleport(this.getSpawnPoint(MineralTeam.Yellow));
			Main.playerFreeze.freeze(player);
		}
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> Main.mineralGame.Start(), 4*20L);
	}
	public void Start() {
		this.gameState = GameState.Running;
		
		for(Player player : this.teamPlayers.get(MineralTeam.Red)) {
			player.teleport(this.getSpawnPoint(MineralTeam.Red));
			this.playStartAnimation(player);
		}
		for(Player player : this.teamPlayers.get(MineralTeam.Green)) {
			player.teleport(this.getSpawnPoint(MineralTeam.Green));
			this.playStartAnimation(player);
		}
		for(Player player : this.teamPlayers.get(MineralTeam.Blue)) {
			player.teleport(this.getSpawnPoint(MineralTeam.Blue));
			this.playStartAnimation(player);
		}
		for(Player player : this.teamPlayers.get(MineralTeam.Yellow)) {
			player.teleport(this.getSpawnPoint(MineralTeam.Yellow));
			this.playStartAnimation(player);
		}
    	// faire apparaître le prochain coffre dans un délai aléatoire
    	Random random = new Random();
		this.secondsBeforeNextChest = random.nextInt(240) + 420; // [0;240] + 420 = [420;660]
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> this.timer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
		    public void run() {
		        time();
		    }}, 1*20L, 1*20L), 4*20L); // appeller la méthode time() toutes les secondes (1*20L)
	}
	
	public void time() {
		/**
		 * Méthode déclenchée chaque seconde durant la partie
		 * Permet d'effectuer des actions programmées dans le temps (ex: fin de partie à 0s)
		 */
		
		// actualiser le temps restant
		this.secondesRestantes--; // retirer 1s au temps restant
		// mettre à jour le texte de la bossbar
		this.bossbar.setProgress((float)(this.secondesRestantes/this.DUREE_PARTIE));
		int minutes = (int)Math.floor(secondesRestantes / 60);
		int secondes = (int)secondesRestantes-(minutes*60);
		this.bossbar.setTitle("Temps restant : " + String.format("%02d", minutes) + ":" + String.format("%02d", secondes));
		
		// afficher le temps restant à des moments clés
		if(this.secondesRestantes == 120) {
			String subtitle = ChatColor.DARK_AQUA + "2 minutes restantes";
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.sendTitle("", subtitle, 1, 3*20, 10);
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.8f);
			}
		} else if(this.secondesRestantes == 30) {
			String subtitle = ChatColor.DARK_AQUA + "30 secondes restantes";
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.sendTitle("", subtitle, 1, 3*20, 10);
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.8f);
			}
		}
		
		
		// GESTION DE LA BORDURE
		int borderStartReductionTime = Main.getConfiguration().getBorderStartReductionTime();
		// envoi d'un message d'avertissement 2min avant la réduction de la bordure
		if(this.secondesRestantes == borderStartReductionTime + 120) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				String title = ChatColor.RED + "Attention";
				String subtitle = ChatColor.WHITE + "Réduction de la bordure dans 2 minutes...";
				player.sendTitle(title, subtitle, 1, 4*20, 10);
				player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 0.8f);
			}
		}
		// réduire la bordure en fin de partie
		if(this.secondesRestantes == borderStartReductionTime) {
			Bukkit.getWorld("mineralmap").getWorldBorder().setDamageAmount(1);
			int borderEndReductionTime = Main.getConfiguration().getBorderEndReductionTime();
			int shrinkDuration = Math.max(borderStartReductionTime-borderEndReductionTime, 10);
			Bukkit.getWorld("mineralmap").getWorldBorder().setSize(78d, shrinkDuration); // taille finale = 80x80, temps de réduction = 30 secondes
			String title = ChatColor.RED + "Réduction de la bordure";
			String subtitle = ChatColor.WHITE + "Rejoignez le centre de la carte";
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.sendTitle(title, subtitle, 1, 4*20, 10);
				player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.8f);
			}
		}
		
		// faire spawn des poulets pendant les dernières 90 secondes
		int chickensTime = Main.getConfiguration().getChickensTime();
		if(this.secondesRestantes <= chickensTime && this.secondesRestantes > 0) {
			if(this.secondesRestantes == chickensTime) {
				for(Player player : Bukkit.getOnlinePlayers()) {
					player.sendMessage(ChatColor.GOLD + "Des poulets apparaissent dans l'arène !");
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
				}
			}
			if(this.secondesRestantes%2 == 0) {
				Bukkit.getWorld("mineralmap").spawnEntity(chestLocation, EntityType.CHICKEN);
			}
		}
		
		// affichage du temps restant sur l'écran lors des dernières secondes
		if(this.secondesRestantes <= 10 && this.secondesRestantes > 0) {
			String title = ChatColor.DARK_AQUA + String.valueOf(this.secondesRestantes);
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.sendTitle(title, "", 1, 10, 10);
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
			}
		}
		
		// timer à zéro = fin de la partie
		if(this.secondesRestantes <= 0) {
			Bukkit.getServer().getScheduler().cancelTask(this.timer); // arrêter la tâche
			this.End(); // mettre fin à la partie
		}
		
		// gestion de l'apparition aléatoire des coffres
		if(this.secondsBeforeNextChest > 0) {
			this.secondsBeforeNextChest--;
			if(this.secondsBeforeNextChest == 10) {
				for(Player player : Bukkit.getOnlinePlayers()) {
					String text = ChatColor.DARK_AQUA + "Un coffre apparaître dans l'arène dans 10 secondes...";
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
					player.sendMessage(text);
					player.sendTitle("", text, 5, 40, 5);
				}
			}
			if(this.secondsBeforeNextChest == 0) {
				this.spawnMinecartChestToUnlock();
			}
		}
		
		// faire spawn un coffre à crocheter 59 secondes avant la fin
		if(this.secondesRestantes == 59) {
			if(this.secondsBeforeNextChest > 10) { // inutile de faire spawn un coffre si un autre coffre est sur le point d'appartaître
				this.secondsBeforeNextChest = -1; // désactiver le spawn de coffres
				this.spawnMinecartChestToUnlock();
			}
		}
	}
	
	public void playStartAnimation(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 4*20, 2)); // slowness 2 pendant 4 secondes
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 4*20, 1)); // blindness pendant 4 secondes
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f), 1*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendMessage(ChatColor.GREEN + "3"), 1*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(ChatColor.GREEN + "3", null, 1, 20, 1), 1*20L);
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f), 2*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendMessage(ChatColor.GREEN + "2"), 2*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(ChatColor.GREEN + "2", null, 1, 20, 1), 2*20L);
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f), 3*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendMessage(ChatColor.GREEN + "1"), 3*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(ChatColor.GREEN + "1", null, 1, 20, 1), 3*20L);
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> Main.playerFreeze.unfreeze(player), 4*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f), 4*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> this.spawnPlayer(player), 4*20L);
	}
	
	
	
	public void End() {
		this.canTpToArena = false;
		// détruire le minecart à crocheter si il est actif
		if(this.minecartChestToUnlock != null) {
			this.minecartChestToUnlock.remove();
			this.minecartChestToUnlock = null;
		}

		if(this.unlockTimer != 0) {
			// arrêter la tâche
			Bukkit.getServer().getScheduler().cancelTask(this.unlockTimer);
		}
		
		// arrêter le timer
		if(this.timer != 0) {
			Bukkit.getServer().getScheduler().cancelTask(this.timer); // arrêter la tâche
			this.End(); // mettre fin à la partie
		}
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			// fermer les inventaires ouverts et les vider
			player.closeInventory();
			player.getInventory().clear();
			// soigner les joueurs
			player.setHealth(20);
			player.setFoodLevel(20);
			// cacher le scoreboard
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			// cacher la bossbar
			this.bossbar.removePlayer(player);
			player.setGameMode(GameMode.SPECTATOR);
			player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
			player.sendMessage(ChatColor.GREEN + "Fin de la partie ! Place aux résultats...");
		}
		HashMap<Integer, MineralTeam> finalScores = this.getTeamLadder();
		int rank = finalScores.keySet().size();
		int delay = 5;
		while(finalScores.keySet().size() > 0 && rank > 0) {
			MineralTeam team = finalScores.get(rank);
			Double score = this.getScore(team);
			for(Player player : Bukkit.getOnlinePlayers()) {
				String title = ChatColor.GOLD + String.valueOf(rank) + "e place";
				String subtitle = ChatColor.WHITE + "avec " + String.valueOf(score) + " points...";
				// afficher la place avec le score
				// fade in et fade out = 1 tick, texte affiché pendant 3*20 ticks = 3s
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(title, subtitle, 1, 4*20, 1), delay*20L);
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f), delay*20L);
			}
			delay += 4;
			for(Player player : Bukkit.getOnlinePlayers()) {
				// afficher l'équipe possédant cette place
				String tempTitle = "";
				if(team == MineralTeam.Red) {
					tempTitle = ChatColor.RED + "Equipe rouge";
				} else if(team == MineralTeam.Green) {
					tempTitle = ChatColor.GREEN + "Equipe verte";
				} else if(team == MineralTeam.Blue) {
					tempTitle = ChatColor.BLUE + "Equipe bleue";
				} else if(team == MineralTeam.Yellow) {
					tempTitle = ChatColor.YELLOW + "Equipe jaune";
				}
				String tempSubtitle = "";
				String title = tempTitle;
				String subtitle = tempSubtitle;
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(title, subtitle, 1, 4*20, 1), delay*20L);
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1.0f), delay*20L);
				String rankText = ChatColor.DARK_AQUA + String.valueOf(rank) + "e place";
				String pointsText = ChatColor.AQUA + String.valueOf(score) + " points";
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendMessage(rankText + ChatColor.DARK_AQUA + " - " + pointsText + ChatColor.DARK_AQUA + " - " + title), delay*20L);
			}
			delay += 6;
			rank--;
		}
		delay += 1;
		for(Player player : Bukkit.getOnlinePlayers()) {
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.teleport(Main.getConfiguration().getLobbyLocation()), delay*20L);
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.setGameMode(GameMode.SURVIVAL), delay*20L);
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> this.giveLobbyInventoryFor(player), delay*20L);
		}
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> this.gameState = GameState.Waiting, delay*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> this.resetTeams(), delay*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> Main.mapGenerated = false, delay*20L);
	}
	
	
	
	public void resetTeams() {
		this.teamPlayers.put(MineralTeam.Spectator, new ArrayList<Player>());
		this.teamPlayers.put(MineralTeam.Red, new ArrayList<Player>());
		this.teamPlayers.put(MineralTeam.Green, new ArrayList<Player>());
		this.teamPlayers.put(MineralTeam.Blue, new ArrayList<Player>());
		this.teamPlayers.put(MineralTeam.Yellow, new ArrayList<Player>());
		
		this.teamScore.put(MineralTeam.Red, 0d);
		this.teamScore.put(MineralTeam.Green, 0d);
		this.teamScore.put(MineralTeam.Blue, 0d);
		this.teamScore.put(MineralTeam.Yellow, 0d);
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			this.setPlayerTeam(MineralTeam.Spectator, player);
		}
	}
	
	
	
	public Scoreboard updateScoreboard(MineralTeam team)
	{
		if(team == null) { team = MineralTeam.Spectator; }
		
		ScoreboardBuilder sbb = new ScoreboardBuilder("mineralcontest", "dummy", "MineralContest");
		sbb.AddLine(" ");
		String teamText = "";
		if(team == MineralTeam.Spectator) {
			teamText = ChatColor.WHITE + "Mode spectateur";
		} else if(team == MineralTeam.Red) {
			teamText = ChatColor.RED + "Equipe rouge";
		} else if(team == MineralTeam.Green) {
			teamText = ChatColor.GREEN + "Equipe verte";
		} else if(team == MineralTeam.Blue) {
			teamText = ChatColor.BLUE + "Equipe bleue";
		} else if(team == MineralTeam.Yellow) {
			teamText = ChatColor.YELLOW + "Equipe jaune";
		}
		sbb.AddLine(teamText);
		sbb.AddLine(" ");
		
		if(team != MineralTeam.Spectator) {
			Double score = this.getScore(team);
			sbb.AddLine(ChatColor.WHITE + String.valueOf(score) + " points");
		}
		else {
			HashMap<Integer, MineralTeam> ladder = this.getTeamLadder();
			for(Integer rank : ladder.keySet()) {
				String str = "";
				if(ladder.get(rank) == MineralTeam.Red) {
					str = ChatColor.RED + String.valueOf(rank) + " Rouge - " + this.getScore(MineralTeam.Red) + " points";
				} else if(ladder.get(rank) == MineralTeam.Green) {
					str = ChatColor.GREEN + String.valueOf(rank) + " Vert - " + this.getScore(MineralTeam.Green) + " points";
				} else if(ladder.get(rank) == MineralTeam.Blue) {
					str = ChatColor.BLUE + String.valueOf(rank) + " Bleu - " + this.getScore(MineralTeam.Blue) + " points";
				} else if(ladder.get(rank) == MineralTeam.Yellow) {
					str = ChatColor.YELLOW + String.valueOf(rank) + " Jaune - " + this.getScore(MineralTeam.Yellow) + " points";
				}
				sbb.AddLine(str);
			}
		}
		
		Scoreboard scoreboard = sbb.build();
		Team sbTeam = scoreboard.registerNewTeam("team");
		if(team == MineralTeam.Spectator) sbTeam.setPrefix(ChatColor.WHITE.toString());
		else if(team == MineralTeam.Red) sbTeam.setPrefix(ChatColor.RED.toString());
		else if(team == MineralTeam.Green) sbTeam.setPrefix(ChatColor.GREEN.toString());
		else if(team == MineralTeam.Blue) sbTeam.setPrefix(ChatColor.BLUE.toString());
		else if(team == MineralTeam.Yellow) sbTeam.setPrefix(ChatColor.YELLOW.toString());
		
		for(Player player : this.teamPlayers.get(team)) {
			// afficher le scoreboard
			player.setScoreboard(scoreboard);
			sbTeam.addPlayer(player);
		}
		
		return scoreboard;
	}
	
	
	
	public void openTeamSelectInventoryFor(Player player)
	{
		HashMap<MineralTeam, ArrayList<Player>> teamsCompositions = (HashMap<MineralTeam, ArrayList<Player>>)this.getAllTeams().clone();
		Inventory menu = Bukkit.createInventory(null, 9, "Choisir une equipe");
		
		// 0 : BARRIER = SPECTATEUR
		ItemBuilder builder = new ItemBuilder(Material.BARRIER).displayName(ChatColor.WHITE + "Spectateur");
		if(teamsCompositions.get(MineralTeam.Spectator).size() > 0) { for(Player p : teamsCompositions.get(MineralTeam.Spectator)) builder.addLine(p.getName()); }
		if(Main.mineralGame.getMineralTeam(player) == MineralTeam.Spectator) menu.setItem(0, builder.buildGlow());
		else menu.setItem(0, builder.build());
		
		// 1 : RED WOOL = EQUIPE ROUGE
		builder = new ItemBuilder(Material.RED_WOOL).displayName(ChatColor.RED + "Equipe rouge");
		if(teamsCompositions.get(MineralTeam.Red).size() > 0) { for(Player p : teamsCompositions.get(MineralTeam.Red)) builder.addLine(p.getName()); }
		if(Main.mineralGame.getMineralTeam(player) == MineralTeam.Red) menu.setItem(1, builder.buildGlow());
		else menu.setItem(1, builder.build());
		
		// 2 : LIME WOOL = EQUIPE VERTE
		builder = new ItemBuilder(Material.LIME_WOOL).displayName(ChatColor.GREEN + "Equipe verte");
		if(teamsCompositions.get(MineralTeam.Green).size() > 0) { for(Player p : teamsCompositions.get(MineralTeam.Green)) builder.addLine(p.getName()); }
		if(Main.mineralGame.getMineralTeam(player) == MineralTeam.Green) menu.setItem(2, builder.buildGlow());
		else menu.setItem(2, builder.build());
		
		// 3 : BLUE WOOL = EQUIPE BLEUE
		builder = new ItemBuilder(Material.BLUE_WOOL).displayName(ChatColor.BLUE + "Equipe bleue");
		if(teamsCompositions.get(MineralTeam.Blue).size() > 0) { for(Player p : teamsCompositions.get(MineralTeam.Blue)) builder.addLine(p.getName()); }
		if(Main.mineralGame.getMineralTeam(player) == MineralTeam.Blue) menu.setItem(3, builder.buildGlow());
		else menu.setItem(3, builder.build());
		
		// 4 : YELLOW WOOL = EQUIPE JAUNE
		builder = new ItemBuilder(Material.YELLOW_WOOL).displayName(ChatColor.YELLOW + "Equipe jaune");
		if(teamsCompositions.get(MineralTeam.Yellow).size() > 0) { for(Player p : teamsCompositions.get(MineralTeam.Yellow)) builder.addLine(p.getName()); }
		if(Main.mineralGame.getMineralTeam(player) == MineralTeam.Yellow) menu.setItem(4, builder.buildGlow());
		else menu.setItem(4, builder.build());
		
		player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
		player.openInventory(menu);
	}
	
	
	
	public void openResourceDepotInventoryFor(Player player) {
		/**
		 * Ouvrir l'inventaire qui permet au joueur de déposer des ressources
		 * Les ressources déposées sont converties en score grâce à l'event onInventoryClick()
		 */
		if(this.getMineralTeam(player) == null || this.getMineralTeam(player) == MineralTeam.Spectator) return;
		Inventory menu = Bukkit.createInventory(null, 9, "Deposez vos ressources");
		player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
		player.openInventory(menu);
	}
	
	
	
	public void giveLobbyInventoryFor(Player player) {
		player.getInventory().clear();
		MineralTeam team = this.getMineralTeam(player);
		
		// spectateur par défaut dans l'inventaire
		ItemBuilder builder = new ItemBuilder(Material.BARRIER).displayName(ChatColor.WHITE + "Sélectionner son équipe");
		if(team == MineralTeam.Red) builder = new ItemBuilder(Material.RED_WOOL).displayName(ChatColor.RED + "Sélectionner son équipe");
		else if(team == MineralTeam.Green) builder = new ItemBuilder(Material.LIME_WOOL).displayName(ChatColor.GREEN + "Sélectionner son équipe");
		else if(team == MineralTeam.Blue) builder = new ItemBuilder(Material.BLUE_WOOL).displayName(ChatColor.BLUE + "Sélectionner son équipe");
		else if(team == MineralTeam.Yellow) builder = new ItemBuilder(Material.YELLOW_WOOL).displayName(ChatColor.YELLOW + "Sélectionner son équipe");
		else builder = new ItemBuilder(Material.BARRIER).displayName(ChatColor.WHITE + "Sélectionner son équipe");
		
		player.getInventory().setItem(0, builder.build());
	}
	
	
	
	public void removePlayer(Player player) {
		for(MineralTeam t : this.teamPlayers.keySet())
		{
			if(this.teamPlayers.get(t).contains(player)) {
				this.teamPlayers.get(t).remove(player);
				return;
			}
		}
	}
	
	
	
	public void tpTeamToArena(MineralTeam team) {
		if(team == null || team == MineralTeam.Spectator) return;
		if(this.canTpToArena == false) return;
		
		
		Location loc = this.teamArenaTp.get(team);
		for(Player player : this.teamPlayers.get(team)) {
			player.teleport(loc);
			player.sendMessage(ChatColor.AQUA + "Votre équipe a été téléportée dans l'arène !");
		}
	}
	
	
	
	public void killPlayer(Player player) {
		if(player.getGameMode() == GameMode.SPECTATOR) return;
		
		Location deathLocation = player.getLocation();
		ArrayList<Material> droppables = new ArrayList<Material>(DroppableItems.getDroppableItems());
		for(ItemStack is : player.getInventory().getContents())
		{
			if(is != null && droppables.contains(is.getType())) {
				deathLocation.getWorld().dropItem(deathLocation, is);
			}
		}
		
		player.getInventory().clear();
		player.setGameMode(GameMode.SPECTATOR);
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 8*20, 1)); // blindness pendant 8 secondes
		
		// informer les joueurs de la mort du joueur
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(this.getMineralTeam(player) == this.getMineralTeam(p)) {
				p.sendMessage(ChatColor.RED + player.getName() + " est mort. Votre équipe perd 125 points.");
			} else {
				p.sendMessage(ChatColor.RED + player.getName() + " est mort.");
			}
		}
		this.removeScore(this.getMineralTeam(player), 125d);
		
		player.sendTitle(ChatColor.RED + "Vous êtes mort", ChatColor.WHITE + "Réapparition dans 7 secondes...", 1, 1*20, 1);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(ChatColor.RED + "Vous êtes mort", ChatColor.WHITE + "Réapparition dans 6 secondes...", 1, 1*20, 1), 1*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(ChatColor.RED + "Vous êtes mort", ChatColor.WHITE + "Réapparition dans 5 secondes...", 1, 1*20, 1), 2*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(ChatColor.RED + "Vous êtes mort", ChatColor.WHITE + "Réapparition dans 4 secondes...", 1, 1*20, 1), 3*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(ChatColor.RED + "Vous êtes mort", ChatColor.WHITE + "Réapparition dans 3 secondes...", 1, 1*20, 1), 4*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(ChatColor.RED + "Vous êtes mort", ChatColor.WHITE + "Réapparition dans 2 secondes...", 1, 1*20, 1), 5*20L);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.sendTitle(ChatColor.RED + "Vous êtes mort", ChatColor.WHITE + "Réapparition dans 1 seconde...", 1, 1*20, 1), 6*20L);
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> this.spawnPlayer(player), 7*20L);
	}
	
	
	
	public void spawnPlayer(Player player) {
		if(player == null) return;
		if(this.secondesRestantes <= 0) return;
		player.getInventory().clear();
		player.setGameMode(GameMode.SURVIVAL);
		Location spawnpoint = this.getSpawnPoint(player).clone();
		spawnpoint.setY(spawnpoint.getY() + 0.25d);
		player.teleport(spawnpoint);
		MineralTeam team = this.getMineralTeam(player);
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		Color color = Color.WHITE;
		if(team == MineralTeam.Red) {
			color = Color.RED;
		} else if(team == MineralTeam.Green) {
			color = Color.GREEN;
		} else if(team == MineralTeam.Blue) {
			color = Color.BLUE;
		} else if(team == MineralTeam.Yellow) {
			color = Color.YELLOW;
		}
		LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
		//LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
		LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
		helmetMeta.setColor(color); /*chestplateMeta.setColor(color);*/ leggingsMeta.setColor(color); bootsMeta.setColor(color);
		helmet.setItemMeta(helmetMeta); /*chestplate.setItemMeta(chestplateMeta);*/ leggings.setItemMeta(leggingsMeta); boots.setItemMeta(bootsMeta);
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
		ItemStack sword = new ItemStack(Material.STONE_SWORD);
		player.getInventory().setItem(0, sword);
		ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE);
		pickaxe.addEnchantment(Enchantment.DIG_SPEED, 1);
		pickaxe.addEnchantment(Enchantment.DURABILITY, 1);
		player.getInventory().setItem(1, pickaxe);
		ItemStack food = new ItemStack(Material.COOKED_BEEF, 5);
		player.getInventory().setItem(2, food);
		ItemStack bow = new ItemStack(Material.BOW, 1);
		player.getInventory().setItem(3, bow);
		ItemStack arrows = new ItemStack(Material.ARROW, 15);
		player.getInventory().setItem(4, arrows);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setExp(0);
		player.setLevel(0);
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10*20, 1)); // effet regeneration pendant 10 secondes
	}
	
	
	
	public MineralTeam getTeamSafezoneAt(Location loc) {
		/**
		 * Retourne l'équipe propriétaire de la safezone (base) aux coordonnées
		 * Si aucune équipe n'est trouvée, alors ce n'est pas une safe zone (base) et on retourne NULL
		 */
		for(MineralTeam team : this.teamBaseCorners.keySet()) {
			ArrayList<Location> corners = this.teamBaseCorners.get(team);
			
			ChatColor color = ChatColor.WHITE;
			if(team == MineralTeam.Red) color = ChatColor.RED;
			else if(team == MineralTeam.Green) color = ChatColor.GREEN;
			else if(team == MineralTeam.Blue) color = ChatColor.BLUE;
			else if(team == MineralTeam.Yellow) color = ChatColor.YELLOW;	
			
			int minX = (int)Math.min(corners.get(0).getX(), corners.get(1).getX());
	    	int minY = (int)Math.min(corners.get(0).getY(), corners.get(1).getY());
	    	int minZ = (int)Math.min(corners.get(0).getZ(), corners.get(1).getZ());
	    	
	    	int maxX = (int)Math.max(corners.get(0).getX(), corners.get(1).getX());
	    	int maxY = (int)Math.max(corners.get(0).getY(), corners.get(1).getY());
	    	int maxZ = (int)Math.max(corners.get(0).getZ(), corners.get(1).getZ());
	    	
			if(loc.getX() >= minX && loc.getX() <= maxX) // vérifications axe X
			{
				if(loc.getZ() >= minZ && loc.getZ() <= maxZ) // vérifications axe Z
				{
					return team;
				}
			}
		}
		return null;
	}
	
	
	public void addScore(MineralTeam team, Double value) {
		/**
		 * Cette méthode permet d'ajouter des points de score à une équipe
		 */
		if(value <= 0) return;
		if(team == null || team == MineralTeam.Spectator) return;
		Double score = this.teamScore.get(team);
		score += value;
		this.teamScore.put(team, score);
		// mettre à jour le scoreboard une fois le score enregistré
		this.teamBoards.put(team, this.updateScoreboard(team));
		// mettre à jour le scoreboard des spectateurs
		this.teamBoards.put(MineralTeam.Spectator, this.updateScoreboard(MineralTeam.Spectator));
	}
	
	public void removeScore(MineralTeam team, Double value) {
		if(value <= 0) return;
		if(team == null || team == MineralTeam.Spectator) return;
		Double score = this.teamScore.get(team);
		score -= value;
		this.teamScore.put(team, score);
		// mettre à jour le scoreboard une fois le score enregistré
		this.teamBoards.put(team, this.updateScoreboard(team));
		// mettre à jour le scoreboard des spectateurs
		this.teamBoards.put(MineralTeam.Spectator, this.updateScoreboard(MineralTeam.Spectator));
		// envoyer un message à l'équipe concernée par le retrait
		for(Player p : this.teamPlayers.get(team)) {
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
			p.sendMessage(ChatColor.GOLD + "Score total : " + String.valueOf(this.getScore(team)));
		}
	}
	
	
	public boolean sellResources(ItemStack is, Player player) {
		MineralTeam team = this.getMineralTeam(player);
		if(team == null || team == MineralTeam.Spectator) return false;
		
		Material type = is.getType();
		int amount = is.getAmount();
		Double value = 0d;
		boolean sold = false;
		if(type == Material.COAL) {
			value = 4d;
			this.addScore(team, amount*value);
			sold = true;
		} else if(type == Material.IRON_INGOT) {
			value = 35d;
			this.addScore(team, amount*value);
			sold = true;
		} else if(type == Material.GOLD_INGOT) {
			value = 75d;
			this.addScore(team, amount*value);
			sold = true;
		} else if(type == Material.DIAMOND) {
			value = 150d;
			this.addScore(team, amount*value);
			sold = true;
		} else if(type == Material.EMERALD) {
			value = 300d;
			this.addScore(team, amount*value);
			sold = true;
		} else if(type == Material.REDSTONE) {
			value = 4d;
			// cette ressource retire des points aux équipes adverses
			// 1. Récupérer les équipes disposant d'au moins 1 joueur
			ArrayList<MineralTeam> otherTeams = new ArrayList<MineralTeam>();
			for(MineralTeam otherTeam : this.teamPlayers.keySet()) {
				if(otherTeam == null || otherTeam == MineralTeam.Spectator || otherTeam == team) continue;
				if(this.teamPlayers.get(otherTeam).size() > 0) otherTeams.add(otherTeam);
			}
			// 2. Retirer des points pour chaque équipe trouvée
			for(MineralTeam otherTeam : otherTeams) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(this.getMineralTeam(p) == otherTeam) {
						p.sendMessage(ChatColor.GOLD + "Une équipe adverse vous a retiré " + String.valueOf(amount*value) + " points.");
					}
				}
				this.removeScore(otherTeam, amount*value);
			}
			sold = true;
		}
		
		if(sold == true) {
			if(type != Material.REDSTONE) {
				for(Player p : this.teamPlayers.get(team)) {
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
					p.sendMessage(ChatColor.AQUA + "Votre équipe a vendu " + String.valueOf(amount) + " " + type.toString() + " pour " + String.valueOf(amount*value) + " points.");
					p.sendMessage(ChatColor.AQUA + "Score total : " + String.valueOf(this.getScore(team)));
				}
			} else {
				for(Player p : this.teamPlayers.get(team)) {
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
					p.sendMessage(ChatColor.AQUA + "Votre équipe a retiré " + String.valueOf(amount*value) + " points aux équipes adverses en vendant " + String.valueOf(amount) + " " + type.toString() + ".");
				}
			}
			
		}
		return sold;
	}
	
	
	//////////////////////
	// METHODES PRIVEES //
	//////////////////////
	private HashMap<Integer, MineralTeam> getTeamLadder()
	{
		// 1. Récupérer les équipes disposant d'au moins 1 joueur
		ArrayList<MineralTeam> teams = new ArrayList<MineralTeam>();
		for(MineralTeam team : this.teamPlayers.keySet()) {
			if(team == null || team == MineralTeam.Spectator) continue;
			if(this.teamPlayers.get(team).size() > 0) teams.add(team);
		}
		// 2. Récupérer les scores de chaque équipe
		HashMap<MineralTeam, Double> scores = new HashMap<MineralTeam, Double>();
		for(MineralTeam team : teams) {
			scores.put(team, this.getScore(team));
		}
		// 3. Générer le classement
		HashMap<Integer, MineralTeam> ladder = new HashMap<Integer, MineralTeam>();
		int rank = 1;
		while(scores.keySet().size() > 0)
		{
			Double max = Double.MIN_VALUE;
			MineralTeam team = null;
			for(MineralTeam t : scores.keySet())
			{
				if(scores.get(t) > max) {
					max = scores.get(t);
					team = t;
				}
			}
			if(max == Double.MIN_VALUE) break;
			ladder.put(rank, team);
			rank++;
			scores.remove(team);
		}
		return ladder;
	}
}
