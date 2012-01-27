package Fishrock123.DecoyBlocks;

import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class DBCommands {
	public static DecoyBlocks m;
	private static DBDatabase database;
	
	public DBCommands(DecoyBlocks instance) {
		m = instance;
		database = m.database;
	}
	
	public boolean commandProcess(CommandSender s, Command cmd, String cLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("db")) {
			
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("decoys")
						&& s.hasPermission("decoyblocks.decoy")) {
					for (DBBlock b : database.decoys) {
						s.sendMessage(b.toString());
						continue;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("log")
					&& s.hasPermission("decoyblocks.log")) {
					if (args.length == 2 
							&& args[1].equalsIgnoreCase("all")) {
						int i = 0;
						for (DBLogEntry entry : database.Log) {
							i++;
							s.sendMessage("(" + i + ") " + entry.toString());
							continue;
						}
						if (s instanceof Player) {
							if (m.logset.contains((Player)s)) {
								m.logset.remove((Player)s);
							} else {
								m.logset.add((Player)s);
							}
							return true;
						}
						return true;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("restore")
						&& s.hasPermission("decoyblocks.restore")) {
					s.sendMessage("DB: Restoring decoys...");
					int e = 0;
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
								s.sendMessage("DB ERROR: Failed to restore a decoy in '" + loc.getWorld() + "'!");
								if (!(s instanceof ConsoleCommandSender)) {
									m.l.info("DB ERROR: Failed to restore a decoy in '" + loc.getWorld() + "'!");
								}
							}
						}
						continue;
					}
					if (e > 0) {
						s.sendMessage("Failed to properly restore " + e + " decoys!");
						if (!(s instanceof ConsoleCommandSender)) {
							m.l.info("Failed to properly restore " + e + " decoys!");
						}
					} else {
						s.sendMessage("All decoys were restored!");
						if (!(s instanceof ConsoleCommandSender)) {
							m.l.info("All decoys were restored!");
						}
					}
					return true;
				}
				/*if (args[0].equalsIgnoreCase("teleport")
						&& s instanceof Player
						&& s.hasPermission("decoyblocks.tp")) {
					if (args.length == 2) {
						for (DBLogEntry entry : database.Log) {
							if (entry.getOfflinePlayer().getName().equalsIgnoreCase(args[1])) {
								try {
									Bukkit.getPlayer(s.getName()).teleport(entry.getBlock().getLocation());
								} catch (Exception e) {
									m.l.info("Error, unable to teleport to broken block.");
								}
								break;
							}
							continue;
						}
						return true;
					}
					return true;
				}*/
				if (args[0].equalsIgnoreCase("clear")
						&& s.hasPermission("decoyblocks.clear")
						&& s.hasPermission("decoyblocks.clear.log")) {
					if (args.length == 2 
							&& args[1].equalsIgnoreCase("log")) {
						database.Log.clear();
						database.logCounter.clear();
						s.sendMessage("DB: Cleared the Log.");
						return true;
					}
					if (args.length == 2 
							&& args[1].equalsIgnoreCase("decoys")) {
						database.decoys.clear();
						database.decoyLocations.clear();
						s.sendMessage("DB: Cleared all decoys.");
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
					return true;
				}
				if (args[0].equalsIgnoreCase("reload")
						&& s.hasPermission("decoyblocks.reload")) {
					final long startTime = System.nanoTime();
			        final long endTime;
					
					s.sendMessage("DB: Saving...");
					database.deinitAutosave(m);
					
					database.SaveDecoys();
					database.SaveLog();
					
					m.reloadConfig();
					
					m.initialize();
					
					endTime = System.nanoTime();
					s.sendMessage("DB: Saved Decoys and Log entries; Reloaded Config & Autosave. {" + TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) + " ms}");
					return true;
				}
				return true;
			}
			return true;
		}
	return false;
	}
}
