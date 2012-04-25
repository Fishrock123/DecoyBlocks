package com.fishrock123.decoyblocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class DBCommands {
	private DecoyBlocks m;
	private DBDatabase database;
	
	public DBCommands(DecoyBlocks instance) {
		m = instance;
		database = m.database;
	}
	
	public boolean commandProcess(CommandSender s, Command cmd, String cLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("db")) {
			
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("decoys")
						&& s.hasPermission("decoyblocks.decoy")) {
					s.sendMessage("DB: Listing all decoys:");
					int i = 0;
					for (DBBlock b : database.decoys) {
						i++;
						s.sendMessage('(' + i + ") " + b.toString());
						continue;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("log")
					&& s.hasPermission("decoyblocks.log")) {
					if (args.length == 2 
							&& args[1].equalsIgnoreCase("all")) {
						s.sendMessage("DB: Listing all log entries:");
						int i = 0;
						for (DBLogEntry entry : database.Log) {
							i++;
							s.sendMessage('(' + i + ") " + entry.toString());
							continue;
						}
					}
					else if (s instanceof Player) {
						if (m.logset.contains((Player)s)) {
							s.sendMessage("DB: Disabled Log viewer! (Log block)");
							m.logset.remove((Player)s);
						} else {
							s.sendMessage("DB: Enabled Log viewer! (Log block)");
							m.logset.add((Player)s);
						}
						return true;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("restore")
						&& s.hasPermission("decoyblocks.restore")) {
					s.sendMessage("DB: Restoring decoys...");
					int e = 0;
					int r = 0;
					for (DBBlock b : database.decoys) {
						Location loc = b.getLocation();
						Block rb = loc.getBlock();
						if (rb.getTypeId() != b.getTypeId() 
								|| rb.getData() != b.getData()) {
							rb.setTypeIdAndData(b.getTypeId(), b.getData(), true);
							if (rb.isEmpty() 
									|| rb.getTypeId() != b.getTypeId()
									|| rb.getData() != b.getData()) {
								e++;
								s.sendMessage("DB ERROR: Failed to restore a decoy in `" + loc.getWorld().getName() + "`!");
								if (!(s instanceof ConsoleCommandSender)) {
									m.l.info("DB ERROR: Failed to restore a decoy in `" + loc.getWorld().getName() + "`!");
								}
							} else {
								r++;
							}
						}
						continue;
					}
					if (e > 0) {
						s.sendMessage("DB: Failed to properly restore " + e + " decoys!");
						if (!(s instanceof ConsoleCommandSender)) {
							m.l.info("DB: Failed to properly restore " + e + " decoys!");
						}
					} else if (r == 0) {
						s.sendMessage("DB: No Decoys to restore.");
					} else {
						s.sendMessage("DB: All decoys were restored!");
						if (!(s instanceof ConsoleCommandSender)) {
							m.l.info("DB: All decoys were restored!");
						}
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("teleport")
						&& s instanceof Player
						&& s.hasPermission("decoyblocks.teleport")) {
					Location loc = null;
					if (args.length == 2) {
						for (DBLogEntry entry : database.Log) {
							if (entry.getOfflinePlayer().getName().equalsIgnoreCase(args[1])) {
								loc = entry.getBlock().getLocation();
								break;
							}
							continue;
						}
					} else {
						loc = database.Log.getLast().getBlock().getLocation();
					}
					
					try {
						Bukkit.getPlayer(s.getName()).teleport(EmptyBlockFinder.getNearestEmptySpace(loc, 5));
						return true;
					} catch (Exception e) {
						s.sendMessage(ChatColor.RED + "Error, unable to teleport to broken block.");
						m.l.info("Error, unable to teleport to broken block.");
						e.printStackTrace();
					}
					return true;
				}
				if ((args[0].equalsIgnoreCase("search") || args[0].equalsIgnoreCase("lookup"))
						&& s.hasPermission("decoyblocks.search")) {
					if (args.length == 2) {
						Player searchPlayer = null;
						try {
							searchPlayer = Bukkit.getPlayerExact(args[1]);
						} catch (Exception e) {
							return true;
						}
						List<DBLogEntry> lookupList = new ArrayList<DBLogEntry>();
						for (DBLogEntry entry : database.Log) {
							if (entry.getOfflinePlayer().getName().equals(searchPlayer.getName())) {
								lookupList.add(entry);
							}
						}
						int i = 0;
						for (DBLogEntry en : lookupList) {
							i++;
							s.sendMessage('(' + i + ") " + en.getBlock().toString() + "-: @ " + en.getTimestamp().toString());
							continue;
						}
						return true;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("clear")) {
					if (args.length == 2 
							&& args[1].equalsIgnoreCase("log")
							&& (s.hasPermission("decoyblocks.clear")
									|| s.hasPermission("decoyblocks.clear.log"))) {
						database.Log.clear();
						database.logCounter.clear();
						s.sendMessage("DB: Cleared the Log.");
						if (!(s instanceof ConsoleCommandSender)) {
							m.l.info("DB: Cleared the Log.");
						}
						return true;
					}
					if (args.length == 2 
							&& args[1].equalsIgnoreCase("decoys")
							&& s.hasPermission("decoyblocks.clear")) {
						database.decoys.clear();
						database.decoyLocations.clear();
						s.sendMessage("DB WARNING: You have cleared all decoys.");
						if (!(s instanceof ConsoleCommandSender)) {
							m.l.info("DB WARNING: " + s.getName() + " Cleared all decoys.");
						}
						return true;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("save")
						&& s.hasPermission("decoyblocks.save")) {
					final long startTime = System.nanoTime();
			        final long endTime;
					
					s.sendMessage("DB: Saving...");
					database.SaveDecoys();
					database.SaveLog();
					database.LoadDecoys();
					database.LoadLog(); 
					endTime = System.nanoTime();
					s.sendMessage("DB: Saved Decoys and Log entries. {" + TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) + " ms}");
					if (!(s instanceof ConsoleCommandSender)) {
						m.l.info("DB: Saved Decoys and Log entries. {" + TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) + " ms}");
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("reload")
						&& s.hasPermission("decoyblocks.reload")) {
					final long startTime = System.nanoTime();
			        final long endTime;
					
					s.sendMessage("DB: Saving...");
					database.deinitAutosave(m);
					m.punishments.clear();
					
					database.SaveDecoys();
					database.SaveLog();
					
					m.reloadConfig();
					
					m.initialize();
					
					endTime = System.nanoTime();
					s.sendMessage("DB: Saved Decoys and Log entries; Reloaded Config & Autosave. {" + TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) + " ms}");
					if (!(s instanceof ConsoleCommandSender)) {
						m.l.info("DB: Saved Decoys and Log entries; Reloaded Config & Autosave. {" + TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) + " ms}");
					}
					return true;
				}
				if (args.length == 2 
						&& args[1].equalsIgnoreCase("testing") 
						&& s instanceof Player 
						&& s.hasPermission("decoyblocks.test")) {
					if (args[0].equalsIgnoreCase("enable")) {
						database.testList.add((Player)s);
						s.sendMessage("The testing function of DecoyBlocks is now enabled for `" + s.getName() + '`');
						m.l.info("The testing function of DecoyBlocks is now enabled for `" + s.getName() + '`');
						return true;
					}
					if (args[0].equalsIgnoreCase("disable")) {
						database.testList.remove((Player)s);
						s.sendMessage("The testing function of DecoyBlocks is now disabled for `" + s.getName() + '`');
						m.l.info("The testing function of DecoyBlocks is now disabled for `" + s.getName() + '`');
						return true;
					}
					return true;
				}
				return true;
			}
			return true;
		}
	return false;
	}
}
