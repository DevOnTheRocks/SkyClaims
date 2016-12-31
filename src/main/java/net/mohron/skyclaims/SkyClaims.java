package net.mohron.skyclaims;

import com.google.inject.Inject;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;
import me.lucko.luckperms.api.LuckPermsApi;
import me.ryanhamshire.griefprevention.GriefPrevention;
import net.mohron.skyclaims.claim.IClaim;
import net.mohron.skyclaims.command.*;
import net.mohron.skyclaims.config.GlobalConfig;
import net.mohron.skyclaims.island.Island;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.permission.PermissionService;

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
	private static SpongeWorldEdit spongeWorldEdit;
	public PermissionService permissionService;
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
	public void onPostInitialization(GamePostInitializationEvent event) {
		getLogger().info(String.format("%s %s is initializing...", NAME, VERSION));

		instance = this;

		//TODO Setup the world with a sponge:void world gen modifier if not already created
	}

	@Listener(order = Order.LATE)
	public void onAboutToStart(GameAboutToStartServerEvent event) {
		this.permissionService = Sponge.getServiceManager().provide(PermissionService.class).get();
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

		Optional<LuckPermsApi> luckPerms = Sponge.getServiceManager().provide(LuckPermsApi.class);
		luckPerms.ifPresent(lp -> {
			SkyClaims.luckPerms = lp;
			getLogger().info("LuckPerms Integration Successful!");
		});

		try {
			Class.forName("com.sk89q.worldedit.sponge.SpongeWorldEdit");
			SkyClaims.spongeWorldEdit = SpongeWorldEdit.inst();
			getLogger().info("WorldEdit Integration Successful!");
		} catch (ClassNotFoundException e) {
			getLogger().info("WorldEdit Integration Failed!");
		}
		Sponge.getGame().getEventManager().registerListeners(this, new SchematicHandler());
	}

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		config = new GlobalConfig();

		database = new Database("SkyClaims.db");
		islands = database.loadData();
		getLogger().info("ISLAND LENGTH: " + islands.keySet().size());

		registerCommands();
		// TODO - Load database data into memory
		getLogger().info("Initialization complete.");
	}

	@Listener
	public void onWorldSave(SaveWorldEvent.Post event) {
		if (event.isCancelled() || event.getTargetWorld().equals(game.getServer().getDefaultWorld().get())) {
			// TODO Save the database
		}
	}

	@Listener
	public void onServerStopped(GameStoppedServerEvent event) {
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
		CommandReset.register();
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

	public LuckPermsApi getLuckPerms() {
		return luckPerms;
	}

	public SpongeWorldEdit getSpongeWorldEdit() {
		return spongeWorldEdit;
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