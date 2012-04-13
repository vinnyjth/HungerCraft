package me.dr_madman.hungercraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class RandomTeleport {
	private HungerCraft plugin;
	// *------------------------------------------------------------------------------------------------------------*
	// | The random location methods contain code made by NuclearW                                                  |
	// | based on his SpawnArea plugin:                                                                             |
	// | http://forums.bukkit.org/threads/tp-spawnarea-v0-1-spawns-targetPlayers-in-a-set-area-randomly-1060.20408/ |
	// *------------------------------------------------------------------------------------------------------------*

	public RandomTeleport(HungerCraft instance) {plugin = instance;}
	public  Location randomLocation(World world){

		int xmin = -this.plugin.getConfig().getInt("spawnradius");
		int xmax = this.plugin.getConfig().getInt("spawnradius");
		int zmin = -this.plugin.getConfig().getInt("spawnradius");
		int zmax = this.plugin.getConfig().getInt("spawnradius");

		int xrand = 0;
		int zrand = 0;
		int y = -1;

		do {
			xrand = xmin + (int) ( Math.random()*(xmax - xmin) + 0.5 );
			zrand = zmin + (int) ( Math.random()*(zmax - zmin) + 0.5 );
			y = getValidHighestBlock(world, xrand,zrand);
		}while (y == -1);

		return new Location(
				world,
				Double.parseDouble(Integer.toString(xrand)) + 0.5, 
				Double.parseDouble(Integer.toString(y)),
				Double.parseDouble(Integer.toString(zrand)) + 0.5
				);
}
	public int getValidHighestBlock(World world, int x, int z) {
		world.getChunkAt(new Location(world, x, 0, z)).load();

		int y = world.getHighestBlockYAt(x, z);
		int blockid = world.getBlockTypeIdAt(x, y - 1, z);
	

		if (blockid == 8) return -1;
		if (blockid == 9) return -1;
		if (blockid == 10) return -1;
		if (blockid == 11) return -1;
		if (blockid == 51) return -1;
		if (blockid == 18) return -1;

		blockid = world.getBlockTypeIdAt(x, y + 1, z);

		if (blockid == 81) return -1;

		return y;
	}
	public void sendGround(Player player, Location location){		

		Location groundLocation = location.subtract(0, 1, 0);

		groundLocation.getChunk().load();

		if(canCauseBlockUpdate(groundLocation.getBlock())){
			player.sendBlockChange(groundLocation, Material.DIRT, (byte) 0);
		}else{
			player.sendBlockChange(groundLocation, groundLocation.getBlock().getType(), groundLocation.getBlock().getData());
		}

	}
	public boolean canCauseBlockUpdate(Block block){

		block.getChunk().load();

		int blockid = block.getTypeId();

		if (blockid == 8) return true;
		if (blockid == 9) return true;
		if (blockid == 10) return true;
		if (blockid == 11) return true;
		if (blockid == 12) return true;
		if (blockid == 13) return true;

		return false;
	}	
}
