package net.mohron.skyclaims.command;

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
	public static void register() {
		try {
			Sponge.getCommandManager().register(SkyClaims.getInstance(), CommandSpec.builder()
					.description(Text.of("SkyClaims Island Command"))
					.child(CommandHelp.commandSpec, "help")
					.child(CommandCreate.commandSpec, "create")
					.executor(new CommandHelp())
					.build(), "skyclaims", "island", "is");

		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			SkyClaims.getInstance().getLogger().error("Failed to register command: CommandIsland");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		return CommandResult.success();
	}

}
