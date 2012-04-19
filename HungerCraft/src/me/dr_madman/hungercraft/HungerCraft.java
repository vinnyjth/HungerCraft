package me.dr_madman.hungercraft;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;



import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.dr_madman.hungercraft.KitHandler;

public class HungerCraft extends JavaPlugin implements CommandExecutor{
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger("Minecraft");
	public static FileConfiguration cfg;
	public static HungerCraft plugin;
	public static PluginDescriptionFile plugdes;
	public static CompassTracker compasstracker = new CompassTracker();
	public HungerListener hungerlistener = new HungerListener(this);
	public RandomTeleport randomteleport = new RandomTeleport(this);
	public WorldManagement worldmanagement = new WorldManagement(this);
	public KitHandler kithandler = new KitHandler(this);
	public CompassTrackerUpdater compasstrackerupdater = new CompassTrackerUpdater(this);
	private int id;
	public int tostart = 0;
	public int voted = 0;
	public boolean countdownactive = false;
	public String prefix = "[HC] " + ChatColor.ITALIC + "" + ChatColor.GREEN;
	public HashMap<Player, String> chosenkit = new HashMap<Player, String>();
	public HashMap<Player, String> chosenkitvip = new HashMap<Player, String>();
	private HashMap<Player, Boolean> hasvoted = new HashMap<Player, Boolean>();
	public HashMap<Player, Boolean> isgm = new HashMap<Player, Boolean>();
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
        getServer().getPluginManager().registerEvents(compasstracker, this);
        checkFolder();
        checkVIPFolder();
        cfg = getConfig();
        cfg.addDefault("isactive", false);
        cfg.addDefault("option.usespawnpoints", false);
        cfg.addDefault("option.spawnradius", 30);
        cfg.addDefault("option.arenasize", 500);
        cfg.addDefault("option.borderkill", true);
        cfg.addDefault("option.joinmessage", "Interested in VIP or Gamemaker? Type /buy");
        cfg.addDefault("participants", null);
        cfg.addDefault("leftgame", null);
        cfg.addDefault("dead", null);
        cfg.addDefault("world", "world");
        cfg.addDefault("motd", "Game inactive");
        cfg.addDefault("option.timebetweenrounds", 300);
        cfg.addDefault("setup", false);
        cfg.addDefault("option.worldname", "arena1");
        cfg.addDefault("VIP", null);
        cfg.addDefault("option.owner", "Dr_MadMan");
        tostart = 0;
        voted = 0;
        hasvoted.clear();
        try {
			KitHandler.initKits();
			KitHandler.initVIP();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        getConfig().options().copyDefaults(true);
        saveConfig();
        List<World> worlds = Bukkit.getServer().getWorlds();
        for (World world : worlds){
        	world.setPVP(false);
        	
        }
        if(!checkForWorld(getConfig().getString("option.worldname"))){
        	generateWorld();
        }
        stopGameEndofRound();
        
        
        
	}
	@Override
	public void onDisable() {
		System.out.println(plugdes.getName() + " is now disabled.");
		CompassTrackerUpdater.stop();
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
	/**public HashMap getKits(){
		HashMap<String, List<ItemStack>> kits = new HashMap<String, List<ItemStack>>();
		List<ItemStack> kitlist = getConfig().getConfigurationSection("kits.").getKeys(false)
		for (String key : getConfig().getConfigurationSection("kits.").getKeys(false)){
		    kits.put(key, getConfig().getInt("kits." + key));
		}
		return kits;
	}
	*/
	public void checkFolder(){
		File dir1 = getDataFolder();
		File dir = new File(dir1, "Classes");
		boolean exists = dir.exists();
		if(!exists){
			dir.mkdir();
		}
	}
	public void checkVIPFolder(){
		File dir1 = getDataFolder();
		File dir = new File(dir1, "VIP");
		boolean exists = dir.exists();
		if(!exists){
			dir.mkdir();
		}
	}
	
	public void generateWorld(){
		String worldname = getConfig().getString("option.worldname");
		String worldnameraw = worldname.substring(0, worldname.length()-1);
		int i = Integer.parseInt(worldname.substring(worldname.length()-1, worldname.length()));
		i++;
		String newworldname = worldnameraw + i;
		cfg.set("option.worldname", newworldname);
		saveConfig();
		final WorldCreator newworld = new WorldCreator(newworldname);
		Random randomGenerator = new Random();
		Long seed = randomGenerator.nextLong();
		newworld.type(WorldType.NORMAL);
		newworld.environment(Environment.NORMAL);
		newworld.generateStructures(true);
		newworld.seed(seed);
		Bukkit.getServer().createWorld(newworld);
		Bukkit.getServer().broadcastMessage("World created!");
		
	}
	
	/**public void initKits(){
		HashMap<String, List<ItemStack>> classes = new HashMap<String, List<ItemStack>>();
		List<ItemStack> archeritems = Arrays.asList(new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 16), new ItemStack(Material.COMPASS, 1));
	    classes.put("archer", archeritems);
	    List<ItemStack> mineritems = Arrays.asList(new ItemStack(Material.STONE_PICKAXE, 1), new ItemStack(Material.COMPASS, 1));
	    classes.put("miner", mineritems);
	    List<ItemStack> bakeritems = Arrays.asList(new ItemStack(Material.BREAD, 4), new ItemStack(Material.COMPASS, 1));
	    classes.put("baker", bakeritems);
	    

	} 
	
	public void saveKit(HashMap<String, List<ItemStack>> classes){
		for (Map.Entry<String, List<ItemStack>> entry : classes.entrySet())
	    {
	        getConfig().set("kits." + entry.getKey(), entry.getValue());
	        saveConfig();

	    }
	}*/
	
	public static JavaPlugin getPlugin(){
		return plugin;
	}
	public void randomTeleport(){
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players){
			player.teleport(getServer().getWorld(getConfig().getString("option.worldname")).getSpawnLocation().add(new Vector(0,64,0)));
			
		}
	}
	public void countdown(final Player player){
		if (countdownactive == true){
			return;	
		}
		countdownactive = true;
		this.id = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			int timebetweenrounds = cfg.getInt("option.timebetweenrounds")+10;


		    public void run() {
		    	if(Bukkit.getServer().getOnlinePlayers().length <= 1){
		    		Bukkit.getServer().broadcastMessage(prefix +  "Too few players to start the countdown:(");
		    		stopGameEndofRound();
		    		Bukkit.getScheduler().cancelTask(id);
		    		return;
		    	}
		    	if(getConfig().getBoolean("isactive")){
		    		Bukkit.getScheduler().cancelTask(id);
		    	}
		    	else {
		    		timebetweenrounds = timebetweenrounds - 10;
		    		Bukkit.getServer().broadcastMessage(prefix + "Round starts in " + timebetweenrounds + " seconds");
		    		cfg.set("motd", "Round starts in " + timebetweenrounds + " seconds");
		    		saveConfig();
		    		if (timebetweenrounds < 0){
			    		Bukkit.getScheduler().cancelTask(id);
			    	}
		    		if (timebetweenrounds == 0){
		    			countdownactive = false;
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
			player.setFoodLevel(20);
			Inventory inv = player.getInventory();
			inv.clear();
			player.setGameMode(GameMode.SURVIVAL);
			((PlayerInventory) inv).setBoots(null);
			((PlayerInventory) inv).setLeggings(null);
			((PlayerInventory) inv).setChestplate(null);
			((PlayerInventory) inv).setHelmet(null);
			show(player);
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 10));
			
	    }	
	    cfg = getConfig();
	    cfg.set("participants", participants);
	    saveConfig();
	}
	public void startGame(Player sender){
		if(Bukkit.getServer().getOnlinePlayers().length < 2){
			Bukkit.getServer().broadcastMessage(prefix + "Too few players to start :(");
			return;
		}
		cfg = getConfig();
		cfg.set("isactive", true);
		voted = 0;
		tostart = 0;
		hasvoted.clear();
		saveConfig();
		randomTeleport();
		sender.getWorld().setPVP(true);
	    World world = sender.getWorld();
	    world.setTime(0);
	    setParticipants();
	    Bukkit.getServer().broadcastMessage(prefix + "The Hunger Games have Begun!");
	    brodcastInvincbility();
	    setMOTD("Game in progress");
	    for(Player player : Bukkit.getServer().getOnlinePlayers()){
	    	if (chosenkit.containsKey(player)){
	    		String i = chosenkit.get(player);
	    		try {
					KitHandler.getKit(player, i);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	if (chosenkitvip.containsKey(player)){
	    		String i = chosenkitvip.get(player);
	    		try {
					KitHandler.getKitVIP(player, i);
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
	public void brodcastInvincbility(){
		this.id = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			int times = 60;

		    public void run() {
		    	Bukkit.getServer().broadcastMessage(prefix + times + " Seconds remain of invincibility");
		    	times = times -10;
		    	if (times == -10){
		    		Bukkit.getServer().broadcastMessage(prefix + "Invincibilty has worn off!");
		    		Bukkit.getScheduler().cancelTask(id);
		    	}
		    	

		    }
		}, 1L, 200L);
	}
	
	public boolean checkForWater(World world){
		Location spawn = world.getSpawnLocation();
		int x = spawn.getBlockX();
		int y = spawn.getBlockY();
		Biome spawnbiome = world.getBiome(x, y);
		if (spawnbiome == Biome.OCEAN){
			return false;
		}
		else{
			return true;
		}
		
	}
	
    /**public void regenChunks(World world){
    	int borderSize = cfg.getInt("option.arenasize");
    	borderSize = (borderSize/16) + 10;
    	for(int x = -borderSize; x != borderSize; x++){
    		Bukkit.getServer().broadcastMessage(x + "x");
    		for(int y = -borderSize; y != borderSize; y++){
    			Chunk chunk = world.getChunkAt(x, y);
    			if(chunk.isLoaded()){
        	    	world.regenerateChunk(x, y);
    			}

    		}
    		
    	}
	}
	*/
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
		cfg.set("setup", false);
		cfg.set("isactive", false);
		cfg.set("motd", "Game has not started yet");
		clearList("participants");
		clearList("leftgame");
		clearList("dead");
		saveConfig();
		
	}
	/**public void addKit(String name, String item, int amount){
		HashMap<String, List<ItemStack>> classes = new HashMap<String, List<ItemStack>>();
		ItemStack itemstack = new ItemStack(Material.getMaterial(item.toUpperCase()), amount);
		List<ItemStack> list = Arrays.asList(itemstack);
		list.add(itemstack);
		classes.put(name,list);
		saveKit(classes);
	}
	*/
	public List<String> getPars(){
		List<String> list = getConfig().getStringList("participants");
		return list;
	}
	
	public void setMOTD(String message){
		cfg.set("motd", message);
		saveConfig();
	}
	public void vanish(Player player){
		player.sendMessage(prefix + "You are now hidden");
		for (Player hidefrom :Bukkit.getServer().getOnlinePlayers()){
			hidefrom.hidePlayer(player);
		}
	}
	public void show(Player player){
		for (Player hidefrom :Bukkit.getServer().getOnlinePlayers()){
			hidefrom.showPlayer(player);
		}
	}
	public void setGM(Player player){
		vanish(player);
		player.setGameMode(GameMode.CREATIVE);
		List<String> list = getPars();
		String pname = player.getName();
		list.remove(pname);
		getConfig().set("participants", list);
		getConfig().set("dead", getConfig().getStringList("dead").add(player.getName()));
		saveConfig();
	}
	public void disableGM(Player player){
		show(player);
		player.setGameMode(GameMode.SURVIVAL);
		getConfig().set("participants", getConfig().getStringList("participants").add(player.getName()));
		getConfig().set("dead", getConfig().getStringList("dead").remove(player.getName()));
		saveConfig();
	}
	public void checkVotes(Player player){
		if(voted == tostart){
			getServer().broadcastMessage(prefix + "Enough votes have been recieved! Game is starting!");
			startGame(player);
		}
		else{
			int votesleft = tostart-voted;
			Bukkit.getServer().broadcastMessage(prefix + votesleft + " votes are needed to start the game");
		}
	}
	
		

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("teleport")){
			
			player.teleport(getServer().getWorld(getConfig().getString("worldname")).getSpawnLocation());
				
		}
		if(cmd.getName().equalsIgnoreCase("startgame")){
			if(player.isOp()){
				if(!getConfig().getBoolean("isactive")){
					countdown(player);
					return true;
				}
				else {
					player.sendMessage(prefix + "Game is already started!");
				}


			}
			else {
				player.sendMessage(prefix + "You must be an OP to start the game!");
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
					player.sendMessage(prefix + "Game is already started!");
				}


			}
			else {
				player.sendMessage(prefix + "You must be an OP to start the game!");
				return true;
			}
		}
		if(cmd.getName().equalsIgnoreCase("stopgame")){
			if(player.isOp()){
				if(getConfig().getBoolean("isactive")){
					stopGameEndofRound();
					player.sendMessage(prefix + "Game has been stopped");
					return true;
				}
				else {
					player.sendMessage(prefix + "Game is already stopped!");
				}

			}
			else {
				player.sendMessage(prefix + "You must be an OP to start the game!");
				return true;
			}
		}
		if(cmd.getName().equalsIgnoreCase("setborder")){
			if(args[0] == null){
				return false;
			}
			int bordersize = Integer.parseInt(args[0]);
			getConfig().set("option.arenasize", bordersize);
			saveConfig();
			reloadConfig();
			player.sendMessage("Border has been set");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("kit")){
			if(args.length == 0){
				sender.sendMessage(prefix +"These are the kits " + KitHandler.listKits());
				return true;
			}
			if(args[0] == "list"){
				player.sendMessage(prefix +"These are the kits " + KitHandler.listKits());
				return true;
			}
			if (getConfig().getBoolean("isactive")){
				player.sendMessage(prefix +"Game is in progress!");
				return true;
			}
			if(KitHandler.listKits().contains(args[0])){
				try {
					chosenkit.remove(player);
					chosenkit.put(player, args[0]);
					player.sendMessage(prefix + args[0]+ " chosen");
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					player.sendMessage(prefix + args[0] + "was invalid!");
					return false;
				}
			}
			else {
				player.sendMessage(prefix + "These are the kits" + KitHandler.listKits());
				return true;
			}
			
		}
		if (cmd.getName().equalsIgnoreCase("addkit")){
			if(!player.isOp()){
				player.sendMessage(prefix + "You must be Op to add a kit");
				return true;
			}
			else {
				player.sendMessage(Integer.parseInt(args[2]) + "");
				try {
					KitHandler.addKit(args[0], args[1], Integer.parseInt(args[2]));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				player.sendMessage(args[0] + " was added");
				return true;

			}
		}
		if (cmd.getName().equalsIgnoreCase("additem")){
			if(!player.isOp()){
				player.sendMessage(prefix + "You must be Op to add a kit");
				return true;
			}
			else {
				try {
					KitHandler.addKitItem(args[0], args[1], Integer.parseInt(args[2]));
					player.sendMessage(args[0] + " items were added");
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		}
		if (cmd.getName().equalsIgnoreCase("setgm")){
			if(!player.isOp()){
				player.sendMessage(prefix + "You must be Op to set a player as gamemaker");
				return true;
			}
			else {
				setGM(getServer().getPlayer(args[0]));
				player.sendMessage(args[0] + "is now a GM");
				return true;
			}
		}
		if (cmd.getName().equalsIgnoreCase("votestart")){
			if(getServer().getOnlinePlayers().length <= 1){
				player.sendMessage(prefix + "You can't vote to start, not enough players");
				return true;
			}
			if(hasvoted.containsKey(player)){
				if(hasvoted.get(player) == true){
					player.sendMessage(prefix + "You have already voted!");
					return false;
				}
			}
			player.sendMessage(prefix + "You have voted to start");
			voted++;
			checkVotes(player);	
			hasvoted.put(player, true);
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("itemlist")){
			if(args.length == 0){
				return false;
			}
			else{
				try {
					KitHandler.getKit(player, args[0]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (cmd.getName().equalsIgnoreCase("addvip")){
			if(!player.isOp()){
				player.sendMessage(prefix + "You must be Op to add a VIP");
				return true;
			}
			if(args.length == 0){
				return false;
			}
			else {
				List<String> vips = getConfig().getStringList("VIP");
				Player vip = getServer().getOfflinePlayer(args[0]).getPlayer();
				vips.add(vip.getName());
				saveConfig();
				if(vip.isOnline()){
					vip.sendMessage(prefix + args[0] + " You are now a VIP! Use /vipkit to pick a vipkit");
				}
				player.sendMessage(prefix  + " You have set " + args[0] + " as a vip");
				return true;
			}
		}
		if(cmd.getName().equalsIgnoreCase("info")){
			player.sendMessage(prefix + "++++++++++++ HungerCraft INFO ++++++++++++" );
			player.sendMessage(prefix + getConfig().getStringList("participants") + " are the players left in the game");
			player.sendMessage(prefix + "Plugin created by Dr_MadMan");
			player.sendMessage(prefix + "This server is owned by " + getConfig().getString("option.owner"));
			player.sendMessage(prefix + "Bugs? Report to @Dr_MadMan on twitter");
			
		}
		if(cmd.getName().equalsIgnoreCase("gm")){
			if(player.hasPermission("hc.gm") || player.isOp()){
				if(isgm.containsKey(player)){
					if(isgm.get(player)){
						disableGM(player);
						player.sendMessage(prefix + "You are no longer GM");
						isgm.put(player, false);
						return true;
					}
					else{
						setGM(player);
						isgm.put(player, true);
						player.sendMessage(prefix + " You are now a GM");
					}
				}
				else{
					setGM(player);
					isgm.put(player, true);
					player.sendMessage(prefix + " You are now a GM");
				}

			}
		}

		//+++++++++++++++++++++++++++++++++++VIP+++++++++++++++++++++++++++++++++++++++
		if (cmd.getName().equalsIgnoreCase("vipkit")){
			if(args.length == 0){
				sender.sendMessage(prefix +"These are the kits " + KitHandler.listVipKits());
				return true;
			}
			if(args[0] == "list"){
				player.sendMessage(prefix +"These are the kits " + KitHandler.listVipKits());
				return true;
			}
			if (getConfig().getBoolean("isactive")){
				player.sendMessage(prefix +"Game is in progress!");
				return true;
			}
			if(getConfig().getStringList("VIP").contains(player.getName()) || player.hasPermission("hc.vip")){
				if(KitHandler.listVipKits().contains(args[0])){
					try {
						chosenkitvip.remove(player);
						chosenkitvip.put(player, args[0]);
						player.sendMessage(prefix + args[0]+ " chosen");
						return true;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						player.sendMessage(prefix + args[0] + "was invalid!");
						return false;
					}
				}
				else {
					player.sendMessage(prefix + "These are the kits" + KitHandler.listVipKits());
					return true;
				}
			
			}
		}
		if (cmd.getName().equalsIgnoreCase("addvipkit")){
			if(!player.isOp()){
				player.sendMessage(prefix + "You must be Op to add a kit");
				return true;
			}
			else {
				player.sendMessage(Integer.parseInt(args[2]) + "");
				try {
					KitHandler.addVIPKit(args[0], args[1], Integer.parseInt(args[2]));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				player.sendMessage(args[0] + " was added");
				return true;

			}
		}
		if (cmd.getName().equalsIgnoreCase("addvipitem")){
			if(!player.isOp()){
				player.sendMessage(prefix + "You must be Op to add a kit");
				return true;
			}
			else {
				try {
					KitHandler.addVIPKitItem(args[0], args[1], Integer.parseInt(args[2]));
					player.sendMessage(args[0] + " items were added");
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		}
			
		return false;
	}

}
