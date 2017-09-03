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

import static net.mohron.skyclaims.PluginInfo.AUTHORS;
import static net.mohron.skyclaims.PluginInfo.DESCRIPTION;
import static net.mohron.skyclaims.PluginInfo.GP_API_VERSION;
import static net.mohron.skyclaims.PluginInfo.GP_VERSION;
import static net.mohron.skyclaims.PluginInfo.ID;
import static net.mohron.skyclaims.PluginInfo.NAME;
import static net.mohron.skyclaims.PluginInfo.NUCLEUS_VERSION;
import static net.mohron.skyclaims.PluginInfo.VERSION;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.GriefPreventionApi;
import net.mohron.skyclaims.command.CommandAdmin;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.SchematicArgument;
import net.mohron.skyclaims.config.ConfigManager;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.database.IDatabase;
import net.mohron.skyclaims.database.MysqlDatabase;
import net.mohron.skyclaims.database.SqliteDatabase;
import net.mohron.skyclaims.integration.Version;
import net.mohron.skyclaims.integration.nucleus.NucleusIntegration;
import net.mohron.skyclaims.listener.ClaimEventHandler;
import net.mohron.skyclaims.listener.ClientJoinHandler;
import net.mohron.skyclaims.listener.EntitySpawnHandler;
import net.mohron.skyclaims.listener.RespawnHandler;
import net.mohron.skyclaims.listener.SchematicHandler;
import net.mohron.skyclaims.listener.WorldLoadHandler;
import net.mohron.skyclaims.metrics.Metrics;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandCleanupTask;
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
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.permission.PermissionService;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    public static Map<UUID, Island> islands = Maps.newHashMap();
    private static Set<Island> saveQueue = Sets.newHashSet();
    private static SkyClaims instance;
    private GriefPreventionApi griefPrevention;
    private PermissionService permissionService;

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

    private static final String cleanup = "skyclaims.island.cleanup";

    public static SkyClaims getInstance() {
        return instance;
    }

    @Listener
    public void onGameConstruction(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        logger.info("{} {} is initializing...", NAME, VERSION);

        defaultConfig = new GlobalConfig();
        pluginConfigManager = new ConfigManager(configManager);
        pluginConfigManager.save();

        if (Sponge.getPluginManager().isLoaded("nucleus")) {
            Sponge.getEventManager().registerListeners(this, new NucleusIntegration());
        }
    }

    @Listener
    public void onPostInitialization(GamePostInitializationEvent event) {
        try {
            griefPrevention = GriefPrevention.getApi();
        } catch (IllegalStateException e) {
            logger.error("GriefPrevention API failed to load.");
        }

        if (griefPrevention != null) {
            if (griefPrevention.getApiVersion() < GP_API_VERSION) {
                logger.error(
                    "GriefPrevention API version {} is unsupported! Please update to API version {}+.",
                    griefPrevention.getApiVersion(), GP_API_VERSION
                );
                enabled = false;
            } else if (Version.of(griefPrevention.getImplementationVersion()).compareTo(GP_VERSION) < 0) {
                logger.error(
                    "GriefPrevention version {} is unsupported! Please update to version {}+.",
                    griefPrevention.getImplementationVersion(), GP_VERSION
                );
                enabled = false;
            } else {
                logger.info("Successfully integrated with GriefPrevention {}!", griefPrevention.getImplementationVersion());
            }
        } else {
            logger.error("GriefPrevention Integration Failed! Disabling SkyClaims.");
            enabled = false;
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Listener(order = Order.LATE)
    public void onAboutToStart(GameAboutToStartServerEvent event) {
        if (!enabled) {
            return;
        }

        permissionService = Sponge.getServiceManager().provideUnchecked(PermissionService.class);
        if (Sponge.getServiceManager().getRegistration(PermissionService.class).get().getPlugin().getId().equalsIgnoreCase("sponge")) {
            logger.error("Unable to initialize plugin. SkyClaims requires a permissions plugin. Disabling SkyClaims.");
            enabled = false;
            return;
        }

        registerListeners();
        registerTasks();
        registerCommands();
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        if (!enabled) {
            return;
        }

        database = initializeDatabase();

        islands = database.loadData();
        logger.info("{} islands loaded.", islands.size());
        if (!saveQueue.isEmpty()) {
            logger.info("Saving {} claims that were malformed.", saveQueue.size());
            database.saveData(saveQueue);
        }

        addCustomMetrics();

        logger.info("Initialization complete.");
    }

    @Listener
    public void onGameStopping(GameStoppingServerEvent event) {
        if (!enabled) {
            return;
        }
        logger.info("{} {} is stopping...", NAME, VERSION);
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
        // Reload Listeners
        Sponge.getEventManager().unregisterPluginListeners(this);
        registerListeners();
        // Reload Tasks
        Sponge.getScheduler().getTasksByName(cleanup).forEach(Task::cancel);
        registerTasks();
        // Reload Commands
        Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
        CommandIsland.clearSubCommands();
        registerCommands();
    }

    private void registerListeners() {
        getGame().getEventManager().registerListeners(this, new SchematicHandler());
        getGame().getEventManager().registerListeners(this, new ClaimEventHandler());
        getGame().getEventManager().registerListeners(this, new RespawnHandler());
        getGame().getEventManager().registerListeners(this, new ClientJoinHandler());
        getGame().getEventManager().registerListeners(this, new WorldLoadHandler());

        if (getConfig().getEntityConfig().isLimitSpawning()) {
            getGame().getEventManager().registerListeners(this, new EntitySpawnHandler());
        }
    }

    private void registerTasks() {
        if (getConfig().getExpirationConfig().isEnabled()) {
            Sponge.getScheduler().createTaskBuilder()
                .name(cleanup)
                .execute(new IslandCleanupTask())
                .interval(getConfig().getExpirationConfig().getInterval(), TimeUnit.MINUTES)
                .async()
                .submit(this);
        }
    }

    private void registerCommands() {
        CommandIsland.register();
        CommandAdmin.register();
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

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public GriefPreventionApi getGriefPrevention() {
        return griefPrevention;
    }

    public PermissionService getPermissionService() {
        return permissionService;
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

    public void setConfig(GlobalConfig config) {
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
