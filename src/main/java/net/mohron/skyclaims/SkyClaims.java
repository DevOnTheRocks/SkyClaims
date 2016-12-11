package net.mohron.skyclaims;

import com.google.inject.Inject;
import net.mohron.skyclaims.command.CommandCreate;
import net.mohron.skyclaims.command.CommandHelp;
import net.mohron.skyclaims.command.CommandIsland;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;

import static net.mohron.skyclaims.PluginInfo.ID;
import static net.mohron.skyclaims.PluginInfo.NAME;
import static net.mohron.skyclaims.PluginInfo.VERSION;
import static net.mohron.skyclaims.PluginInfo.DESCRIPTION;
import static net.mohron.skyclaims.PluginInfo.AUTHORS;

@Plugin(id = ID,
		name = NAME,
		version = VERSION,
		description = DESCRIPTION,
		authors = {AUTHORS},
		dependencies = {
				@Dependency(id = "griefprevention", optional = true),
				@Dependency(id = "worldedit", optional = true)
		})
public class SkyClaims {
	public static SkyClaims instance;
	@Inject private Logger logger;
	@Inject private Game game;

	public DataStore dataStore;

	public Logger getLogger() {
		return logger;
	}

	public Game getGame() { return game; }

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

		registerCommands();
		// TODO
		getLogger().info("Initialization complete.");
	}

	@Listener
	public void onServerStopped(GameStoppedServerEvent event) {
		getLogger().info(String.format("%S %S is stopping...", NAME, VERSION));
		// TODO
		getLogger().info("Shutdown actions complete.");
	}

	private void registerCommands() {
		CommandCreate.register();
		CommandHelp.register();
		CommandIsland.register();
	}
}
