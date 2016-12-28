package net.mohron.skyclaims;

import com.google.inject.Inject;
import me.lucko.luckperms.api.LuckPermsApi;
import me.ryanhamshire.griefprevention.GriefPrevention;
import net.mohron.skyclaims.command.CommandCreate;
import net.mohron.skyclaims.command.CommandHelp;
import net.mohron.skyclaims.command.CommandInfo;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.CommandReset;
import net.mohron.skyclaims.command.CommandSetBiome;
import net.mohron.skyclaims.command.CommandSetSpawn;
import net.mohron.skyclaims.config.GlobalConfig;
import net.mohron.skyclaims.island.Island;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
	private static GriefPrevention griefPrevention;
	private static LuckPermsApi luckPerms;
	public static Map<UUID, Island> islands = new HashMap<>();

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

	@Listener
	public void onPostInialization(GamePostInitializationEvent event) {
		instance = this;

		Optional<GriefPrevention> griefPrevention = Sponge.getServiceManager().provide(GriefPrevention.class);
		griefPrevention.ifPresent(gp -> {
			this.griefPrevention = gp;
			getLogger().info("GriefPrevention Integration Successful!");
		});
		Optional<LuckPermsApi> luckPerms = Sponge.getServiceManager().provide(LuckPermsApi.class);
		luckPerms.ifPresent(lp -> {
			this.luckPerms = lp;
			getLogger().info("LuckPerms Integration Successful!");
		});
	}

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {

		getLogger().info(String.format("%s %s is initializing...", NAME, VERSION));

		config = new GlobalConfig();

		database = new Database("SkyClaims.db");
		islands = database.loadData();
		getLogger().info("ISLAND LENGTH: " + islands.keySet().size());

		registerCommands();
		// TODO - Load database data into memory
		getLogger().info("Initialization complete.");
	}

	@Listener
	public void onServerStopped(GameStoppedServerEvent event) {
		getLogger().info(String.format("%S %S is stopping...", NAME, VERSION));
		database.saveData(islands);
		getLogger().info("Shutdown actions complete.");
	}

	private void registerCommands() {
		CommandIsland.register();
		CommandHelp.register();
		CommandCreate.register();
		CommandReset.register();
		CommandSetSpawn.register();
		CommandInfo.register();
		CommandSetBiome.register();
	}

	public static SkyClaims getInstance() {
		return instance;
	}

	public LuckPermsApi getLuckPerms() {
		return luckPerms;
	}

	public GriefPrevention getGriefPrevention() {
		return griefPrevention;
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