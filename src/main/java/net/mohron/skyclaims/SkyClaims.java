/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2022 Mohron
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
import com.google.inject.Inject;
import com.griefdefender.api.GriefDefender;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.debug.CommandPlayerInfo;
import net.mohron.skyclaims.command.debug.CommandVersion;
import net.mohron.skyclaims.config.ConfigManager;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.database.IDatabase;
import net.mohron.skyclaims.database.MysqlDatabase;
import net.mohron.skyclaims.database.SqliteDatabase;
import net.mohron.skyclaims.integration.Version;
import net.mohron.skyclaims.integration.griefdefender.GDIntegration;
import net.mohron.skyclaims.integration.nucleus.NucleusIntegration;
import net.mohron.skyclaims.listener.ClientJoinHandler;
import net.mohron.skyclaims.listener.EntitySpawnHandler;
import net.mohron.skyclaims.listener.RespawnHandler;
import net.mohron.skyclaims.listener.SchematicHandler;
import net.mohron.skyclaims.schematic.SchematicManager;
import net.mohron.skyclaims.team.InviteService;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.IslandCleanupTask;
import net.mohron.skyclaims.world.IslandManager;
import net.mohron.skyclaims.world.gen.VoidWorldGeneratorModifier;
import net.mohron.skyclaims.world.region.Region;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bstats.sponge.Metrics;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.world.ConstructWorldPropertiesEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.metric.MetricsConfigManager;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

@Plugin(
    id = PluginInfo.ID,
    name = PluginInfo.NAME,
    version = PluginInfo.VERSION,
    description = PluginInfo.DESCRIPTION,
    authors = PluginInfo.AUTHORS,
    dependencies = {
        @Dependency(id = "spongeapi", version = PluginInfo.SPONGE_API),
        @Dependency(id = "griefdefender", version = PluginInfo.GD_VERSION),
        @Dependency(id = "nucleus", version = PluginInfo.NUCLEUS_VERSION, optional = true)
    })
public class SkyClaims {

  private static SkyClaims instance;
  private PermissionService permissionService;

  @Inject
  private PluginContainer pluginContainer;

  @Inject
  private MetricsConfigManager metricsConfigManager;

  @Inject
  private Logger logger;

  @Inject
  private Game game;

  private Metrics metrics;

  @Inject
  @ConfigDir(sharedRoot = false)
  private Path configDir;
  @Inject
  @DefaultConfig(sharedRoot = false)
  private ConfigurationLoader<CommentedConfigurationNode> configLoader;
  private ConfigManager configManager;
  private GlobalConfig config;

  private IDatabase database;

  private Map<UUID, IslandManager> islandManagers = Maps.newHashMap();
  private InviteService inviteService;
  private SchematicManager schematicManager;
  private WorldGeneratorModifier voidGenModifier = new VoidWorldGeneratorModifier();

  private boolean enabled = true;
  private boolean setupSpawn = false;

  private static final String ISLAND_CLEANUP = "skyclaims.island.cleanup";

  @Inject
  public SkyClaims(Metrics.Factory metricsFactory) {
    metrics = metricsFactory.make(96);
  }

  public static SkyClaims getInstance() {
    return instance;
  }

  @Listener
  public void onGameConstruction(GameConstructionEvent event) {
    instance = this;
  }

  @Listener
  public void onInitialization(GameInitializationEvent event) {
    logger.info("{} {} is initializing...", PluginInfo.NAME, PluginInfo.VERSION);

    config = new GlobalConfig();
    configManager = new ConfigManager(configLoader);
    configManager.save();

    if (Sponge.getPluginManager().isLoaded("nucleus")) {
      Sponge.getEventManager().registerListeners(this, new NucleusIntegration());
    }
  }

