package com.fishrock123.decoyblocks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class DBBlockListener implements Listener {
	private DecoyBlocks m;
	private DBDatabase database;
	private DBProcessor processor;
	
	public DBBlockListener(DecoyBlocks instance) {
		m = instance;
		database = m.database;
		processor = m.processor;
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent e) {
		if (database.decoyLocations.containsKey(e.getBlock().getLocation()) && e.getPlayer().hasPermission("decoyblocks.decoy")) {
			database.remove(e.getBlock());
			e.getPlayer().sendMessage("Removed " + new DBBlock(e.getBlock()).toString() + " from the decoyList.");
			return;
		}
		
		String name = e.getPlayer().getName();
		
		if (database.decoyLocations.containsKey(e.getBlock().getLocation())) {
			DBLogEntry entry = new DBLogEntry(e.getPlayer(), e.getBlock());
			database.Log.add(entry);
			Byte breakcount = 0;
			
            if (database.logCounter.containsKey(entry.getOfflinePlayer().getName())) {
            	breakcount = database.logCounter.get(entry.getOfflinePlayer().getName());
            	
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
			processor.execute(e.getPlayer(), e.getBlock().getLocation().getWorld().getName(), breakcount);
			e.getBlock().setTypeId(0);
			if (m.AutoRestore == true) {
				database.decoyLocations.get(e.getBlock().getLocation()).autoRestore(m, m.AutoRestoreTime);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		
		if (p.getItemInHand().getTypeId() == 17
				&& m.logset.contains(p)) {
			int i = 0;
			
			for (DBLogEntry entry : database.Log) {
				if (entry.getBlock().getLocation().equals(e.getBlock().getLocation())) {
					i++;
					p.sendMessage('(' + i + ") This decoy has logged " + entry.getOfflinePlayer().getName() + " at " + entry.getTimestamp());
				}
			}
			
			e.setCancelled(true);
		}
	}
}
