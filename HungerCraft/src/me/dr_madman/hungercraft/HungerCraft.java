package me.dr_madman.hungercraft;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class HungerCraft extends JavaPlugin{
	Logger logger;
	public static HungerCraft plugin;
	public static PluginDescriptionFile plugdes;
	public static FileConfiguration config;
	
	public HungerListener hungerlistener = new HungerListener(this);
	
	@Override
	public void onEnable() {
		plugdes = getDescription();
		System.out.println("PLUGDESC NAME: "+plugdes.getName());
        getServer().getPluginManager().registerEvents(hungerlistener, this);
	}
	@Override
	public void onDisable() {
		System.out.println(plugdes.getName() + " is now disabled.");
    }
	public void initCommands(){
 
    }
	public static JavaPlugin getPlugin(){
		return plugin;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (cmd.equals("teleport")){
			
		}
		return false;
	}
	
	

}
