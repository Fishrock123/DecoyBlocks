package com.fishrock123.decoyblocks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class DBProcessor {
	private DecoyBlocks m;
	
	public DBProcessor(DecoyBlocks instance) {
		m = instance;
	}
	
	public void execute(Player p, String wn, int i) {
		
		for (DBPunishment dbp : m.punishments) {
			
			if (i >= dbp.getTrigger()) {
				OfflinePlayer op = p;
				String name = p.getName();
				String t = dbp.getName();
				String cmd = new String();
				
				if (t.equalsIgnoreCase("Kill")) {
					p.damage(9001^3);
					p.sendMessage("You have been killed for griefing!");
					m.l.info("DB ALERT: " + name + " has been killed for breaking decoys in `" + wn + "`!");
				}
				if (t.equalsIgnoreCase("Jail")) {
					try {
						p.teleport(m.jailLoc);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					p.sendMessage("You have been jailled for griefing!");
					m.l.info("DB ALERT: " + name + " has been jailled for breaking decoys in `" + wn + "`!");
				}
				if (t.equalsIgnoreCase("Kick")) {
					p.kickPlayer("You have been kicked for greifing!");
					m.l.info("DB ALERT: " + name + " has been kicked for breaking decoys in `" + wn + "`!");
				}
				if (t.equalsIgnoreCase("Ban")) {
					p.kickPlayer("You have been kicked and banned for greifing!");
					m.l.info("DB ALERT: " + name + " has been kicked and banned for breaking decoys in `" + wn + "`!");
					op.setBanned(true);
				}
				if (t.equalsIgnoreCase("IPBan")) {
					String ip = p.getAddress().toString();
					p.kickPlayer("You have been kicked and IP banned for greifing!");
					m.l.info("DB ALERT: " + name + " has been kicked and IP banned for breaking decoys in `" + wn + "`!");
					op.setBanned(true);
					Bukkit.banIP(ip);
				}
				if (t.contains("=") && t.split("=")[0].equalsIgnoreCase("Command")) {
					try {
						cmd = t.split("=")[1];
						
						if (cmd.contains("%player%")) {
							String[] temp = cmd.split("%player%");
							cmd = temp[0] + op.getName() + temp[1];
						}
						if (cmd.contains("%world%")) {
							String[] temp = cmd.split("%world%");
							cmd = temp[0] + p.getWorld().getName() + temp[1];
						}
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
						
						m.l.info("DB ALERT: " + name + " has been punished via `" + cmd + "` for breaking decoys in `" + wn + "`!");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
				for (World w : Bukkit.getWorlds()) {
					for (Player wp : w.getPlayers()) {
						if (wp.isOp() 
								|| (wp.hasPermission("decoyblocks.notify") 
										|| wp.hasPermission("decoyblocks.restore"))) {
							if (t.equalsIgnoreCase("Kill")) {
								wp.sendMessage("DB ALERT: " + name + " has been killed for breaking decoys in `" + wn + "`!");
							}
							if (t.equalsIgnoreCase("Jail")) {
								wp.sendMessage("DB ALERT: " + name + " has been jailled for breaking decoys in `" + wn + "`!");
							}
							if (t.equalsIgnoreCase("Kick")) {
								wp.sendMessage("DB ALERT: " + name + " has been kicked for breaking decoys in `" + wn + "`!");
							}
							if (t.equalsIgnoreCase("Ban")) {
								wp.sendMessage("DB ALERT: " + name + " has been kicked and banned for breaking decoys in `" + wn + "`!");
							}
							if (t.equalsIgnoreCase("IPBan")) {
								wp.sendMessage("DB ALERT: " + name + " has been kicked and IP banned for breaking decoys in `" + wn + "`!");
							}
							if (t.contains(":") && t.split(":")[0].equalsIgnoreCase("Command")) {
								wp.sendMessage("DB ALERT: " + name + " has been punished via `" + cmd + "` for breaking decoys in `" + wn + "`!");
							}
						}
					}
				}
			}
		}
	}
}