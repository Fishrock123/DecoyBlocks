package Fishrock123.DecoyBlocks;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class DBLogEntry {
	private OfflinePlayer p;
	private DBBlock b;
	private Date time;
	
	public DBLogEntry(Player p, Block b) {
		this(Bukkit.getOfflinePlayer(p.getName()), b, new Date());
	}
	
	public DBLogEntry(OfflinePlayer p, Block b, Date d) {
		this.p = p;
		this.b = new DBBlock(b.getLocation(), b.getTypeId(), b.getData());
		this.time = d;
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return p;
	}
	
	public DBBlock getBlock() {
		return b;
	}
	
	public Date getTimestamp() {
		return time;
	}
	
	public String toString() {
		Location loc = b.getLocation();
		return p.getName() + " -: (" + loc.getWorld().getName() + ")(X:" + (long)loc.getX() + ")(Y:" + (long)loc.getY() + ")(Z:" + (long)loc.getZ() + ")" + " -: @ " + time.toString();
	}
}
