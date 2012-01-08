package Fishrock123.DecoyBlocks;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class DBPlayerListener extends PlayerListener {
	public static DecoyBlocks m;
	private static DBDatabase database;
	public DBPlayerListener(DecoyBlocks instance) {
		m = instance;
		database = m.database;
	}
	
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if (!e.isCancelled()
				&& p.hasPermission("decoyblocks.decoy")
				&& e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (p.getItemInHand().getTypeId() == 280) {
				if (database.decoyLocations.contains(e.getClickedBlock().getLocation())) {
					database.remove(e.getClickedBlock());
					p.sendMessage("Removed " + e.getClickedBlock().toString() + " from the decoyList.");
				} else {
					database.add(e.getClickedBlock());
					p.sendMessage("Added " + e.getClickedBlock().toString() + " to the decoyList.");
				}
			}
		}
		if (database.logCounter.containsKey(p.getName())
				&& database.logCounter.get(p.getName()) >= 5) {
			e.setCancelled(true);
		}
	}
}