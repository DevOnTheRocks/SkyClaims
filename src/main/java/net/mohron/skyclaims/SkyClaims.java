package net.mohron.skyclaims;

import com.google.inject.Inject;
import me.lucko.luckperms.api.LuckPermsApi;
import me.ryanhamshire.griefprevention.GriefPrevention;
import net.mohron.skyclaims.command.*;
import net.mohron.skyclaims.config.ConfigManager;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.util.ConfigUtil;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionService;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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
		})
public class SkyClaims {
	private static SkyClaims instance;
	private static GriefPrevention griefPrevention;
	//	private static LuckPermsApi luckPerms;
	public static PermissionService permissionService;
	public static Map<UUID, Island> islands = new HashMap<>();

	@Inject
	private PluginContainer pluginContainer;

	@Inject
	private Logger logger;

	@Inject
	private Game game;

	//@Inject
	//private Metrics metrics;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;
	private GlobalConfig defaultConfig;
	private ConfigManager pluginConfigManager;

	private Database database;

	@Listener
	public void onPostInitialization(GamePostInitializationEvent event) {
		getLogger().info(String.format("%s %s is initializing...", NAME, VERSION));

		instance = this;

		//TODO Setup the worldName with a sponge:void worldName gen modifier if not already created
	}

	@Listener(order = Order.LATE)
	public void onAboutToStart(GameAboutToStartServerEvent event) {
		SkyClaims.permissionService = Sponge.getServiceManager().provide(PermissionService.class).get();
		if (Sponge.getServiceManager().getRegistration(PermissionService.class).get().getPlugin().getId().equalsIgnoreCase("sponge")) {
			getLogger().error("Unable to initialize plugin. SkyClaims requires a permissions plugin.");
			return;
		}

		// GriefPrevention integration
		try {
			Class.forName("me.ryanhamshire.griefprevention.GriefPrevention");
			SkyClaims.griefPrevention = GriefPrevention.instance;
			getLogger().info("GriefPrevention Integration Successful!");
		} catch (ClassNotFoundException e) {
			getLogger().info("GriefPrevention Integration Failed!");
		}

//		Optional<LuckPermsApi> luckPerms = Sponge.getServiceManager().provide(LuckPermsApi.class);
//		luckPerms.ifPresent(lp -> {
//			SkyClaims.luckPerms = lp;
//			getLogger().info("LuckPerms Integration Successful!");
//		});

		defaultConfig = new GlobalConfig();
		pluginConfigManager = new ConfigManager(configManager);
		pluginConfigManager.save();

		Sponge.getGame().getEventManager().registerListeners(this, new SchematicHandler());

		registerCommands();
	}

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		database = new Database(getConfigDir() + File.separator + "skyclaims.db");
		islands = database.loadData();
		getLogger().info("ISLAND LENGTH: " + islands.keySet().size());

		getLogger().info("Initialization complete.");
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Listener
	public void onWorldSave(SaveWorldEvent.Post event) {
		if (event.isCancelled() || event.getTargetWorld().equals(ConfigUtil.getWorld())) {
			database.saveData(islands);
		}
	}

	@Listener
	public void onServerStopped(GameStoppingServerEvent event) {
		getLogger().info(String.format("%S %S is stopping...", NAME, VERSION));
		database.saveData(islands);
		getLogger().info("Shutdown actions complete.");
	}

	private void registerCommands() {
		CommandAdmin.register();
		CommandCreate.register();
		CommandHelp.register();
		CommandInfo.register();
		CommandIsland.register();
		CommandList.register();
		CommandReset.register();
		CommandCreateSchematic.register();
		CommandSetBiome.register();
		CommandSetSpawn.register();
		CommandSetup.register();
		CommandSpawn.register();
	}

	public static SkyClaims getInstance() {
		return instance;
	}

	public GriefPrevention getGriefPrevention() {
		return griefPrevention;
	}

//	public LuckPermsApi getLuckPerms() {
//		return luckPerms;
//	}

	public PluginContainer getPluginContainer() {
		return pluginContainer;
	}

	public Logger getLogger() {
		return logger;
	}

	public Game getGame() {
		return game;
	}

	public GlobalConfig getConfig() {
		return this.defaultConfig;
	}

	public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
		return this.configManager;
	}

	public Path getConfigDir() {
		return configDir;
	}

	public Database getDatabase() {
		return database;
	}
}