  private void validateGriefDefenderVersion() {
    com.griefdefender.api.Version version;
    try {
      version = GriefDefender.getVersion();
    } catch (IllegalStateException e) {
      logger.error("GriefDefender API failed to load!", e);
      logger.warn("Disabling SkyClaims.");
      enabled = false;
      return;
    }

    if (version.getApiVersion() < PluginInfo.GD_API_VERSION) {
      logger.error(
          "GriefDefender API version {} is unsupported! Please update to API version {}+.",
          version.getApiVersion(), PluginInfo.GD_API_VERSION
      );
      enabled = false;
    } else if (Version.of(version.getImplementationVersion()).compareTo(PluginInfo.GD_VERSION) < 0) {
      logger.error(
          "GriefDefender version {} is unsupported! Please update to version {}+.",
          version.getImplementationVersion(), PluginInfo.GD_VERSION
      );
      enabled = false;
    } else {
      Sponge.getEventManager().registerListeners(this, new GDIntegration());
      logger.info("Successfully integrated with GriefDefender {}!", version.getImplementationVersion());
    }
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Listener(order = Order.LATE)
  public void onAboutToStart(GameAboutToStartServerEvent event) {
    registerDebugCommands();
    validateGriefDefenderVersion();
    if (!enabled) {
      return;
    }

    permissionService = Sponge.getServiceManager().provideUnchecked(PermissionService.class);
    if (!Sponge.getServiceManager().getRegistration(PermissionService.class).isPresent()
        || Sponge.getServiceManager().getRegistration(PermissionService.class).get().getPlugin().getId().equalsIgnoreCase("sponge")) {
      logger.error("Unable to initialize plugin. SkyClaims requires a permissions plugin. Disabling SkyClaims.");
      enabled = false;
      return;
    }

    inviteService = new InviteService();
    schematicManager = new SchematicManager(this);

    registerListeners();
    registerTasks();
    registerCommands();
  }

  @Listener
  public void onGameRegistry(GameRegistryEvent.Register<WorldGeneratorModifier> event) {
    event.register(voidGenModifier);
  }

  @Listener
  public void onConstructWorldProperties(ConstructWorldPropertiesEvent event, @Getter(value = "getWorldProperties") WorldProperties properties) {
    if (!properties.isInitialized() && properties.getWorldName()
        .equalsIgnoreCase(config.getWorldConfig().getWorldName())) {
      setupSpawn = true;
    }
  }

  @Listener(order = Order.LATE)
  public void onWorldLoad(LoadWorldEvent event, @Getter(value = "getTargetWorld") World world) {
    if (!enabled || !world.equals(config.getWorldConfig().getWorld()) || !GriefDefender.getCore().isEnabled(world.getUniqueId())) {
      return;
    }

    if (setupSpawn && !config.getWorldConfig().isSeparateSpawn()) {
      Location<World> spawn = new Region(0, 0).getCenter();
      int size = 4;
      for (int x = -size; x <= size; x++) {
        for (int z = -size; z <= size; z++) {
          spawn.add(x, -1, z).setBlock(BlockState.builder().blockType(BlockTypes.BEDROCK).build());
          if (Math.abs(x) == size || Math.abs(z) == size) {
            spawn.add(x, 1, z).setBlock(BlockState.builder().blockType(BlockTypes.FENCE).build());
          }
          world.getProperties().setSpawnPosition(spawn.getPosition().toInt());
          world.getProperties().setGameRule("spawnRadius", "0");
        }
      }
      logger.info("Spawn area created.");
    }
  }

  @Listener
  public void onServerStarted(GameStartedServerEvent event) {
    if (!enabled) {
      return;
    }

    Optional<World> world = config.getWorldConfig().loadWorld();
    if (!world.isPresent()) {
      unload();
      this.enabled = false;
      logger.error("Unable to load world '{}'.", config.getWorldConfig().getWorldUuid().map(UUID::toString).orElse(config.getWorldConfig().getWorldName()));
      return;
    }
    if (logger.isInfoEnabled()) {
      logger.info(
          "Using world: {} | DIM {} | {}",
          world.get().getName(),
          world.get().getProperties().getAdditionalProperties().getInt(DataQuery.of("SpongeData", "dimensionId")).map(String::valueOf).orElse("?"),
          world.get().getUniqueId()
      );
    }

    database = initializeDatabase();
    schematicManager.load();

    IslandManager.ISLANDS = database.loadData();
    logger.info("{} islands loaded.", IslandManager.ISLANDS.size());
    if (!IslandManager.saveQueue.isEmpty()) {
      logger.info("Saving {} claims that were malformed.", IslandManager.saveQueue.size());
      database.saveData(IslandManager.saveQueue);
    }

    // SkyClaims Metrics are enabled by default
    if (this.metricsConfigManager.getCollectionState(this.pluginContainer) == Tristate.UNDEFINED) {
      Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "sponge metrics " + PluginInfo.ID + " enable");
    }
    addCustomMetrics();

    logger.info("Initialization complete.");
  }

