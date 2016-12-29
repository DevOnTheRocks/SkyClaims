package net.mohron.skyclaims.command;


import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandIsland implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = String.format("use to run %s's subcommands or display command help info.", PluginInfo.NAME);

	private static CommandSpec commandSpec = CommandSpec.builder()
			.description(Text.of(helpText))
			.child(CommandAdmin.commandSpec, "admin")
			.child(CommandCreate.commandSpec, "create")
			.child(CommandHelp.commandSpec, "help")
			.child(CommandInfo.commandSpec, "info")
			.child(CommandReset.commandSpec, "reset")
			.child(CommandSetBiome.commandSpec, "setbiome")
			.child(CommandSetSpawn.commandSpec, "setspawn")
			.child(CommandSpawn.commandSpec, "spawn", "tp")
			.executor(new CommandHelp())
			.build();

	public static void register() {
		try {
			Sponge.getCommandManager().register(PLUGIN, commandSpec, "skyclaims", "island", "is");
			PLUGIN.getLogger().info("Registered command: CommandIsland");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandIsland");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		// Not used, CommandIsland is used exclusively as a parent command. Runs /is help when not supplied with a subcommand.
		return CommandResult.empty();
	}

}