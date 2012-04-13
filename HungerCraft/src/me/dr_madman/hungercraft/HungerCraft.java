package me.dr_madman.hungercraft;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class HungerCraft extends JavaPlugin implements CommandExecutor{
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger("Minecraft");
	public static FileConfiguration cfg;
	public static HungerCraft plugin;
	public static PluginDescriptionFile plugdes;
	public HungerListener hungerlistener = new HungerListener(this);
	public RandomTeleport randomteleport = new RandomTeleport(this);
	
	public Location getLocation(Player p, int i){
		int x = cfg.getInt(p.getWorld().getName() + "." + i + "." + "X");
        int y = cfg.getInt(p.getWorld().getName() + "." + i + "." + "Y");
        int z = cfg.getInt(p.getWorld().getName() + "." + i + "." + "Z");
        Location loc = new Location(p.getWorld(), x, y, z);
        return loc;
	}
	
	@Override
	public void onEnable(){
		plugdes = getDescription();
		System.out.println("PLUGDESC NAME: "+plugdes.getName());
        getServer().getPluginManager().registerEvents(hungerlistener, this);
        cfg = getConfig();
        cfg.addDefault("isactive", false);
        cfg.addDefault("usespawnpoints", false);
        cfg.addDefault("spawnradius", 30);
        cfg.addDefault("arenasize", 50);
        cfg.addDefault("borderkill", true);
        cfg.addDefault("participants", null);
        cfg.addDefault("leftgame", null);
        cfg.addDefault("dead", null);
        getConfig().options().copyDefaults(true);
        saveConfig();
        List<World> worlds = Bukkit.getServer().getWorlds();
        for (World world : worlds){
        	world.setSpawnLocation(0, 64, 0);
        	world.setPVP(false);
        	
        }
        
        
        
	}
	@Override
	public void onDisable() {
		System.out.println(plugdes.getName() + " is now disabled.");
    }
	
	public static JavaPlugin getPlugin(){
		return plugin;
	}
	public void randomTeleport(){
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players){
			player.teleport(randomteleport.randomLocation(player.getWorld()));
			
		}
	}
	public void setParticipants(){
		Player[] players = Bukkit.getOnlinePlayers();
		List<String> participants = new ArrayList<String>();
	    for (Player player : players){
			String parname = player.getName();
			participants.add(parname);
	    }	
	    cfg = getConfig();
	    cfg.set("participants", participants);
	    saveConfig();
	}
	public void startGame(Player sender){
		cfg = getConfig();
		cfg.set("isactive", true);
		saveConfig();
		randomTeleport();
		sender.getWorld().setPVP(true);
	    World world = sender.getWorld();
	    world.setTime(0);
	    setParticipants();
	}
	public void clearList(String list){
		cfg = getConfig();
		cfg.set(list, null);
		saveConfig();
		
	}
	public void stopGame(Player sender){
		cfg = getConfig();
		cfg.set("isactive", false);
		clearList("participants");
		clearList("leftgame");
		clearList("dead");
		saveConfig();
		sender.getWorld().setPVP(false);
		
	}
	public void stopGameEndofRound(){
		cfg = getConfig();
		cfg.set("isactive", false);
		clearList("participants");
		clearList("leftgame");
		clearList("dead");
		saveConfig();
		List<World> worlds = Bukkit.getServer().getWorlds();
        for (World world : worlds){
        	world.setSpawnLocation(0, 64, 0);
        	world.setPVP(false);
        	
        }
		
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("teleport")){
			
			Location loc = getLocation(player, 0);
			player.teleport(loc.add(new Location(player.getWorld(), 0, 1, 0)));
			return true;
				
		}
		if(cmd.getName().equalsIgnoreCase("startgame")){
			if(player.isOp()){
				if(!getConfig().getBoolean("isactive")){
					startGame(player);
					Bukkit.getServer().broadcastMessage("The Hunger Games have Begun!");
					return true;
				}
				else {
					player.sendMessage("Game is already started!");
				}


			}
			else {
				player.sendMessage("You must be an OP to start the game!");
				return true;
			}
		}
		if(cmd.getName().equalsIgnoreCase("stopgame")){
			if(player.isOp()){
				if(getConfig().getBoolean("isactive")){
					stopGame(player);
					player.sendMessage("Game has been stopped");
					return true;
				}
				else {
					player.sendMessage("Game is already stopped!");
				}

			}
			else {
				player.sendMessage("You must be an OP to start the game!");
				return true;
			}
		}
		return false;
	}

}
