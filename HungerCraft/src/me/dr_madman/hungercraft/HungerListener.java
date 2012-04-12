package me.dr_madman.hungercraft;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class HungerListener implements Listener {
	public static HungerCraft plugin;
	private ArrayList<Location> spawnlocations = new ArrayList<Location>();

	public HungerListener(HungerCraft instance) {plugin = instance;}
	/** SLAPI = Saving/Loading API
	 * API for Saving and Loading Objects.
	 * @author Tomsik68
	 */
	public static  class SLAPI {
		public static void save(Object obj,String path) throws Exception
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}
		public static Object load(String path) throws Exception
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
			Object result = ois.readObject();
			ois.close();
			return result;
		}
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
	public void blockInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if(checkRightClickGoldBlock(event)){
			if(player.isOp() || event.getPlayer().hasPermission("hc.setspawn")){
				try {
					spawnlocations = (ArrayList<Location>)SLAPI.load("location.bin");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				spawnlocations.add(block.getLocation().add(0, 1, 0));
			}
		}
	}
	
}
