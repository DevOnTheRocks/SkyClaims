package net.mohron.skyclaims;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.GriefPreventionApi;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import net.mohron.skyclaims.command.*;
import net.mohron.skyclaims.config.ConfigManager;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.database.IDatabase;
import net.mohron.skyclaims.database.MysqlDatabase;
import net.mohron.skyclaims.database.SqliteDatabase;
import net.mohron.skyclaims.listener.ClaimEventHandler;
import net.mohron.skyclaims.listener.ClientJoinHandler;
import net.mohron.skyclaims.listener.RespawnHandler;
import net.mohron.skyclaims.listener.SchematicHandler;
import net.mohron.skyclaims.metrics.Metrics;
import net.mohron.skyclaims.util.ConfigUtil;
import net.mohron.skyclaims.world.Island;
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
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.mohron.skyclaims.PluginInfo.*;

@Plugin(id = ID,
		name = NAME,
		version = VERSION,
		description = DESCRIPTION,
		authors = AUTHORS,
		dependencies = {
				@Dependency(id = "griefprevention")
		})
public class SkyClaims {
	private static SkyClaims instance;
	private static GriefPreventionApi griefPrevention;
	private static PermissionService permissionService;
	public static Map<UUID, Island> islands = Maps.newHashMap();
	public static Set<Claim> islandClaims = Sets.newHashSet();

	@Inject
	private PluginContainer pluginContainer;

	@Inject
	private Logger logger;

	@Inject
	private Game game;

	@Inject
	private Metrics metrics;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path schematicDir = Paths.get(configDir + File.separator + "schematics");
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;
	private ConfigManager pluginConfigManager;
	private GlobalConfig defaultConfig;

	private IDatabase database;

	private boolean enabled = true;

	@Listener
	public void onPostInitialization(GamePostInitializationEvent event) {
		getLogger().info(String.format("%s %s is initializing...", NAME, VERSION));

		instance = this;

		SkyClaims.griefPrevention = GriefPrevention.getApi();
		if (SkyClaims.griefPrevention != null) {
			getLogger().info("GriefPrevention Integration Successful!");
			if (griefPrevention.getApiVersion() < GP_API_VERSION) {
				getLogger().info(String.format("GriefPrevention API version %s is unsupported! Please update to API version %s+.", griefPrevention.getApiVersion(), GP_API_VERSION));
				enabled = false;
			}
		} else {
			getLogger().error("GriefPrevention Integration Failed! Disabling SkyClaims.");
			enabled = false;
		}

		//TODO Setup the worldName with a sponge:void worldName gen modifier if not already created
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Listener(order = Order.LATE)
	public void onAboutToStart(GameAboutToStartServerEvent event) {
		if (!enabled) return;
		SkyClaims.permissionService = Sponge.getServiceManager().provide(PermissionService.class).get();
		if (Sponge.getServiceManager().getRegistration(PermissionService.class).get().getPlugin().getId().equalsIgnoreCase("sponge")) {
			getLogger().error("Unable to initialize plugin. SkyClaims requires a permissions plugin. Disabling SkyClaims.");
			enabled = false;
			return;
		}

		defaultConfig = new GlobalConfig();
		pluginConfigManager = new ConfigManager(configManager);
		pluginConfigManager.save();

		Sponge.getGame().getEventManager().registerListeners(this, new SchematicHandler());
		Sponge.getGame().getEventManager().registerListeners(this, new ClaimEventHandler());
		Sponge.getGame().getEventManager().registerListeners(this, new RespawnHandler());
		Sponge.getGame().getEventManager().registerListeners(this, new ClientJoinHandler());

		registerCommands();
	}

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		if (!enabled) return;

		if (ConfigUtil.getDatabaseType().equalsIgnoreCase("sqlite"))
			database = new SqliteDatabase();
		else if (ConfigUtil.getDatabaseType().equalsIgnoreCase("mysql"))
			database = new MysqlDatabase();
		else
			throw new UnsupportedOperationException("Database type not supported, should not continue.");

		islands = database.loadData();
		addCustomMetrics();
		getLogger().info("ISLAND LENGTH: " + islands.size());
		getLogger().info("Initialization complete.");
	}

	@Listener
	public void onGameStopping(GameStoppingServerEvent event) {
		if (!enabled) return;
		getLogger().info(String.format("%S %S is stopping...", NAME, VERSION));
	}

	private void registerCommands() {
		CommandAdmin.register();
		CommandCreate.register();
		CommandCreateSchematic.register();
		CommandDelete.register();
		CommandInfo.register();
		CommandIsland.register();
		CommandList.register();
		CommandLock.register();
		CommandReload.register();
		CommandReset.register();
		CommandSetBiome.register();
		CommandSetSpawn.register();
		CommandSetup.register();
		CommandSpawn.register();
		CommandUnlock.register();
	}

	private void addCustomMetrics() {
		metrics.addCustomChart(new Metrics.SingleLineChart("islands") {
			@Override
			public int getValue() {
				return islands.size();
			}
		});
	}

	public static SkyClaims getInstance() {
		return instance;
	}

	public GriefPreventionApi getGriefPrevention() {
		return griefPrevention;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

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

	public ConfigManager getConfigManager() {
		return this.pluginConfigManager;
	}

	public Path getConfigDir() {
		return configDir;
	}

	public Path getSchematicDir() {
		return schematicDir;
	}

	public IDatabase getDatabase() {
		return database;
	}
}