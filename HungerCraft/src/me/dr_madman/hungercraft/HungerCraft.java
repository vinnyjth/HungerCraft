package me.dr_madman.hungercraft;

import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class HungerCraft extends JavaPlugin implements CommandExecutor{
	Logger logger;
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
        saveConfig();
        
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
	
	public void startGame(){
		cfg = getConfig();
		cfg.set("isactive", true);
		saveConfig();
		randomTeleport();
		
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("teleport")){
			
			Location loc = randomteleport.randomLocation(player.getWorld());
			player.teleport(loc);
			return true;
				
		}
		if(cmd.equals("start")){
			if(player.isOp()){
				startGame();

			}
			else {
				player.sendMessage("You must be an OP to start the game!");
			}
		}
		return false;
	}

}
