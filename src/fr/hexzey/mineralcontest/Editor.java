package fr.hexzey.mineralcontest;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Editor
{
	///////////////
	// SINGLETON //
	///////////////
	private static Editor instance = null;
	public static Editor getInstance()
	{
		if(Editor.instance == null) Editor.instance = new Editor();
		return Editor.instance;
	}
	
	//////////////////////
	// ATTRIBUTS PRIVES //
	//////////////////////
	private Player player = null;
	
	private Location corner1;
	private Location corner2;
	
	private Location safeCorner1;
	private Location safeCorner2;
	
	private Location redSpawn; private Location greenSpawn; private Location blueSpawn; private Location yellowSpawn;
	
	private Location chestLocation;
	
	public Editor()
	{
		this.player = null;
		
		this.corner1 = null;
		this.corner2 = null;
		
		this.redSpawn = null;
		this.greenSpawn = null;
		this.blueSpawn = null;
		this.yellowSpawn = null;
		
		this.chestLocation = null;
	}
	
	public Player getPlayer() { return this.player; }
	public void setPlayer(Player player) { this.player = player; }
	
	public void setCorner1(Location loc) { this.corner1 = loc; }
	public void setCorner2(Location loc) { this.corner2 = loc; }
	
	public void setSafeCorner1(Location loc) { this.safeCorner2 = loc; }
	public void setSafeCorner2(Location loc) { this.safeCorner2 = loc; }
	
	public void setRedSpawn(Location loc) { this.redSpawn = loc; }
	public void setGreenSpawn(Location loc) { this.greenSpawn = loc; }
	public void setBlueSpawn(Location loc) { this.blueSpawn = loc; }
	public void setYellowSpawn(Location loc) { this.yellowSpawn = loc; }
	
	public void setChestLocation(Location loc) { this.chestLocation = loc; }
	
	public void save()
	{
		// A FAIRE
	}
	
	public void load()
	{
		// A FAIRE
	}
}
