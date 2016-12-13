package net.mohron.skyclaims;

import com.google.inject.Inject;
import net.mohron.skyclaims.command.CommandCreate;
import net.mohron.skyclaims.command.CommandHelp;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.CommandReset;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.sql.SQLException;

import static net.mohron.skyclaims.PluginInfo.*;

@Plugin(id = ID,
		name = NAME,
		version = VERSION,
		description = DESCRIPTION,
		authors = AUTHORS,
		dependencies = {
				@Dependency(id = "griefprevention", optional = true),
				@Dependency(id = "worldedit", optional = true)
		})
public class SkyClaims {
	private static SkyClaims instance;
	@Inject private Logger logger;
	@Inject private Game game;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;

	private Database database;

	public DataStore dataStore;

	public static SkyClaims getInstance() {
		return instance;
	}

	public Logger getLogger() {
		return logger;
	}

	public Game getGame() { return game; }

	public Path getConfigDir() {
		return configDir;
	}

	public Database getDatabase() { return database; }

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		instance = this;

		getLogger().info(String.format("%s %s is initializing...", NAME, VERSION));

		if (this.dataStore == null) {
			try {
				this.dataStore = new DataStore();
			} catch (Exception e) {
				getLogger().error(e.getMessage());
			}
		}

		database = new Database("SkyClaims.db");
		dataStore = database.loadData();
		getLogger().info("ISLAND LENGTH: " + dataStore.data.keySet().size());

		// DEBUG TEST
//		try {
//			database.saveData(dataStore);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

		registerCommands();
		// TODO - Load database data into memory
		getLogger().info("Initialization complete.");
	}

	@Listener
	public void onServerStopped(GameStoppedServerEvent event) {
		getLogger().info(String.format("%S %S is stopping...", NAME, VERSION));
		// TODO - Dump data in memory to the database
		getLogger().info("Shutdown actions complete.");
	}

	private void registerCommands() {
		CommandIsland.register();
		CommandHelp.register();
		CommandCreate.register();
		CommandReset.register();
	}
}
