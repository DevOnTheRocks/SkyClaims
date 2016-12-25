package net.mohron.skyclaims;

import com.google.inject.Inject;
import me.lucko.luckperms.api.LuckPermsApi;
import net.mohron.skyclaims.command.CommandCreate;
import net.mohron.skyclaims.command.CommandHelp;
import net.mohron.skyclaims.command.CommandInfo;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.CommandReset;
import net.mohron.skyclaims.command.CommandSetSpawn;
import net.mohron.skyclaims.config.GlobalConfig;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.Optional;

import static net.mohron.skyclaims.PluginInfo.*;

@Plugin(id = ID,
		name = NAME,
		version = VERSION,
		description = DESCRIPTION,
		authors = AUTHORS,
		dependencies = {
				@Dependency(id = "griefprevention", optional = true),
				@Dependency(id = "luckperms", optional = true),
				@Dependency(id = "worldedit", optional = true)
		})
public class SkyClaims {
	private static SkyClaims instance;
	public DataStore dataStore;
	public static Optional<LuckPermsApi> luckPerms = Sponge.getServiceManager().provide(LuckPermsApi.class);

	@Inject
	private Logger logger;
	@Inject
	private Game game;
	//@Inject private Metrics metrics;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	private Database database;
	private GlobalConfig config;

	public static SkyClaims getInstance() {
		return instance;
	}

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		instance = this;

		getLogger().info(String.format("%s %s is initializing...", NAME, VERSION));

		config = new GlobalConfig();

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
		CommandSetSpawn.register();
		CommandInfo.register();
	}

	public Logger getLogger() {
		return logger;
	}

	public Game getGame() {
		return game;
	}

	public GlobalConfig getConfig() {
		return config;
	}

	public void setConfig(GlobalConfig config) {
		this.config = config;
	}

	public Path getConfigDir() {
		return configDir;
	}

	public Database getDatabase() {
		return database;
	}
}