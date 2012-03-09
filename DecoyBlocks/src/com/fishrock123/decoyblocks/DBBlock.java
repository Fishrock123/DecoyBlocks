package com.fishrock123.decoyblocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class DBBlock {
	private Location loc;
	private int id;
	private byte data;
	
	public DBBlock(Block b) {
		this(b.getLocation(), b.getTypeId(), b.getData());
	}
	
	public DBBlock(Location loc, int id, byte data) {
		this.loc = loc;
		this.id = id;
		this.data = data;
	}
	
	public String getWorldName() {
		return loc.getWorld().getName();
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public int getTypeId() {
		return id;
	}
	
	public byte getData() {
		return data;
	}
	
	public String toString() {
		return Material.getMaterial(id) + ":(" + data + ") -: (`" + loc.getWorld().getName() + "`)(X:" + (long)loc.getX() + ")(Y:" + (long)loc.getY() + ")(Z:" + (long)loc.getZ() + ')';
	}
	
	public void autoRestore(final Plugin plugin, long seconds) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { 
        	public void run() {
				Block rb = loc.getBlock();
				if (rb.getTypeId() != id || rb.getData() != data) {
					rb.setTypeIdAndData(id, data, true);
					if (rb.isEmpty() || rb.getTypeId() != id || rb.getData() != data) {
						plugin.getLogger().info("DB ERROR: Failed to restore a decoy in `" + loc.getWorld().getName() + "`!");
					}
				}
        	}
		}, 20L * seconds);
	}
}
