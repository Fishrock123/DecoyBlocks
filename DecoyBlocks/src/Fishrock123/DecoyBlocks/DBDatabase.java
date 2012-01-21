package Fishrock123.DecoyBlocks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public class DBDatabase {
	public static DecoyBlocks m;
	public List<DBBlock> decoys = new ArrayList<DBBlock>();
	public List<Location> decoyLocations = new ArrayList<Location>();
	public List<DBLogEntry> Log = new ArrayList<DBLogEntry>();
	public Map<String, Byte> logCounter = new HashMap<String, Byte>();
	
	public DBDatabase(DecoyBlocks instance) {
		m = instance;
	}
	
	public void add(Block b) {
		decoys.add(new DBBlock(b));
		decoyLocations.add(b.getLocation());
	}
	
	public void remove(Block b) {
		decoys.remove(new DBBlock(b));
		decoyLocations.remove(b.getLocation());
	}
	
	public void SaveDecoys() {
		File savefile = new File("plugins" + File.separator + "DecoyBlocks" + File.separator + "DBDecoys.dat");
		
		if (!savefile.exists()) {
			try {
				new File("plugins" + File.separator + "DecoyBlocks").mkdir();
				savefile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        try {
            FileOutputStream fstream = new FileOutputStream("plugins" + File.separator + "DecoyBlocks" + File.separator + "DBDecoys.dat");
            ObjectOutputStream ostream = new ObjectOutputStream(fstream);
            
            short decoycount = 0;
            for (@SuppressWarnings("unused") DBBlock b : decoys) {
            	decoycount ++;
            }
            ostream.writeShort(decoycount);
            
            for (DBBlock b : decoys) {
            	
            	ostream.writeUTF(b.getWorldName());
            	ostream.writeDouble(b.getLocation().getX());
            	ostream.writeDouble(b.getLocation().getY());
            	ostream.writeDouble(b.getLocation().getZ());
            	byte id = 0;
            	if (b.getTypeId() > 128) {
            		id = (byte)-(b.getTypeId() - 128);
            	} else if (b.getTypeId() <= 128) {
            		id = (byte)b.getTypeId();
            	}
            	ostream.writeByte((byte)id);
            	ostream.writeByte(b.getData());
            }
            
            ostream.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	decoys.clear();
        	decoyLocations.clear();
        }
    }

    public void LoadDecoys() {
        File savefile = new File("plugins" + File.separator + "DecoyBlocks" + File.separator + "DBDecoys.dat");
        boolean exists = savefile.exists();
        
        if (exists) {
        	FileInputStream ifstream = null;
            ObjectInputStream iostream = null;
            try {
                ifstream = new FileInputStream("plugins" + File.separator + "DecoyBlocks" + File.separator + "DBDecoys.dat");
                iostream = new ObjectInputStream(ifstream);
                
                short decoycount = iostream.readShort();
                
                if (decoycount > 0) {
                	for (int i = 0; i < decoycount; i++)  {
                		
                		String name = null;
                		try {
                			name = iostream.readUTF();
                		} catch (Exception e) {
                			name = iostream.readObject().toString();
                		}
                		World w = Bukkit.getServer().getWorld(name);
                		
                		Double x = iostream.readDouble();
                		Double y = iostream.readDouble();
                		Double z = iostream.readDouble();
                		byte bid = iostream.readByte();
                		short tid = 0;
                    	if (bid < 0) {
                    		tid = (short)((-bid) + 128);
                    	} else if (bid >= 0) {
                    		tid = (short)bid;
                    	}
                		byte data = iostream.readByte();
                		decoys.add(new DBBlock(new Location(w, x, y, z), tid, data));
                	}
                }
                
            } catch (FileNotFoundException e) {
                m.l.info("DB ERROR: Could not locate DBDecoys.dat");
                e.printStackTrace();
            } catch(IOException e) {
                m.l.info("DB ERROR: I\\O error while attempting to read DBDecoys.dat");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                m.l.info( "DB ERROR: Could not read DBDecoys.dat, class not found.");
                e.printStackTrace();
            } finally {
                try {
                    ifstream.close();
                } catch (IOException e) {
                    m.l.info( "DB ERROR: Error reading DBDecoys.dat, could not close stream.");
                    e.printStackTrace();
                }
                for (DBBlock b : decoys) {
                	decoyLocations.add(b.getLocation());
                }
            }
        }
    }
    
    public void SaveLog() {
		File savefile = new File("plugins" + File.separator + "DecoyBlocks" + File.separator + "DBLog.dat");
		
		if (!savefile.exists()) {
			try {
				new File("plugins" + File.separator + "DecoyBlocks").mkdir();
				savefile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        try {
            FileOutputStream fstream = new FileOutputStream("plugins" + File.separator + "DecoyBlocks" + File.separator + "DBLog.dat");
            ObjectOutputStream ostream = new ObjectOutputStream(fstream);
            
            short entrycount = 0;
            for (@SuppressWarnings("unused") DBLogEntry entry: Log) {
            	entrycount ++;
            }
            ostream.writeShort(entrycount);
            
            for (DBLogEntry entry : Log) {
            	
            	ostream.writeUTF(entry.getOfflinePlayer().getName());
            	ostream.writeUTF(entry.getBlock().getLocation().getWorld().getName());
            	ostream.writeDouble(entry.getBlock().getLocation().getX());
            	ostream.writeDouble(entry.getBlock().getLocation().getY());
            	ostream.writeDouble(entry.getBlock().getLocation().getZ());
            	ostream.writeObject(entry.getTimestamp());
            }
            
            ostream.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	Log.clear();
        	logCounter.clear();
        }
    }

    public void LoadLog() {
        File savefile = new File("plugins" + File.separator + "DecoyBlocks" + File.separator + "DBLog.dat");
        boolean exists = savefile.exists();
        
        if (exists) {
        	FileInputStream ifstream = null;
            ObjectInputStream iostream = null;
            try {
                ifstream = new FileInputStream("plugins" + File.separator + "DecoyBlocks" + File.separator + "DBLog.dat");
                iostream = new ObjectInputStream(ifstream);
                
                short entrycount = iostream.readShort();
                for (int i = 0; i < entrycount; i++)  {
            		
            		String pname = null;
            		try {
            			pname = iostream.readUTF();
            		} catch (Exception e) {
            			pname = iostream.readObject().toString();
            		}
                	OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(pname);
                	
                	String wname = null;
            		try {
            			wname = iostream.readUTF();
            		} catch (Exception e) {
            			wname = iostream.readObject().toString();
            		}
            		World w = Bukkit.getServer().getWorld(wname);
                	
                    Double x = iostream.readDouble();
                    Double y = iostream.readDouble();
                    Double z = iostream.readDouble();
                    Object time = iostream.readObject();
                    
                    DBLogEntry entry = new DBLogEntry(p, w.getBlockAt(new Location(w, x, y, z)), (Date)time);
                    Log.add(entry);
                    byte breakcount = 0;
                    
                    if (logCounter.containsKey(entry.getOfflinePlayer().getName())) {
                    	breakcount = logCounter.get(entry.getOfflinePlayer().getName());
                    	logCounter.remove(entry.getOfflinePlayer().getName());
                    	
                    }
                    logCounter.put(entry.getOfflinePlayer().getName(), breakcount++);
                }
                
            } catch (FileNotFoundException e) {
                m.l.info("DB ERROR: Could not locate DBLog.dat");
                e.printStackTrace();
            } catch(IOException e) {
                m.l.info("DB ERROR: I\\O error while attempting to read DBLog.dat");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                m.l.info("DB ERROR: Could not read DBLog.dat, class not found.");
                e.printStackTrace();
            } finally {
                try {
                    ifstream.close();
                } catch (IOException e) {
                    m.l.info("DB ERROR: Error reading DBLog.dat, could not close stream.");
                    e.printStackTrace();
                }
            }
        }
    }

    public void initAutosave(JavaPlugin plugin) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() { 
        	public void run() {
        		SaveDecoys();
        		SaveLog();
        		LoadDecoys();
        		LoadLog();
        	} 
        }, 1200L * 5L, 1200L * 5L ); // saves every 5 minutes
    }
    
    public void deinitAutosave(JavaPlugin plugin) {
    	Bukkit.getServer().getScheduler().cancelTasks(plugin);
    }
}
