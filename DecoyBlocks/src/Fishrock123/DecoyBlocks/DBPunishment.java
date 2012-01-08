package Fishrock123.DecoyBlocks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class DBPunishment {
	public static DecoyBlocks m;
	public DBPunishment(DecoyBlocks instance) {
		m = instance;
	}
	
	public static void execute(Player p, String wn, String t) {
		OfflinePlayer op  = p;
		String name = p.getName();
		
		if (t.equalsIgnoreCase("Kill")) {
			p.damage(9001);
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
				}
			}
		}
	}
}
