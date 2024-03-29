package me.dr_madman.hungercraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
	public String prefix = "[HC] " + ChatColor.ITALIC + "" + ChatColor.GREEN;
	private Player lastjoin = null;
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
			player.kickPlayer(prefix +  "You won! Server restarting(nice job btw)");
		}
		cfg.set("setup", true);
		cfg.set("motd", "Setting up next game");	
		plugin.saveConfig();
		Bukkit.getServer().broadcastMessage("deleting world");
		if( Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(plugin.getConfig().getString("option.worldname")), false)){
			deleteWorld(plugin.getConfig().getString("option.worldname"));
		}
	    plugin.generateWorld();
	    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			public void run() {
				plugin.stopGameEndofRound();
	        }
		}, 600L);

		
	}
		

	public void deleteWorld(String world){
        String line = "rm -rf " + world;
        try {
            Process child = Runtime.getRuntime().exec(line);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(child.getInputStream()));
 
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(child.getErrorStream()));
 
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
 
            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
                while ((s = stdError.readLine()) != null) {
            System.out.println(s);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
 
    }

/**
   public boolean deleteWorld(){
	   String worldname = "getConfig().getString("worldname")";
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
	   Bukkit.getServer().unloadWorld((Bukkit.getServer().getWorld(plugin.getConfig().getString("worldname"))), false);
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
		if (plugin.getConfig().getStringList("participants").size() == 3){
			Player player0 = Bukkit.getServer().getPlayer(plugin.getConfig().getStringList("participants").get(0));
			Player player1 = Bukkit.getServer().getPlayer(plugin.getConfig().getStringList("participants").get(1));
			Player player2 = Bukkit.getServer().getPlayer(plugin.getConfig().getStringList("participants").get(2));
			if(plugin.teamGrabber(player0) == plugin.teamGrabber(player1) && plugin.teamGrabber(player1) == plugin.teamGrabber(player2)){
				plugin.clearTags();
				Bukkit.getServer().broadcastMessage(prefix + "It seems that your team is the only team left! You know what that means, FIGHT!");
				
			}
			return;
		}
		if (plugin.getConfig().getStringList("participants").size() == 2){
			Player player0 = Bukkit.getServer().getPlayer(plugin.getConfig().getStringList("participants").get(0));
			Player player1 = Bukkit.getServer().getPlayer(plugin.getConfig().getStringList("participants").get(1));
			if(plugin.teamGrabber(player0) == plugin.teamGrabber(player1)){
				plugin.clearTags();
				Bukkit.getServer().broadcastMessage(prefix + "It seems that your team is the only team left! You know what that means, FIGHT!");
			}
			return;
		}
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
					if(checkList(player, "dead")){
						return;
					}
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
	public void setTeamTags(){
		for(int i : plugin.teams.keySet()){
			List<Player> players = plugin.teams.get(i);
			for(Player p: players){
				setTag(i, p);
				
			}
			
		}
	}
	public void setTag(int i, Player player){
		String pname = player.getName();
		if(pname.length() > 13){
			pname = pname.substring(0, pname.length() - 3);
		}
		player.setPlayerListName(pname + "[" + i + "]"); 
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
	public void addTag(Player player){
		List<String> tagged = plugin.getConfig().getStringList("nametag");
		tagged.add(player.getName() + plugin.teamGrabber(player));
		plugin.getConfig().set("nametag", tagged);
		plugin.saveConfig();
	}
	
	public int getTag(Player player){
		List<String> tagged = plugin.getConfig().getStringList("nametag");
		String nameint = null;
		for (String i : tagged){
			if(i.contains(player.getName()));
			nameint = i;
		}
		tagged.remove(nameint);
		plugin.getConfig().set("nametag", tagged);
		plugin.saveConfig();
		String number = nameint.substring(nameint.length()-1, nameint.length());
		Bukkit.getServer().broadcastMessage(number);
		int tag = Integer.parseInt(number);
		return tag;
		
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
	public void playerAttackEvent(EntityDamageByEntityEvent event){
		if(plugin.getConfig().getBoolean("option.teams")){
			if(plugin.getConfig().getBoolean("teamvote")){
				if (event.getDamager() instanceof Player){

					if (event.getEntity() instanceof Player){	
	
						Player damager= (Player) event.getDamager();
						Player hurt = (Player) event.getEntity();
						String hurttag = hurt.getPlayerListName().substring(hurt.getPlayerListName().length() - 3, hurt.getPlayerListName().length());
						String damagertag = damager.getPlayerListName().substring(damager.getPlayerListName().length() - 3, damager.getPlayerListName().length());

						if (hurttag.equalsIgnoreCase(damagertag)){
							damager.sendMessage(prefix + "You can't hurt your buddy!");
							event.setCancelled(true);
						}
					
					}
				}
			}
		}
		
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		int distancefromspawn = (plugin.getConfig().getInt("option.arenasize"))*(plugin.getConfig().getInt("option.arenasize"));
		if(checkActive()){
			if(player.getWorld() != Bukkit.getWorld(plugin.getConfig().getString("option.worldname"))){
				player.teleport(Bukkit.getWorld(plugin.getConfig().getString("option.worldname")).getSpawnLocation().add(0, 64, 0));
			}
			Double distance = player.getLocation().distanceSquared(Bukkit.getServer().getWorld(plugin.getConfig().getString("option.worldname")).getSpawnLocation());
			int distanceint = distance.intValue();
			if (distanceint > distancefromspawn - 7000){
				player.sendMessage(prefix + "You are approaching the force field. Contact with field results in death");
			}
			if(distance > distancefromspawn){
				if(plugin.getConfig().getBoolean("option.borderkill")){
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
			Bukkit.getServer().broadcastMessage(prefix + plugin.getConfig().getStringList("participants").size() + " players remain");
			plugin.getConfig().set("motd", (plugin.getConfig().getStringList("participants").size()  + " players remain"));

		}
	}
	@EventHandler
	public void playerJoin(PlayerLoginEvent event){
		cfg = plugin.getConfig();
		Player player = event.getPlayer();
		String pname = player.getName();
		List<String> pNameList = new ArrayList<String>();
		for (Player players : Bukkit.getServer().getOnlinePlayers()){
			pNameList.add(players.getName());
		}
		displayMessageDelayed(player, prefix + "Choose a class with /kit, or if you are a vip, /vipkit");
		displayMessageDelayed(player, prefix + "Type /votestart to vote to start");
		displayMessageDelayed(player, prefix + "Get info about the server with /info");
		displayMessageDelayed(player, prefix + "Player list: " + pNameList);
		displayMessageDelayed(player, prefix + "Want to play a game with teams? Type /vote yes");
		displayMessageDelayed(player, prefix + plugin.getConfig().getString("option.joinmessage"));
		if(Bukkit.getServer().getOnlinePlayers().length == Bukkit.getServer().getMaxPlayers()){
			if(player.hasPermission("hc.vip")){
				lastjoin.kickPlayer(prefix + "Making room for a VIP");
			}
			
		}
		if(cfg.getBoolean("setup")){
			event.disallow(Result.KICK_OTHER, prefix + "Setting up next game");
			return;
		}
		if(checkActive()){
			if(player.isOp() || player.hasPermission("hc.gm")){
				plugin.setGM(player);
				player.sendMessage(prefix + " You are now a GM");
				plugin.isgm.put(player, true);
			}
			if(cfg.getStringList("dead").contains(pname)){
					event.disallow(Result.KICK_OTHER,prefix + "You have died, wait for the next game");
					return;

			}
			if(cfg.getStringList("leftgame").contains(pname)){
				event.allow();
				removeList(player,"leftgame");
				addList(player, "participants");
				setTag(getTag(player), player);
				return;
			}
			else{
				
				event.disallow(Result.KICK_OTHER, "Game in progress");
				return;
			}
		}
		else {
			player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
			if(Bukkit.getServer().getOnlinePlayers().length >= 1){
				/**if(!plugin.countdownactive){
					plugin.countdown(player);
				}
				*/

				 

			}
			plugin.tostart = Bukkit.getServer().getOnlinePlayers().length / (4/3);
		}
		if (!player.hasPermission("hc.vip")){
			lastjoin = player;
		}
	}
	@EventHandler
	public void playerLeave(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(checkActive()){
			if(checkList(player, "dead")){
				if(Bukkit.getServer().getOnlinePlayers().length == 5){
					Bukkit.getServer().getWorld(plugin.getConfig().getString("option.worldname")).setDifficulty(Difficulty.NORMAL);
					Bukkit.getServer().broadcastMessage(prefix + "Difficulty is now normal");
				}
				if(Bukkit.getServer().getOnlinePlayers().length == 3){
					Bukkit.getServer().getWorld(plugin.getConfig().getString("option.worldname")).setDifficulty(Difficulty.HARD);
					Bukkit.getServer().broadcastMessage(prefix + "Difficulty is now hard");
				} 
				return;
			}
			else{
				addTag(player);
				removeList(player, "participants");
				addList(player, "leftgame");
				if (checkList(player, "leftgame")){
					checkActivePlayer(player);
				}
			}
			
			checkWinner();

		}
		else { plugin.tostart = Bukkit.getServer().getOnlinePlayers().length / (4/3);
		 Bukkit.broadcastMessage(String.valueOf(plugin.tostart));
		}
		CompassTrackerUpdater.removePlayer(player);
		
	}
	@EventHandler
	public void changeMOTD(ServerListPingEvent event){
		event.setMotd(plugin.getConfig().getString("motd"));
	}
	
}
