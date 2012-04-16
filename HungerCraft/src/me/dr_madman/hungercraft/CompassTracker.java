package me.dr_madman.hungercraft;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;


public class CompassTracker implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static ArrayList<String> toggleActivated = new ArrayList<String>();
	@EventHandler
	public void onCompassRightClick(PlayerInteractEvent event){
		Player player = event.getPlayer();
		String pname = player.getName();
		if(player.getInventory().getItemInHand().getType() == Material.COMPASS){
			if (toggleActivated.contains(pname)){	
				toggleActivated.remove(pname);
				player.sendMessage("You are no longer tracking the nearest player");
			}
			else{	
				Player[] listOfPlayers = Bukkit.getServer().getOnlinePlayers();
				List<String> players = new ArrayList<String>();
				for (Player par : listOfPlayers){
					String parname = par.getName();
					players.add(parname);
				}
				players.remove(player.getName());
				if(players.size() == 0){
					player.sendMessage("There are no players to track!");
				}
				else {
					toggleActivated.add(pname);
					Player newplayer = null;
					String newplayername = null;
					double lowvalue = 100000000;
					for (String p : players){
						Player pl = Bukkit.getServer().getPlayer(p);
						double tempval = player.getLocation().distance(pl.getLocation());
						if(tempval < lowvalue){
							lowvalue = tempval;
							newplayer = pl;
							newplayername = p;
						}		
					}
					player.sendMessage("You are now tracking " + newplayername);
					newplayer.sendMessage(pname + " is now tracking you, Beware!");
					CompassTrackerUpdater.setWatcher(player, newplayer);
				}
			}
		}
	}
}