  @Listener
  public void onGameStopping(GameStoppingServerEvent event) {
    if (!enabled) {
      return;
    }
    logger.info("{} {} is stopping...", PluginInfo.NAME, PluginInfo.VERSION);
  }

  @Listener
  public void onReload(GameReloadEvent event) {
    reload();
  }

  private void unload() {
    // Unregister Listeners
    Sponge.getEventManager().unregisterPluginListeners(this);
    // Cancel Tasks
    Sponge.getScheduler().getTasksByName(ISLAND_CLEANUP).forEach(Task::cancel);
    // Remove Commands
    Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
    CommandIsland.clearSubCommands();
  }

  public void reload() {
    if (enabled) {
      unload();
      // Load Plugin Config
      configManager.load();
      // Load Schematics
      schematicManager.load();
      // Load Database
      IslandManager.ISLANDS = database.loadData();
      // Reload Listeners
      registerListeners();
      // Reload Tasks
      registerTasks();
      // Reload Commands
      registerCommands();
    }
  }

  private void registerListeners() {
    getGame().getEventManager().registerListeners(this, new SchematicHandler());
    getGame().getEventManager().registerListeners(this, new RespawnHandler());
    getGame().getEventManager().registerListeners(this, new ClientJoinHandler());

    if (getConfig().getEntityConfig().isLimitSpawning()) {
      getGame().getEventManager().registerListeners(this, new EntitySpawnHandler());
    }
  }

  private void registerTasks() {
    if (getConfig().getExpirationConfig().isEnabled()) {
      Sponge.getScheduler().createTaskBuilder()
          .name(ISLAND_CLEANUP)
          .execute(new IslandCleanupTask(IslandManager.ISLANDS.values()))
          .interval(getConfig().getExpirationConfig().getInterval(), TimeUnit.MINUTES)
          .async()
          .submit(this);
    }
  }

  private void registerDebugCommands() {
    CommandVersion.register();
    CommandPlayerInfo.register();
  }

  private void registerCommands() {
    CommandIsland.register();
  }

  private IDatabase initializeDatabase() {
    String type = config.getStorageConfig().getType();
    if (type.equalsIgnoreCase("SQLite")) {
      return new SqliteDatabase();
    } else if (type.equalsIgnoreCase("MySQL")) {
      return new MysqlDatabase();
    } else {
      return new SqliteDatabase();
    }
  }

  private void addCustomMetrics() {
    metrics.addCustomChart(new SingleLineChart("islands", IslandManager.ISLANDS::size));
    metrics.addCustomChart(new DrilldownPie("sponge_version", () -> {
      Map<String, Map<String, Integer>> map = new HashMap<>();
      String api = Sponge.getPlatform().getContainer(Platform.Component.API).getVersion().orElse("?").split("-")[0];
      Map<String, Integer> entry = new HashMap<>();
      entry.put(Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getVersion().orElse(null), 1);
      map.put(api, entry);
      return map;
    }));
    metrics.addCustomChart(new SimplePie("allocated_ram", () ->
        String.format("%s GB", Math.round((Runtime.getRuntime().maxMemory() / 1024.0 / 1024.0 / 1024.0) * 2.0) / 2.0))
    );
  }

  public PluginContainer getPluginContainer() {
    return pluginContainer;
  }

  public PermissionService getPermissionService() {
    return permissionService;
  }

  public InviteService getInviteService() {
    return inviteService;
  }

  public SchematicManager getSchematicManager() {
    return schematicManager;
  }

  public Logger getLogger() {
    return logger;
  }

  public Game getGame() {
    return game;
  }

  public GlobalConfig getConfig() {
    return this.config;
  }

  public void setConfig(GlobalConfig config) {
    this.config = config;
  }

  public Path getConfigDir() {
    return configDir;
  }

  public IDatabase getDatabase() {
    return database;
  }

  public Optional<IslandManager> getIslandManager(World world) {
    return getIslandManager(world.getProperties());
  }

  public Optional<IslandManager> getIslandManager(WorldProperties world) {
    return Optional.ofNullable(islandManagers.get(world.getUniqueId()));
  }

  public void queueForSaving(Island island) {
    IslandManager.saveQueue.add(island);
  }
}
