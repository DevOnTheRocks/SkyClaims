/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.GriefPreventionApi;
import net.mohron.skyclaims.command.CommandAdmin;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.admin.CommandConfig;
import net.mohron.skyclaims.command.admin.CommandCreateSchematic;
import net.mohron.skyclaims.command.admin.CommandDelete;
import net.mohron.skyclaims.command.admin.CommandReload;
import net.mohron.skyclaims.command.admin.CommandSetup;
import net.mohron.skyclaims.command.admin.CommandTransfer;
import net.mohron.skyclaims.command.argument.SchematicArgument;
import net.mohron.skyclaims.command.user.*;
import net.mohron.skyclaims.config.ConfigManager;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.database.IDatabase;
import net.mohron.skyclaims.database.MysqlDatabase;
import net.mohron.skyclaims.database.SqliteDatabase;
import net.mohron.skyclaims.integration.Integration;
import net.mohron.skyclaims.listener.ClaimEventHandler;
import net.mohron.skyclaims.listener.ClientJoinHandler;
import net.mohron.skyclaims.listener.RespawnHandler;
import net.mohron.skyclaims.listener.SchematicHandler;
import net.mohron.skyclaims.metrics.Metrics;
import net.mohron.skyclaims.world.Island;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionService;

import java.nio.file.Path;
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
		@Dependency(id = "griefprevention", version = GP_VERSION),
		@Dependency(id = "nucleus", version = NUCLEUS_VERSION, optional = true)
	})
public class SkyClaims {
	private static SkyClaims instance;
	private static GriefPreventionApi griefPrevention;
	private static PermissionService permissionService;
	private static Integration integration;
	public static Map<UUID, Island> islands = Maps.newHashMap();
	private static Set<Island> saveQueue = Sets.newHashSet();

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
		integration = new Integration();

		try {
			SkyClaims.griefPrevention = GriefPrevention.getApi();
		} catch (IllegalStateException e) {
			getLogger().error("GriefPrevention API failed to load.");
		}

		if (SkyClaims.griefPrevention != null) {
			if (griefPrevention.getApiVersion() < GP_API_VERSION) {
				getLogger().error(String.format(
					"GriefPrevention API version %s is unsupported! Please update to API version %s+.",
					griefPrevention.getApiVersion(), GP_API_VERSION
				));
				enabled = false;
			} else
				getLogger().info("GriefPrevention Integration Successful!");
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

		permissionService = Sponge.getServiceManager().provideUnchecked(PermissionService.class);
		if (Sponge.getServiceManager().getRegistration(PermissionService.class).get().getPlugin().getId()
			.equalsIgnoreCase("sponge")) {
			getLogger()
				.error("Unable to initialize plugin. SkyClaims requires a permissions plugin. Disabling SkyClaims.");
			enabled = false;
			return;
		}

		defaultConfig = new GlobalConfig();
		pluginConfigManager = new ConfigManager(configManager);
		pluginConfigManager.save();

		getGame().getEventManager().registerListeners(this, new SchematicHandler());
		getGame().getEventManager().registerListeners(this, new ClaimEventHandler());
		getGame().getEventManager().registerListeners(this, new RespawnHandler());
		getGame().getEventManager().registerListeners(this, new ClientJoinHandler());

		registerCommands();
	}

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		if (!enabled) return;

		database = initializeDatabase();

		islands = database.loadData();
		getLogger().info("Islands Loaded: " + islands.size());
		if (!saveQueue.isEmpty()) {
			getLogger().info("Saving " + saveQueue.size() + " claims that were malformed");
			database.saveData(saveQueue);
		}

		addCustomMetrics();

		getLogger().info("Initialization complete.");
	}

	@Listener
	public void onGameStopping(GameStoppingServerEvent event) {
		if (!enabled) return;
		getLogger().info(String.format("%S %S is stopping...", NAME, VERSION));
	}

	@Listener
	public void onReload(GameReloadEvent event) {
		reload();
	}

	public void reload() {
		// Load Plugin Config
		pluginConfigManager.load();
		// Load Schematics Directory
		SchematicArgument.load();
		// Load Database
		islands = database.loadData();
	}

	private void registerCommands() {
		CommandAdmin.register();
		CommandConfig.register();
		CommandCreate.register();
		CommandCreateSchematic.register();
		CommandExpand.register();
		CommandHome.register();
		CommandDelete.register();
		CommandInfo.register();
		CommandIsland.register();
		CommandList.register();
		CommandLock.register();
		CommandReload.register();
		CommandReset.register();
		CommandSetBiome.register();
		CommandSetHome.register();
		CommandSetSpawn.register();
		CommandSetup.register();
		CommandSpawn.register();
		CommandTransfer.register();
		CommandUnlock.register();
	}

	private IDatabase initializeDatabase() {
		String type = defaultConfig.getStorageConfig().getType();
		if (type.equalsIgnoreCase("SQLite")) {
			return new SqliteDatabase();
		} else if (type.equalsIgnoreCase("MySQL")) {
			return new MysqlDatabase();
		} else {
			return new SqliteDatabase();
		}
	}

	private void addCustomMetrics() {
		metrics.addCustomChart(new Metrics.SingleLineChart("islands") {
			@Override
			public int getValue() {
				return islands.size();
			}
		});
		metrics.addCustomChart(new Metrics.SimplePie("sponge_version") {
			@Override
			public String getValue() {
				return Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getVersion().orElse(null);
			}
		});
		metrics.addCustomChart(new Metrics.SimplePie("allocated_ram") {
			@Override
			public String getValue() {
				return String.format("%s GB", Math.round((Runtime.getRuntime().maxMemory() / 1024.0 / 1024.0 / 1024.0) * 2.0) / 2.0);
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

	public Integration getIntegration() {
		return integration;
	}

	public Cause getCause() {
		return Cause.source(pluginContainer).build();
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

	public void setConfig(GlobalConfig config){
		this.defaultConfig = config;
	}

	public ConfigManager getConfigManager() {
		return this.pluginConfigManager;
	}

	public Path getConfigDir() {
		return configDir;
	}

	public IDatabase getDatabase() {
		return database;
	}

	public void queueForSaving(Island island) {
		saveQueue.add(island);
	}
}