package fr.hexzey.mineralcontest.tools;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class PlayerFreeze
{
	private ArrayList<Player> freezedPlayers;
	
	public PlayerFreeze()
	{
		this.freezedPlayers = new ArrayList<Player>();
	}
	
	public boolean isFreezed(Player player)
	{
		return this.freezedPlayers.contains(player);
	}
	
	public void freeze(Player player)
	{
		this.freezedPlayers.add(player);
	}
	
	public void unfreeze(Player player)
	{
		this.freezedPlayers.remove(player);
	}
	
	public ArrayList<Player> getFreezedPlayers()
	{
		return this.freezedPlayers;
	}
}
