package Fishrock123.DecoyBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public class DecoyBlocks extends JavaPlugin {
	public List<DBPunishment> punishments = new ArrayList<DBPunishment>();
	public boolean AutoRestore;
	public boolean AutoSave;
	public Location jailLoc;
	public List<Player> logset = new ArrayList<Player>();
	
	public final Logger l = Logger.getLogger("Minecraft");
	public DBDatabase database;
	public DBCommands commands;
	public DBBlockListener bListener;
	public DBPlayerListener pListener;
	public DBConfig config;
	public DBProcessor processor;

	public void onEnable() {
		final long startTime = System.nanoTime();
        final long endTime;
        
        config = new DBConfig(this);
        database = new DBDatabase(this);
        bListener = new DBBlockListener(this);
        pListener = new DBPlayerListener(this);
        
        this.initialize();
        
        commands = new DBCommands(this);
        processor = new DBProcessor(this);
        
  		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, bListener, Event.Priority.Lowest, this);
  		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, pListener, Event.Priority.High, this);
  		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, bListener, Event.Priority.Lowest, this);
  		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, pListener, Event.Priority.Lowest, this);
  		
		endTime = System.nanoTime();
		l.info("DecoyBlocks version " + getDescription().getVersion() + " is enabled! {" + TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) + " ms}");
		
		if (getDescription().getVersion().contains("TEST") 
				|| getDescription().getVersion().contains("ALPHA")
				|| getDescription().getVersion().contains("BETA")) {
			l.info("DB Disclaimer: ");
			l.info("You are running a Testing version of DecoyBlocks!");
			l.info("This version may contain unwanted bugs, ");
			l.info(" and new features may not be fully functioning.");
		}
	}
	
	public void initialize() {
		config.generate();
		config.load();
		database.LoadDecoys();
		database.LoadLog();
		
		if (AutoSave) {
			database.initAutosave(this);
		}
	}
	
	public void onDisable() {  
		final long startTime = System.nanoTime();
        final long endTime;
        
        database.deinitAutosave(this);
		
		database.SaveDecoys();
		database.SaveLog();
		endTime = System.nanoTime();
		l.info("DecoyBlocks Disabled! {" + TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) + " ms}");
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String cLabel, String[] args) {
		return commands.commandProcess(s, cmd, cLabel, args);
	}
}
