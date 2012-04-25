package com.fishrock123.decoyblocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.fishrock123.decoyblocks.utils.Metrics;

public class DecoyBlocks extends JavaPlugin {
	public List<DBPunishment> punishments = new ArrayList<DBPunishment>();
	public boolean AutoRestore;
	public long AutoRestoreTime;
	public boolean AutoSave;
	public Location jailLoc;
	public List<Player> logset = new ArrayList<Player>();
	
	public Logger l;
	public DBDatabase database;
	public DBCommands commands;
	public DBBlockListener bListener;
	public DBPlayerListener pListener;
	public DBConfig config;
	public DBProcessor processor;

	@Override
	public void onEnable() {
		final long startTime = System.nanoTime();
        final long endTime;
        
        l = getLogger();
        
        config = new DBConfig(this);
        database = new DBDatabase(this);
        pListener = new DBPlayerListener(this);
        
        this.initialize();
        
        commands = new DBCommands(this);
        processor = new DBProcessor(this);
        bListener = new DBBlockListener(this);
        
  		getServer().getPluginManager().registerEvents(bListener, this);
  		getServer().getPluginManager().registerEvents(pListener, this);
  		
		endTime = System.nanoTime();
		l.info("DecoyBlocks version " + getDescription().getVersion() + " is enabled! {" + TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) + " ms}");
		
		if (getDescription().getVersion().contains("TEST")) {
			l.info("DB Disclaimer: ");
			l.info("You are running a Testing version of DecoyBlocks!");
			l.info("This version may contain unwanted bugs, ");
			l.info(" and new features may not be fully functioning.");
		}
		
		try {
		    Metrics metrics = new Metrics(this);
		    
		    metrics.addCustomData(new Metrics.Plotter("Decoys") {
		        @Override
		        public synchronized int getValue() {
		        	return database.decoys.size();
		        }
		    });
		    
		    metrics.addCustomData(new Metrics.Plotter("Log Entries") {
		        @Override
		        public synchronized int getValue() {
		        	return database.Log.size();
		        }
		    });

		    metrics.start();
		    
		} catch (IOException e) {
		    l.info(e.getMessage());
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
	
	@Override
	public void onDisable() {  
		final long startTime = System.nanoTime();
        final long endTime;
        
        database.deinitAutosave(this);
		
		database.SaveDecoys();
		database.SaveLog();
		endTime = System.nanoTime();
		l.info("DecoyBlocks Disabled! {" + TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS) + " ms}");
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String cLabel, String[] args) {
		return commands.commandProcess(s, cmd, cLabel, args);
	}
}
