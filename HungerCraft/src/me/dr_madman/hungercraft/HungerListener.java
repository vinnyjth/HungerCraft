package me.dr_madman.hungercraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class HungerListener implements Listener {
	private HungerCraft plugin;
	private FileConfiguration cfg;
	private int id;
	public HungerListener(HungerCraft instance) {plugin = instance;}

	Logger log;
	public void addLocation(Location loc, int i){
		cfg = plugin.getConfig();
		cfg.set(loc.getWorld().getName() + "." + i + "." + "X", loc.getBlockX());
		cfg.set(loc.getWorld().getName() + "." + i + "." + "Y", loc.getBlockY());
		cfg.set(loc.getWorld().getName() + "." + i + "." + "Z", loc.getBlockZ());
		this.plugin.saveConfig();
	}
	
	public Location getLocation(Player p, int i){
		cfg = plugin.getConfig();
		int x = cfg.getInt(p.getWorld().getName() + "." + i + "." + "X");
        int y = cfg.getInt(p.getWorld().getName() + "." + i + "." + "Y");
        int z = cfg.getInt(p.getWorld().getName() + "." + i + "." + "Z");
        Location loc = new Location(p.getWorld(), x, y, z);
        return loc;
	}
	
	public boolean checkActive(){
		cfg = plugin.getConfig();
		boolean activestate = cfg.getBoolean("isactive");
		return activestate;
	}
	public void setupNewGame(){
		for(Player player :Bukkit.getServer().getOnlinePlayers()){
			player.kickPlayer("Server restarting(nice job btw)");
		}
		cfg.set("setup", true);
		cfg.set("motd", "Setting up next game");	
		plugin.saveConfig();
		for (Entity entity: Bukkit.getServer().getWorld("world2").getEntities()){
			entity.remove();
		Bukkit.getServer().broadcastMessage("deleting world");
		 Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld("world2"), false);
		deleteWorld("world2");
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			   public void run() {
				   plugin.generateWorld();
				   plugin.stopGameEndofRound();
			   }
			}, 300L);
		}
					  
					   

	}
		

	public void deleteWorld(String world){
		String line = "rm -rf " + world;
		try {
			Runtime.getRuntime().exec(line);
			Bukkit.getServer().broadcastMessage("Deleted world");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

/**
   public boolean deleteWorld(){
	   String worldname = "world2";
	   if (Bukkit.getServer().unloadWorld(worldname, false)) {
           Bukkit.getServer().broadcastMessage("***********");
           Bukkit.getServer().broadcastMessage("Unloaded World");
           Bukkit.getServer().broadcastMessage("***********");
           File world = new File(worldname);
           Bukkit.getServer().broadcastMessage("Deleting World");
           if (this.deleteDir(world)) {
        	   		Bukkit.getServer().broadcastMessage("World Deleted");
           }
           else {
        	   		Bukkit.getServer().broadcastMessage("Error Deleting World");
           }
           Bukkit.getServer().broadcastMessage("***********");

   else {
	   Bukkit.getServer().broadcastMessage("Failed To Unload World");
   	}
  
   return true;
   }
     }
  */
   public boolean deleteDir(File dir) {
	   Bukkit.getServer().unloadWorld((Bukkit.getServer().getWorld("world2")), false);
       if (dir.isDirectory()) {
               String[] children = dir.list();
               for (String element : children) {
            	   Bukkit.getServer().broadcastMessage(" - deleting " + element);
                       boolean success = this.deleteDir(new File(dir, element));
                       if (!success) {
                               return false;
                       }
               }
       }
       return dir.delete();
   }
	


        
	public boolean checkRightClickGoldBlock(Event event){
		if(((PlayerInteractEvent) event).getAction() == Action.RIGHT_CLICK_BLOCK ){
			if(((PlayerInteractEvent) event).getClickedBlock().getType() == Material.GOLD_BLOCK){
				return true;
			}
		}
		return false;
	}
	public boolean checkList(Player player, String list){
		cfg = plugin.getConfig();
		List<String> templist = cfg.getStringList(list);
		String pname = player.getName();
		if(templist.contains(pname)){
			return true;
		}
		return false;
	}
	public void checkWinner(){
		if(plugin.getConfig().getStringList("participants").size() == 1){
			List<String> winners = plugin.getConfig().getStringList("participants");
			String winner = winners.get(0);
			Player winplayer = Bukkit.getPlayer(winner);
			winMessage(winplayer);
			
		}
	}
	public void removeList(Player player, String list){
		cfg = plugin.getConfig();
		List<String> templist = cfg.getStringList(list);
		String pname = player.getName();
		if(templist.contains(pname)){
			templist.remove(pname);
		}
		cfg.set(list, templist);
	    plugin.saveConfig();
	}
	public void addList(Player player, String list){
		cfg = plugin.getConfig();
		List<String> templist = cfg.getStringList(list);
		String pname = player.getName();
		templist.add(pname);
		cfg.set(list, templist);
	    plugin.saveConfig();
	}
	public void checkActivePlayer(final Player player){
		if(checkActive()){
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				public void run() {
					if(checkList(player, "leftgame")){
						removeList(player,"leftgame");
						String pname = player.getName();
						Bukkit.broadcastMessage(pname + " Has forfit!");
					}
		        	
		        }
			}, 1200L);
		}
	}
	public void displayMessageDelayed(final Player player, final String string){
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				public void run() {
					player.sendMessage(string);
					}
		        	
			}, 20);
		
	}
	public void winMessage(Player player){
		String pname = player.getName();
    	final String[] messagelist = {(ChatColor.GOLD + "We have our winner!"),(ChatColor.GOLD + "And the winner is:"),(ChatColor.GOLD + pname + "!!!11!!1!1"),(pname + "Prepare to get kicked")};
		this.id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int times = 0;

		    public void run() {
		    	Bukkit.getServer().broadcastMessage(messagelist[times]);
		    	times ++;
		    	if (times == 4){
		    		setupNewGame();
		    		Bukkit.getScheduler().cancelTask(id);
		    	}
		    	

		    }
		}, 1L, 20L);
	}
	public List<String> listKits(){
		File dir1 = plugin.getDataFolder();
		File dir = new File(dir1, "Classes");

		String[] children = dir.list();
		List<String> classes = new ArrayList<String>();
		for (String i : children){
			String j = i.substring(0, i.length()-4);
			classes.add(j);
		}
		return classes;
	}
	
	@EventHandler
	public void blockInteract(PlayerInteractEvent event) throws Exception{
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if(checkRightClickGoldBlock(event)){
			if(player.isOp() || event.getPlayer().hasPermission("hc.setspawn")){
				Location blocklocation = block.getLocation();
				addLocation(blocklocation, 0);
				
				
			}
		}
		if(!checkActive()){
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		int distancefromspawn = (plugin.getConfig().getInt("arenasize"))*(plugin.getConfig().getInt("arenasize"));
		if(checkActive()){
			Double distance = player.getLocation().distanceSquared(Bukkit.getServer().getWorld("world2").getSpawnLocation());
			int distanceint = distance.intValue();
			if (distanceint > distancefromspawn - 3000){
				player.sendMessage("You are approaching the force field");
			}
			if(distance > distancefromspawn){
				if(plugin.getConfig().getBoolean("borderkill")){
					player.getWorld().createExplosion(player.getLocation(), 4F, true);
				}
				else {
					player.teleport((event.getTo().toVector().multiply(event.getFrom().toVector())).toLocation(player.getWorld()));
				}
				
				
			}
		}
	}
	@EventHandler
	public void deathEvent(PlayerDeathEvent event){
		if(checkActive()){
			Player player = (Player) event.getEntity();
			String kickmessage = event.getDeathMessage();
			player.kickPlayer(kickmessage);
			removeList(player, "participants");
			addList(player, "dead");
			Bukkit.getServer().broadcastMessage(plugin.getConfig().getStringList("participants").size() + " players remain");
			plugin.getConfig().set("motd", (plugin.getConfig().getStringList("participants").size()  + " players remain"));

		}
	}
	@EventHandler
	public void playerJoin(PlayerLoginEvent event){
		cfg = plugin.getConfig();
		Player player = event.getPlayer();
		String pname = player.getName();
		if(cfg.getBoolean("setup")){
			event.disallow(Result.KICK_OTHER, "Setting up next game");
			return;
		}
		if(checkActive()){
			if(cfg.getStringList("dead").contains(pname)){
					event.disallow(Result.KICK_OTHER, "You have died, wait for the next game");
					return;

			}
			if(cfg.getStringList("leftgame").contains(pname)){
				event.allow();
				removeList(player,"leftgame");
				addList(player, "participants");
				return;
			}
			if(player.isOp()){
				event.allow();
				removeList(player,"leftgame");
				addList(player, "participants");
			}
			else{
				event.disallow(Result.KICK_OTHER, "Game in progress");
				return;
			}
		}
		else {
			player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
			displayMessageDelayed(player, "Choose a class with /kit ");
			if(Bukkit.getServer().getOnlinePlayers().length == 1){
				Bukkit.getServer().broadcastMessage("Round is starting");
				plugin.countdown(player);
			}
		}
	}
	@EventHandler
	public void playerLeave(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(checkActive()){
			if(checkList(player, "dead")){
				return;
			}
			else{
				removeList(player, "participants");
				addList(player, "leftgame");
				if (checkList(player, "leftgame")){
					checkActivePlayer(player);
				}
			}
			
			checkWinner();

		}
		
	}
	@EventHandler
	public void changeMOTD(ServerListPingEvent event){
		event.setMotd(plugin.getConfig().getString("motd"));
	}
	
}
