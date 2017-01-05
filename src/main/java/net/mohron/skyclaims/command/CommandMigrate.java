package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.lib.Arguments;
import net.mohron.skyclaims.lib.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

/**
 * Created by Slind on 1/5/2017.
 */
public class CommandMigrate implements CommandExecutor {
    private static final SkyClaims PLUGIN = SkyClaims.getInstance();

    public static String helpText = "used to migrate the database.";

    public static CommandSpec commandSpec = CommandSpec.builder()
            .permission(Permissions.COMMAND_MIGRATE)
            .description(Text.of(helpText))
            .arguments(GenericArguments.user(Arguments.VERSION))
            .executor(new CommandMigrate())
            .build();

    public static void register() {
        try {
            PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
            PLUGIN.getLogger().debug("Registered command: CommandMigrate");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            PLUGIN.getLogger().error("Failed to register command: CommandMigrate");
        }
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> user = args.getOne(Arguments.VERSION);
        if (user.isPresent() && user.get().equals("1")) {
            for (Island island : SkyClaims.islands.values()) {
                island.migrate();
            }
            src.sendMessage(Text.of(TextColors.GREEN, "Successfully migrated database from data version 1."));
        }
        return CommandResult.success();
    }
}
