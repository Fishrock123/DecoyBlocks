package com.fishrock123.decoyblocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class EmptyBlockFinder {
	
	/*public static Location findNearestTop(Location input) {
		boolean cont = true;
		Location loc = input.getBlock().getLocation();
		loc.setY(loc.getY() + 1);
		while (cont) {
			Location above = loc;
			above.setY(loc.getY() + 1);
			if (loc.getBlock().isEmpty() && above.getBlock().isEmpty()) {
				return loc;
			} else {
				
			}
		}
		return null;
	}*/
	
	/**
	 * @Author Njol
	 * 
	 * Source: http://forums.bukkit.org/threads/how-to-find-the-nearest-empty-block.71367/#post-1081386
	 * 
	 * @param Original location of original block
	 * @param Scanning radius
	 * @return Nearest empty space (for teleportation purposes).
	 */
	public static Location getNearestEmptySpace(Location loc, int maxradius) {
		loc.setY(loc.getY() + 1); // Fishrock123 - Shift the search area up by one block.
		BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST};
		BlockFace[][] orth = {{BlockFace.NORTH, BlockFace.EAST}, {BlockFace.UP, BlockFace.EAST}, {BlockFace.NORTH, BlockFace.UP}};
		for (int r = 0; r <= maxradius; r++) {
			for (int s = 0; s < 6; s++) {
				BlockFace f = faces[s%3];
				BlockFace[] o = orth[s%3];
				if (s >= 3) {
					f = f.getOppositeFace();
				}
				Block c = loc.getBlock().getRelative(f, r);
				for (int x = -r; x <= r; x++) {
					for (int y = -r; y <= r; y++) {
						Block a = c.getRelative(o[0], x).getRelative(o[1], y);
						if (a.getTypeId() == 0 && a.getRelative(BlockFace.UP).getTypeId() == 0) {
							return getBlockCenter(a); // Fishrock123 - get the location at the center of the block.
						}
					}
				}
			}
		}
		return getNearestEmptySpace(loc, maxradius*2); // Fishrock123 - If the cube does not have any empty blocks, search a cube twice it's size. (There will eventually be an empty space.)
		//return null; // no empty space within a cube of (2*(maxradius+1))^3
	}
	
	/**
	 * Returns the location at the center of the specified block.
	 * 
	 * @param Block
	 * @return Center location
	 */
	public static Location getBlockCenter(Block b) {
		Location loc = b.getLocation();
		loc.setX(loc.getX() + 0.5D);
		loc.setZ(loc.getZ() + 0.5D);
		return loc;
	}
}
