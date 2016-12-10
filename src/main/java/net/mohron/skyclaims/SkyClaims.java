package net.mohron.skyclaims;

import com.google.inject.Inject;
import net.mohron.skyclaims.command.CommandCreate;
import net.mohron.skyclaims.command.CommandHelp;
import net.mohron.skyclaims.command.CommandIsland;
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


	public DataStore dataStore;

	public Logger getLogger() {
		return logger;
	}

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		instance = this;

		getLogger().info(NAME + " " + VERSION + " is initializing.");

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
		getLogger().info(NAME + " " + VERSION + " is stopping.");
		// TODO
		getLogger().info("Shutdown actions complete.");
	}

	public void registerCommands() {
		// island help
		CommandSpec help = CommandSpec.builder()
				.description(Text.of("Help"))
				.executor(new CommandHelp())
				.build();
		// island info

		// island create
		CommandSpec create = CommandSpec.builder()
				.permission(Permissions.COMMAND_CREATE)
				.description(Text.of("create"))
				.executor(new CommandCreate())
				.build();
		// island
		Sponge.getCommandManager().register(this, CommandSpec.builder()
				.description(Text.of("SkyClaims Island Command"))
				.child(help, "help")
				.child(create, "create")
				.executor(new CommandHelp())
				.build(), "skyclaims", "island", "is");
	}
}
