package me.dr_madman.hungercraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeamHandler {
	private HungerCraft plugin;
	public TeamHandler(HungerCraft instance) {plugin = instance;}
	
	public void initTeams(){
		List<Player> listofp = playerList();
		List<Player> listofp2 = playerList();
		List<Player> reference = playerList();

		int halfofplayers = reference.size()/2;
		List<Player> sub1 = listofp.subList(0, halfofplayers);
		Collections.shuffle(sub1);

		List<Player> sub2 = listofp2.subList(halfofplayers, reference.size());
		Collections.shuffle(sub2);
		ListIterator<Player> iterator = sub2.listIterator();
		int i = 1;
		if(listofp.size() % 2 == 0){

			for(Player player : sub2){
				List<Player> teamplayer  = new ArrayList<Player>();
				teamplayer.clear();
				Player randomplayer = sub1.get((int) (Math.random() * sub1.size()));
				sub1.remove(randomplayer);
				teamplayer.add(player);
				teamplayer.add(randomplayer);
				plugin.teams.put(i, teamplayer);
				i++;
			}
		}
		else{

			List<Player> teamplayer  = new ArrayList<Player>();
			teamplayer.clear();
			Player randomplayer1 = sub1.get((int) (Math.random() * sub1.size()));
			teamplayer.add(randomplayer1);
			sub1.remove(randomplayer1);
			teamplayer.add(iterator.next());
			iterator.remove();
			teamplayer.add(iterator.next());
			iterator.remove();
			plugin.teams.put(i, teamplayer);
			i++;
			while(iterator.hasNext()){
				teamplayer.clear();
				Player randomplayer = sub1.get((int) (Math.random() * sub1.size()));
				sub1.remove(randomplayer);
				teamplayer.add(iterator.next());
				teamplayer.add(randomplayer);
				plugin.teams.put(i, teamplayer);
				i++;
			}
		}
		
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
		if(pname.length() > 10){
			pname = pname.substring(0, pname.length() - 3);
		}
		player.setPlayerListName(pname + "[" + i + "]"); 
	}
	public List<Player> playerList(){
		List<Player> listofp = new ArrayList<Player>();
		for (Player player : Bukkit.getServer().getOnlinePlayers()){
			listofp.add(player);
		}
		return listofp;
	}

}
