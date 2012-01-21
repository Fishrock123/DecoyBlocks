package Fishrock123.DecoyBlocks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class DBBlockListener extends BlockListener {
	public static DecoyBlocks m;
	private static DBDatabase database;
	
	public DBBlockListener(DecoyBlocks instance) {
		m = instance;
		database = m.database;
	}
	
	public void onBlockBreak(BlockBreakEvent e) {
		String name = e.getPlayer().getName();
		
		if (database.decoyLocations.contains(e.getBlock().getLocation())) {
			DBLogEntry entry = new DBLogEntry(e.getPlayer(), e.getBlock());
			database.Log.add(entry);
			Byte breakcount = 0;
			
            if (database.logCounter.containsKey(entry.getOfflinePlayer().getName())) {
            	breakcount = database.logCounter.get(entry.getOfflinePlayer().getName());
            	database.logCounter.remove(entry.getOfflinePlayer().getName());
            	breakcount++;
            } else {
            	breakcount = 1;
            }
            
            database.logCounter.put(entry.getOfflinePlayer().getName(), breakcount);
			m.l.info("DB ALERT: (" + breakcount + ") " + name + " broke a decoy in `" + e.getBlock().getLocation().getWorld().getName() + "`!");
			
			for (World w : Bukkit.getWorlds()) {
				for (Player p : w.getPlayers()) {
					if (p.isOp() 
							|| (p.hasPermission("decoyblocks.notify") 
									|| p.hasPermission("decoyblocks.restore"))) {
						p.sendMessage("DB ALERT: (" + breakcount + ") " + name + " broke a decoy in `" + e.getBlock().getLocation().getWorld().getName() + "`!");
					}
				}
			}
			
			m.l.info("DB Debug: " + m.punishments.get(breakcount) + " + " + e.getBlock().getLocation().getWorld().getName() + " + " + e.getPlayer().getName());
			DBProcessor.execute(e.getPlayer(), e.getBlock().getLocation().getWorld().getName(), breakcount);
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		
		if (p.getItemInHand().getTypeId() == 17
				&& m.logset.contains(p)) {
			int i = 0;
			
			for (DBLogEntry entry : database.Log) {
				if (entry.getBlock().getLocation().equals(e.getBlock().getLocation())) {
					i++;
					p.sendMessage("(" + i + ") This decoy has logged " + entry.getOfflinePlayer().getName() + " at " + entry.getTimestamp());
				}
			}
			
			e.setCancelled(true);
		}
	}
}
