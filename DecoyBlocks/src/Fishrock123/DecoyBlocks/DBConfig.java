package Fishrock123.DecoyBlocks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class DBConfig {
	public static DecoyBlocks m;
	
	public DBConfig(DecoyBlocks instance) {
		m = instance;
	}
	
	@SuppressWarnings("unchecked")
	public void generate() {
		final FileConfiguration c = m.getConfig();
		
		if (!new File(m.getDataFolder(), "config.yml").exists()) {
			m.l.info("DB: Generating New Config File... ");
			c.addDefault("Punishments", Arrays.asList("1,Jail", "3,Ban"));
			c.addDefault("JailLocation", Arrays.asList("world", 0, 0, 0));
			c.addDefault("AutoRestore", false);
			c.addDefault("AutoSave", true);
   	 		c.options().copyDefaults(true);
   		    m.saveConfig();
   		    
   		    m.l.info("DB: Adding Header to config.yml... ");
   		    StringBuffer contents = new StringBuffer();
   		    try {
   		    	BufferedReader reader = new BufferedReader(new InputStreamReader(m.getClass().getResourceAsStream("configheader.txt")));
   		    	String text = new String();
   		    	while ((text = reader.readLine()) != null) {
   		    		contents.append(text).append(System.getProperty("line.separator"));
   		    	}
   		    	try {
   		    		if (reader != null) {
 						reader.close();
 					}
 				} catch (IOException e) {
 					e.printStackTrace();
 				}
 			} catch (FileNotFoundException e) {
 				e.printStackTrace();
 			} catch (IOException e) {
 				e.printStackTrace();
 			} finally {
 		
 				c.options().header(contents.toString());
 				c.options().copyHeader(true);
 				m.saveConfig();
 			}
		}
	}
	@SuppressWarnings("unchecked")
	public void load() {
		final FileConfiguration c = m.getConfig();
  		m.l.info("DB: Loading Config File... ");
  		
  		List<String> l = c.getStringList("Punishments");
  		
  		for (String s: l) {
  			m.punishments.add(new DBPunishment(Byte.parseByte(s.split(",")[0]), s.split(",")[1]));
  		}
  		
  		List<?> coords = c.getList("JailLocation");
  		
  		try {
  			m.jailLoc = new Location(Bukkit.getWorld((String) coords.get(0)), Double.parseDouble(coords.get(1).toString()), Double.parseDouble(coords.get(2).toString()), Double.parseDouble(coords.get(3).toString()));
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  		
  		m.AutoRestore = c.getBoolean("AutoRestore", false);
  		m.AutoSave = c.getBoolean("AutoSave", true);
	}
}
