package me.dr_madman.hungercraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class HungerListener implements Listener {
	private HungerCraft plugin;
	private FileConfiguration cfg;
	public HungerListener(HungerCraft instance) {plugin = instance;}
	
	
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
	


        
	public boolean checkRightClickGoldBlock(Event event){
		if(((PlayerInteractEvent) event).getAction() == Action.RIGHT_CLICK_BLOCK ){
			if(((PlayerInteractEvent) event).getClickedBlock().getType() == Material.GOLD_BLOCK){
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void blockInteract(PlayerInteractEvent event) throws Exception{
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		Location blocklocation = block.getLocation();
		if(checkRightClickGoldBlock(event)){
			if(player.isOp() || event.getPlayer().hasPermission("hc.setspawn")){
				addLocation(blocklocation, 0);
				
				
			}
		}
		if(!checkActive()){
			event.setCancelled(true);
		}
		
	}
	
}
