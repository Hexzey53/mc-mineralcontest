package fr.hexzey.mineralcontest;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import fr.hexzey.mineralcontest.enumerators.GameState;
import fr.hexzey.mineralcontest.enumerators.MineralTeam;
import fr.hexzey.mineralcontest.populators.CavePopulator;
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
import fr.hexzey.mineralcontest.tools.ItemBuilder;

public class Events implements Listener
{
	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		if(event.getWorld().getName().equalsIgnoreCase("mineralmap"))  {
			Bukkit.getWorld("mineralmap").setKeepSpawnInMemory(false);
			// générateurs de charbon
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new CoalVeinsPopulator_Low());
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new CoalVeinsPopulator_Medium());
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new CoalVeinsPopulator_High());
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new CoalVeinsPopulator_VeryHigh());
			// générateurs de fer
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new IronVeinsPopulator_Low());
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new IronVeinsPopulator_Medium());
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new IronVeinsPopulator_High());
			// générateurs d'or
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new GoldVeinsPopulator());
			// générateurs de diamant
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new DiamondVeinsPopulator());
			// générateurs de redstone
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new RedstoneVeinsPopulator());
			// générateurs d'émeraude
			Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new EmeraldVeinsPopulator());
			
			//Bukkit.getWorld("mineralmap").getPopulators().add((BlockPopulator)new CavePopulator());
		}
	}
	
	@EventHandler
	public void onWorldLoad(WorldLoadEvent event)
	{
		String worldname = event.getWorld().getName();
		if(worldname.equalsIgnoreCase("mineralmap"))
		{
			Bukkit.getWorld("mineralmap").setAutoSave(false);
		}
	}
	
	@EventHandler
	public void onPlayerCraft(CraftItemEvent event)
	{
		/**
		 * Empêcher les joueurs de fabriquer des objets
		 */
		event.setCancelled(false);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		/**
		 * Gestion des joueurs lors de la connexion
		 */
		if(Main.mineralGame.getGameState() == GameState.Waiting) {
			if(Main.mapGenerated == false) {
				event.getPlayer().teleport(Main.getConfiguration().getLobbyLocation());
			} else {
				event.getPlayer().teleport(Main.getConfiguration().getChestLocation());
			}
			event.getPlayer().setGameMode(GameMode.SURVIVAL);
			event.getPlayer().setHealth(20);
			event.getPlayer().setFoodLevel(20);
			Main.mineralGame.setPlayerTeam(MineralTeam.Spectator, event.getPlayer()); // équipe par défaut
		}
		else {
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
			event.getPlayer().teleport(Main.getConfiguration().getChestLocation());
		}
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		/**
		 * Gestion des joueurs lors d'une déconnexion
		 */
		Main.mineralGame.removePlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		/**
		 * Empêcher les joueurs de poser des blocs au lobby
		 * Empêcher les joueurs de poser au centre de la carte
		 */
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return; // aucune restriction en créatif
		
		if(Main.mineralGame.getGameState() == GameState.Waiting)
		{
			event.setCancelled(true);
			return;
		}
		
		Location loc = event.getBlock().getLocation();
		if(loc.getWorld().getName().equalsIgnoreCase("mineralmap")) {
			Location middle = new Location(Bukkit.getWorld("mineralmap"), 0, loc.getY(), 0);
			if(middle.distance(loc) <= 45) {
				event.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas poser ce bloc (trop près du centre).");
				event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_IRON_GOLEM_STEP, 1.0f, 1.0f);
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		/**
		 * Empêcher les joueurs de casser des blocs au lobby
		 * Empêcher les joueurs de casser le centre de la carte
		 */
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return; // aucune restriction en créatif
		
		if(Main.mineralGame.getGameState() == GameState.Waiting)
		{
			event.setCancelled(true);
			return;
		}
		
		Location loc = event.getBlock().getLocation();
		if(loc.getWorld().getName().equalsIgnoreCase("mineralmap")) {
			Location middle = new Location(Bukkit.getWorld("mineralmap"), 0, loc.getY(), 0);
			if(middle.distance(loc) <= 45) {
				event.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas casser ce bloc (trop près du centre).");
				event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_IRON_GOLEM_STEP, 1.0f, 1.0f);
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		
		int clickedSlot = event.getSlot();
		ItemStack resources = event.getCurrentItem();
		if(resources == null) { return; }
		
		if(Main.mineralGame.getGameState() == GameState.Waiting && event.getView().getTitle().equals("Choisir une equipe")) {
			// un clic a eu lieu sur le menu de selection d'équipe
			ItemStack clickedItem = event.getCurrentItem();
			if(clickedItem == null) { return; } // securite anti-crash si le slot cliqué est vide
			
			if(clickedItem.getType() == Material.BARRIER) {
				Main.mineralGame.setPlayerTeam(MineralTeam.Spectator, player);
			} else if(clickedItem.getType() == Material.RED_WOOL) {
				Main.mineralGame.setPlayerTeam(MineralTeam.Red, player);
			} else if(clickedItem.getType() == Material.LIME_WOOL) {
				Main.mineralGame.setPlayerTeam(MineralTeam.Green, player);
			} else if(clickedItem.getType() == Material.BLUE_WOOL) {
				Main.mineralGame.setPlayerTeam(MineralTeam.Blue, player);
			} else if(clickedItem.getType() == Material.YELLOW_WOOL) {
				Main.mineralGame.setPlayerTeam(MineralTeam.Yellow, player);
			}
			player.closeInventory();
			return;
		}
		if(Main.mineralGame.getGameState() == GameState.Running && event.getView().getTitle().equals("Deposez vos ressources")) {
			// un clic a eu lieu sur le menu de dépôt des ressources
			if(Main.mineralGame.sellResources(resources, player) == true) {
				player.getInventory().setItem(clickedSlot, new ItemStack(Material.AIR));
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
				player.sendMessage(ChatColor.RED + "Vous ne pouvez pas vendre cet objet !");
				event.setCancelled(true);
			};
			return;
		}
		if(Main.mineralGame.getGameState() == GameState.Running && event.getView().getTitle().equals("Crochetage...")) {
			event.setCancelled(true);
			return;
		}
		if(Main.mineralGame.getGameState() == GameState.Running && resources.getType() == Material.LAPIS_LAZULI) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player)event.getPlayer();
		if(Main.mineralGame.getGameState() == GameState.Running && Main.mineralGame.getUnlocker() == player) {
			Main.mineralGame.cancelUnlocking();
			return;
		}
		if(Main.mineralGame.getGameState() == GameState.Running && event.getInventory() instanceof EnchantingInventory) {
			EnchantingInventory inv = (EnchantingInventory) event.getInventory();
			inv.setItem(1, null);
		}
	}
	
	@EventHandler
	public void onInteractEvent(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		
		if(Main.mineralGame.getGameState() == GameState.Waiting && event.getPlayer().getGameMode() == GameMode.SURVIVAL) // GESTION DU SELECTEUR DE'EQUIPE
		{
			if(event.getItem() == null) { return; } // securite interaction main vide
			if(event.getItem().getType() == Material.BARRIER) Main.mineralGame.openTeamSelectInventoryFor(player);
			else if(event.getItem().getType() == Material.RED_WOOL) Main.mineralGame.openTeamSelectInventoryFor(player);
			else if(event.getItem().getType() == Material.LIME_WOOL) Main.mineralGame.openTeamSelectInventoryFor(player);
			else if(event.getItem().getType() == Material.BLUE_WOOL) Main.mineralGame.openTeamSelectInventoryFor(player);
			else if(event.getItem().getType() == Material.YELLOW_WOOL) Main.mineralGame.openTeamSelectInventoryFor(player);
			return;
		}
		
		Block clickedBlock = null;
		try {
			clickedBlock = event.getClickedBlock();
		} catch(Exception e) { }
		
		
		if(Main.mineralGame.getGameState() == GameState.Running && clickedBlock != null && clickedBlock.getType() == Material.CHEST && event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			Location chestLoc = clickedBlock.getLocation();
			MineralTeam team = Main.mineralGame.getTeamSafezoneAt(chestLoc);
			
			if(team != null && team != MineralTeam.Spectator && team == Main.mineralGame.getMineralTeam(player)) {
				Main.mineralGame.openResourceDepotInventoryFor(player);
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		/**
		 * Gestion du mouvement des joueurs (ex: freeze)
		 */
		
		// aucune restriction pour les joueurs en spectateur
		if(event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
		
		// vérifier si le joueur est affecté par un freeze
		if(Main.playerFreeze.isFreezed(event.getPlayer())) {
			Location source = event.getFrom();
			Location destination = event.getTo();
			destination.setX(source.getX());
			destination.setZ(source.getZ());
			event.setTo(destination);
			return;
		}
		
		if(Main.mineralGame.getGameState() == GameState.Waiting) {
			event.getPlayer().setFoodLevel(20);
		}
		
		// empêcher les joueurs de sortir de l'arène tant que la partie n'a pas démarré
		if(event.getTo().getWorld().getName().equals("mineralmap") && Main.mineralGame.getGameState() == GameState.Waiting) {
			Double y = event.getTo().getY();
			if(y > Main.getConfiguration().getChestLocation().getY() + 5) {
				event.getPlayer().teleport(Main.getConfiguration().getChestLocation());
				event.getPlayer().playSound(event.getTo(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
		}
		
		// empêcher les joueurs d'entrer dans les bases adverses
		if(Main.mineralGame.getGameState() == GameState.Running)
		{
			MineralTeam team = Main.mineralGame.getMineralTeam(event.getPlayer());
			MineralTeam areaOwner = Main.mineralGame.getTeamSafezoneAt(event.getTo());
			if(team != null && team != MineralTeam.Spectator && areaOwner != null && areaOwner != team) {
				Vector from = event.getFrom().toVector();
				Vector to = event.getTo().toVector();
				Vector knockback = from.subtract(to).normalize();
				knockback.multiply(1.2);
				knockback.setY(0.75f);
				event.getPlayer().setVelocity(knockback);
				event.getPlayer().damage(3d);
				event.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas entrer dans une base ennemie");
				event.getPlayer().playSound(event.getTo(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 1.0f);
				return;
			}
		}
	}
	
	@EventHandler
	public void onDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			Player victim = (Player)event.getEntity();
			if(Main.mineralGame.getGameState() == GameState.Waiting) {
				event.setCancelled(true);
				return;
			}
			if(Main.playerFreeze.isFreezed(victim) == true) {
				event.setCancelled(true);
				return;
			}
			Location loc = victim.getLocation();
			MineralTeam team = Main.mineralGame.getMineralTeam(victim);
			MineralTeam areaOwner = Main.mineralGame.getTeamSafezoneAt(victim.getLocation());
			// annuler les dégâts si le joueur victime est dans sa base
			if(team != null && team != MineralTeam.Spectator && areaOwner != null && areaOwner == team) {
				if(event.getDamager() instanceof Player) {
					Player damager = (Player)event.getDamager();
					damager.sendMessage(ChatColor.RED + "Vous ne pouvez pas attaquer un joueur qui est dans sa base.");
					damager.playSound(damager.getLocation(), Sound.ENTITY_IRON_GOLEM_STEP, 1.0f, 1.0f);
				}
				event.setCancelled(true);
				return;
			}
			// annuler les dégâts si le damager et la victime sont de la même équipe
			if(Main.mineralGame.getGameState() == GameState.Running && event.getDamager() instanceof Player) {
				Player damager = (Player)event.getDamager();
				MineralTeam damagerTeam = Main.mineralGame.getMineralTeam(damager);
				if(team != null && damagerTeam != null && team == damagerTeam) {
					event.setCancelled(true);
					return;
				}
			}
			// éliminer le joueur si ses pv deviennent inférieurs à 0
			if(victim.getHealth() - event.getDamage() <= 0) {
				Main.mineralGame.killPlayer(victim);
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player victim = (Player)event.getEntity();
			if(Main.mineralGame.getGameState() == GameState.Waiting) {
				event.setCancelled(true);
				return;
			}
			MineralTeam team = Main.mineralGame.getMineralTeam(victim);
			MineralTeam areaOwner = Main.mineralGame.getTeamSafezoneAt(victim.getLocation());
			// annuler les dégâts si le joueur victime est dans sa base
			if(team != null && team != MineralTeam.Spectator && areaOwner != null && areaOwner == team) {
				event.setCancelled(true);
				return;
			}
			if(Main.mineralGame.getUnlocker() == victim) Main.mineralGame.cancelUnlocking();
			if(Main.playerFreeze.isFreezed(victim) == true) {
				event.setCancelled(true);
				return;
			}
			if(victim.getHealth() - event.getDamage() <= 0) {
				Main.mineralGame.killPlayer(victim);
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		/**
		 * Empêcher les explosions de casser des blocs
		 */
		event.blockList().clear();
		return;
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		/**
		 * Cet event permet de démarrer le crochetage du coffre au centre de l'arène
		 */
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
		
		if(Main.mineralGame.getGameState() == GameState.Waiting) {
			event.setCancelled(true);
			return;
		}
		
		Entity target = event.getRightClicked();
		if(target.getType() != EntityType.MINECART_CHEST || target != Main.mineralGame.getMinecartChestToUnlock()) return;
		
		if(Main.mineralGame.getGameState() == GameState.Running) {
			Main.mineralGame.startUnlocking(player);
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		/**
		 * Changer les drops des poulets vers la fin de la partie
		 */
		Entity entity = event.getEntity();
		if(entity.getType() == EntityType.CHICKEN) {
			if(Main.mineralGame.getRemainingSeconds() <= Main.getConfiguration().getChickensTime())
			{
				event.getDrops().clear();
				ItemBuilder builder = new ItemBuilder(Material.COAL);
				Random random = new Random();
				int probability = random.nextInt(100); // [0-99]
				if(probability >= 80) builder.material(Material.GOLD_INGOT); // 20% de gold
				else if(probability >= 15) builder.material(Material.IRON_INGOT); // 65% de iron
				// reste = 15% de coal
				event.getDrops().add(builder.build());
				return;
			}
			else {
				event.getDrops().clear();
				ItemStack foodDrop = new ItemStack(Material.COOKED_CHICKEN, 2);
				event.getDrops().add(foodDrop);
				ItemStack leatherDrop = new ItemStack(Material.FEATHER, 1);
				event.getDrops().add(leatherDrop);
				return;
			}
		}
		if(entity.getType() == EntityType.COW) {
			event.getDrops().clear();
			ItemStack foodDrop = new ItemStack(Material.COOKED_BEEF, 2);
			event.getDrops().add(foodDrop);
			ItemStack leatherDrop = new ItemStack(Material.LEATHER, 1);
			event.getDrops().add(leatherDrop);
			return;
		}
		if(entity.getType() == EntityType.PIG) {
			event.getDrops().clear();
			ItemStack foodDrop = new ItemStack(Material.COOKED_PORKCHOP, 2);
			event.getDrops().add(foodDrop);
			return;
		}
		if(entity.getType() == EntityType.SHEEP) {
			event.getDrops().clear();
			ItemStack foodDrop = new ItemStack(Material.COOKED_MUTTON, 2);
			event.getDrops().add(foodDrop);
			return;
		}
		if(entity.getType() == EntityType.RABBIT) {
			event.getDrops().clear();
			ItemStack foodDrop = new ItemStack(Material.COOKED_RABBIT, 2);
			event.getDrops().add(foodDrop);
			return;
		}
		if(entity.getType() == EntityType.LLAMA) {
			event.getDrops().clear();
			ItemStack foodDrop = new ItemStack(Material.COOKED_BEEF, 2);
			event.getDrops().add(foodDrop);
			return;
		}
	}
	
	@EventHandler
	public void onVehicleMove(VehicleEntityCollisionEvent event) {
		/**
		 * Empêcher le minecart du centre (à crocheter) d'être poussé par d'autres entités
		 */
		if(event.getVehicle().getType() == EntityType.MINECART_CHEST) {
			event.setCollisionCancelled(true);
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		Material type = event.getItem().getType();
		if(type == Material.LEATHER_HELMET || type == Material.IRON_CHESTPLATE || type == Material.LEATHER_LEGGINGS || type == Material.LEATHER_BOOTS) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		/**
		 * Empêcher les joueurs en attente d'un respawn de se téléporter vers les autres joueurs (gamemode spectator)
		 */
		if(Main.mineralGame.getGameState() == GameState.Running) {
			if(event.getCause() == TeleportCause.SPECTATE && Main.mineralGame.getMineralTeam(event.getPlayer()) != null
					&& Main.mineralGame.getMineralTeam(event.getPlayer()) != MineralTeam.Spectator) {
				event.getPlayer().sendMessage(ChatColor.RED + "Action impossible.");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		/**
		 * Restrictions sur les drops d'items
		 */
		Player player = event.getPlayer();
		// aucune restriction en créatif/spectateur
		if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
		
		if(Main.mineralGame.getGameState() == GameState.Waiting) {
			event.setCancelled(true);
			return;
		}
		
		if(Main.mineralGame.getGameState() == GameState.Running) {
			Material type = event.getItemDrop().getItemStack().getType();
			if(type == Material.LEATHER_HELMET || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_LEGGINGS || type == Material.LEATHER_BOOTS) {
				player.sendMessage(ChatColor.RED + "Vous ne pouvez pas jeter cet item !");
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f);
				event.setCancelled(true);
				return;
			}
			if(type == Material.LAPIS_LAZULI) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		// 1. Récupérer l'envoyeur du message ainsi que son équipe
		Player sender = event.getPlayer();
		MineralTeam team = Main.mineralGame.getMineralTeam(sender);
		if(team == null) team = MineralTeam.Spectator;
		// 2. Récupérer la couleur à afficher pour le pseudo
		ChatColor color = ChatColor.WHITE;
		if(team == MineralTeam.Spectator) color = ChatColor.LIGHT_PURPLE;
		else if(team == MineralTeam.Red) color = ChatColor.RED;
		else if(team == MineralTeam.Green) color = ChatColor.GREEN;
		else if(team == MineralTeam.Blue) color = ChatColor.BLUE;
		else if(team == MineralTeam.Yellow) color = ChatColor.YELLOW;
		// 3. Générer le nouveau message formatté
		String message = ChatColor.WHITE + "[" + color + sender.getName() + ChatColor.WHITE + "] " + event.getMessage();
		// 4. Envoyer le nouveau message à tous les joueurs connectés
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(message);
		}
		// 5. Annuler l'event
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		/**
		 * Empêcher les monstres et animaux d'apparaître tant que la partie n'a pas débuté
		 */
		if(Main.mineralGame.getGameState() != GameState.Running) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		/**
		 * Ajouter du lapis lors de l'ouverture d'une table d'enchant
		 */
		if(Main.mineralGame.getGameState() == GameState.Running && event.getInventory() instanceof EnchantingInventory) {
			EnchantingInventory inv = (EnchantingInventory) event.getInventory();
			ItemStack lapis = new ItemStack(Material.LAPIS_LAZULI, 64);
			inv.setItem(1, lapis);
		}
	}
}
