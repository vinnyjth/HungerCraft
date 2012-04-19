package me.dr_madman.hungercraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitHandler {
	private static HungerCraft plugin;
	public KitHandler(HungerCraft instance) {plugin = instance;}
	
	public static List<String> listKits(){
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
	public static void initKits() throws Exception{
		File data = new File(plugin.getDataFolder(), "Classes");
		HashMap<Integer, Integer> Archer = new HashMap<Integer, Integer>();
	    Archer.put( 262, 16);
	    Archer.put( 261, 1);
	    Archer.put( 345, 1);
	    save(Archer, data + "/archer.txt");
	    HashMap<Integer, Integer> Baker = new HashMap<Integer, Integer>();
	    Baker.put( 297, 4);
	    Baker.put( 345, 1);
	    save(Baker, data + "/baker.txt");
	    HashMap<Integer, Integer> Miner = new HashMap<Integer, Integer>();
	    Miner.put( 274, 1);
	    Miner.put( 345, 1);
	    save(Miner, data + "/miner.txt");

	}
	public static void getKit(Player player, String kit) throws Exception{
		String kitlower = kit.toLowerCase();
		File data = new File(plugin.getDataFolder(), "Classes");
		@SuppressWarnings("unchecked")
		HashMap<Integer, Integer> list = (HashMap<Integer, Integer>) load(data + "/" + kitlower + ".txt");
		for(Integer i : list.keySet()){
			Integer j = list.get(i);
			player.getInventory().addItem(new ItemStack(i, j));
			
		}
		
	}
	public static void addKit(String name, String item, Integer amount) throws Exception{
		Integer itemid = Material.getMaterial(item.toUpperCase()).getId();
		HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
		list.put(itemid, amount);
		File data = new File(plugin.getDataFolder(), "Classes");
		save(list, data + "/" + name + ".txt");

	}
	public static void addKitItem(String name, String item, Integer amount){
		File data = new File(plugin.getDataFolder(), "Classes");
		Integer itemid = Material.getMaterial(item.toUpperCase()).getId();
		try {
			@SuppressWarnings("unchecked")
			HashMap<Integer, Integer> list = (HashMap<Integer, Integer>) load(data + "/" + name + ".txt");
			list.put(itemid, amount);
			save(list,data + "/" + name + ".txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ VIP SECTION +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	public static List<String> listVipKits(){
		File dir1 = plugin.getDataFolder();
		File dir = new File(dir1, "VIP");
		String[] children = dir.list();
		List<String> classes = new ArrayList<String>();
		for (String i : children){
			String j = i.substring(0, i.length()-4);
			classes.add(j);
		}
		return classes;
	}
	public static void addVIPKitItem(String name, String item, Integer amount){
		File data = new File(plugin.getDataFolder(), "VIP");
		Integer itemid = Material.getMaterial(item.toUpperCase()).getId();
		try {
			@SuppressWarnings("unchecked")
			HashMap<Integer, Integer> list = (HashMap<Integer, Integer>) load(data + "/" + name + ".txt");
			list.put(itemid, amount);
			save(list,data + "/" + name + ".txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		public static void getKitVIP(Player player, String kit) throws Exception{
			String kitlower = kit.toLowerCase();
			File data = new File(plugin.getDataFolder(), "VIP");
			@SuppressWarnings("unchecked")
			HashMap<Integer, Integer> list = (HashMap<Integer, Integer>) load(data + "/" + kitlower + ".txt");
			for(Integer i : list.keySet()){
				Integer j = list.get(i);
				player.getInventory().addItem(new ItemStack(i, j));
				
			}
			
		}
		public static void addVIPKit(String name, String item, Integer amount) throws Exception{
			Integer itemid = Material.getMaterial(item.toUpperCase()).getId();
			HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
			list.put(itemid, amount);
			File data = new File(plugin.getDataFolder(), "VIP");
			save(list, data + "/" + name + ".txt");

		}
		public static void initVIP() throws Exception{
			File data = new File(plugin.getDataFolder(), "VIP");
			HashMap<Integer, Integer> Archer = new HashMap<Integer, Integer>();
		    Archer.put( 262, 16);
		    Archer.put( 261, 1);
		    Archer.put( 345, 1);
		    Archer.put(272, 1);
		    Archer.put(274, 1);
		    Archer.put(275, 1);
		    save(Archer, data + "/allaround.txt");
		    HashMap<Integer, Integer> Baker = new HashMap<Integer, Integer>();
		    Baker.put( 307, 1);
		    Baker.put( 345, 1);
		    Baker.put( 267, 1);
		    Baker.put( 258, 1);
		    Baker.put( 257, 1);
		    save(Baker, data + "/ironman.txt");
		    HashMap<Integer, Integer> Miner = new HashMap<Integer, Integer>();
		    Miner.put( 362, 10);
		    Miner.put( 395, 10);
		    Miner.put( 344, 10);
		    Miner.put( 366, 4);
		    Miner.put( 293, 1);
		    Miner.put( 345, 1);
		    save(Miner, data + "/farmer.txt");

		}
}
