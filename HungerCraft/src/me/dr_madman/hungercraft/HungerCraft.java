package me.dr_madman.hungercraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HungerCraft extends JavaPlugin implements CommandExecutor{
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger("Minecraft");
	public static FileConfiguration cfg;
	public static HungerCraft plugin;
	public static PluginDescriptionFile plugdes;
	public HungerListener hungerlistener = new HungerListener(this);
	public RandomTeleport randomteleport = new RandomTeleport(this);
	private int id;
	public HashMap<Player, String> chosenkit = new HashMap<Player, String>();
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
        cfg.addDefault("arenasize", 3000);
        cfg.addDefault("borderkill", true);
        cfg.addDefault("participants", null);
        cfg.addDefault("leftgame", null);
        cfg.addDefault("dead", null);
        cfg.addDefault("world", "world");
        cfg.addDefault("motd", "Game inactive");
        cfg.addDefault("timebetweenrounds", 300);
        try {
			initKits();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        getConfig().options().copyDefaults(true);
        saveConfig();
        stopGameEndofRound();
        List<World> worlds = Bukkit.getServer().getWorlds();
        for (World world : worlds){
        	world.setSpawnLocation(0, 64, 0);
        	world.setPVP(false);
        	
        }
        if(!checkForWorld("world2")){
        	generateWorld();
        }
        
        
        
	}
	@Override
	public void onDisable() {
		System.out.println(plugdes.getName() + " is now disabled.");
    }
	public boolean checkForWorld(String world){
		List<World> worlds = getServer().getWorlds();
		World worldname = getServer().getWorld(world);
		if (worlds.contains(worldname)){
			return true;
		}
		else{
			return false;
		}
		
	}
	public void generateWorld(){
		WorldCreator newworld = new WorldCreator("world2");
		Random randomGenerator = new Random();
		Long seed = randomGenerator.nextLong();
		newworld.copy(getServer().getWorld("world"));
		newworld.seed(seed);
		getServer().createWorld(newworld);
	}
	public static void save(Object obj,String path) throws Exception
	{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
	public List<String> listKits(){
		File dir1 = getDataFolder();
		File dir = new File(dir1, "Classes");

		String[] children = dir.list();
		List<String> classes = new ArrayList<String>();
		for (String i : children){
			String j = i.substring(0, i.length()-4);
			classes.add(j);
		}
		return classes;
	}
	public static Object load(String path) throws Exception
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
		Object result = ois.readObject();
		ois.close();
		return result;
	}
	public void initKits() throws Exception{
		File data = new File(getDataFolder(), "Classes");
		HashMap<Integer, Integer> Archer = new HashMap<Integer, Integer>();
	    Archer.put( 262, 16);
	    Archer.put( 261, 1);
	    Archer.put( 345, 1);
	    save(Archer, data + "//archer.txt");
	    HashMap<Integer, Integer> Baker = new HashMap<Integer, Integer>();
	    Baker.put( 297, 4);
	    Baker.put( 345, 1);
	    save(Baker, data + "//baker.txt");
	    HashMap<Integer, Integer> Miner = new HashMap<Integer, Integer>();
	    Miner.put( 274, 1);
	    Miner.put( 345, 1);
	    save(Miner, data + "//miner.txt");
		
	}
	
	public static JavaPlugin getPlugin(){
		return plugin;
	}
	public void randomTeleport(){
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players){
			Bukkit.getServer().broadcastMessage("Teleporting players");
			player.teleport(randomteleport.randomLocation(getServer().getWorld("world2")));
			
		}
	}
	public void countdown(final Player player){
		this.id = getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			int timebetweenrounds = cfg.getInt("timebetweenrounds")+10;

		    public void run() {
		    	if(Bukkit.getServer().getOnlinePlayers().length == 1){
		    		Bukkit.getScheduler().cancelTask(id);
		    		Bukkit.getServer().broadcastMessage("Too few players to start :(");
		    		stopGameEndofRound();
		    	}
		    	if(getConfig().getBoolean("isactive")){
		    		Bukkit.getScheduler().cancelTask(id);
		    	}
		    	else {
		    		timebetweenrounds = timebetweenrounds - 10;
		    		Bukkit.getServer().broadcastMessage("Round starts in " + timebetweenrounds + " seconds");
		    		cfg.set("motd", "Round starts in " + timebetweenrounds + " seconds");
		    		saveConfig();
		    		if (timebetweenrounds == 0){
			    		startGame(player);
			    		Bukkit.getScheduler().cancelTask(id);
			    	}
		    	}
		    	
		    	

		    }
		}, 1L, 200L);
	}
	public void setParticipants(){
		Player[] players = Bukkit.getOnlinePlayers();
		List<String> participants = new ArrayList<String>();
	    for (Player player : players){
			String parname = player.getName();
			participants.add(parname);
			player.setHealth(20);
			player.getInventory().clear();
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 10));
			
	    }	
	    cfg = getConfig();
	    cfg.set("participants", participants);
	    saveConfig();
	}
	public void startGame(Player sender){
		if(Bukkit.getServer().getOnlinePlayers().length == 1){
			Bukkit.getServer().broadcastMessage("Too few players to start :(");
		}
		cfg = getConfig();
		cfg.set("isactive", true);
		saveConfig();
		randomTeleport();
		sender.getWorld().setPVP(true);
	    World world = sender.getWorld();
	    world.setTime(0);
	    setParticipants();
	    Bukkit.getServer().broadcastMessage("The Hunger Games have Begun!");
	    setMOTD("Game in progress");
	    for(Player player : Bukkit.getServer().getOnlinePlayers()){
	    	if (chosenkit.containsKey(player)){
	    		String i = chosenkit.get(player);
	    		try {
					getKit(player, i);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	
	    }
	}
	public void clearList(String list){
		cfg = getConfig();
		cfg.set(list, null);
		saveConfig();
		
	}
	public void getKit(Player player, String kit) throws Exception{
		String kitlower = kit.toLowerCase();
		File data = new File(getDataFolder(), "Classes//");
		@SuppressWarnings("unchecked")
		HashMap<Integer, Integer> list = (HashMap<Integer, Integer>) load(data + kitlower + ".txt");
		for(Integer i : list.keySet()){
			Integer j = list.get(i);
			player.sendMessage("adding" + i.toString() + j.toString());
			player.getInventory().addItem(new ItemStack(i, j));
			
		}
		
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
		cfg.set("motd", "Game has not started yet");
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
	public void addKit(String name, Integer item, Integer amount) throws Exception{
		HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
		list.put(item, amount);
		save(list, name + ".txt");
		
	}
	public void addKitItem(String name, int item, int amount){
		
	}
	public void setMOTD(String message){
		cfg.set("motd", message);
		saveConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("teleport")){
			
			player.teleport(getServer().getWorld("world2").getSpawnLocation());
				
		}
		if(cmd.getName().equalsIgnoreCase("startgame")){
			if(player.isOp()){
				if(!getConfig().getBoolean("isactive")){
					countdown(player);
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
		if(cmd.getName().equalsIgnoreCase("forcestart")){
			if(player.isOp()){
				if(!getConfig().getBoolean("isactive")){
					startGame(player);
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
					stopGameEndofRound();
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
		if(cmd.getName().equalsIgnoreCase("setborder")){
			if(args[0] == null){
				return false;
			}
			int bordersize = Integer.parseInt(args[0]);
			getConfig().set("arenasize", bordersize);
			saveConfig();
			reloadConfig();
			player.sendMessage("Border has been set");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("kit")){
			if(args[0] == null){
				player.sendMessage("You need to include a kit");
				return true;
					
			}
			if (getConfig().getBoolean("isactive")){
				player.sendMessage("Game is in progress!");
				return true;
			}
			
			if(args[0] == "list"){
				player.sendMessage("These are the kits" + listKits());
				return true;
			}
			
			
			
			if(listKits().contains(args[0])){
				try {
					chosenkit.put(player, args[0]);
					player.sendMessage(args[0]+ "chosen");
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					player.sendMessage(args[0] + "was invalid!");
					return false;
				}
			}
			else {
				player.sendMessage(args[0] + " is not a kit");
				player.sendMessage("These are the kits" + listKits());
				return true;
			}
			
		}
		if (cmd.getName().equalsIgnoreCase("addkit")){
			if(!player.isOp()){
				player.sendMessage("You must be Op to add a kit");
				return true;
			}
			else{
				if ((args[0] instanceof String) || args[0] == null || Integer.getInteger(args[1]) instanceof Integer || args[1] == null || args[2] == null){
					player.sendMessage("You need to insert the right commands!");
				}
				else {
					try {
						addKit(args[0], Integer.getInteger(args[1]), Integer.getInteger(args[2]));
						player.sendMessage(args[0] + " was added");
						return true;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}
			}
		}
			
		return false;
	}

	public void log(String string) {
		// TODO Auto-generated method stub
		
	}

}
