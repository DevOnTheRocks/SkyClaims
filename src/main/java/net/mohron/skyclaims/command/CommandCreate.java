package net.mohron.skyclaims.command;

import net.mohron.skyclaims.Permissions;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.util.IslandUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CommandCreate implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "create an island.";

	private static Map<String, File> schematics = new HashMap<>();

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_CREATE)
			.description(Text.of(helpText))
			.arguments(GenericArguments.choices(Text.of("schematic"), schematics))
			.executor(new CommandCreate())
			.build();

	static {
		for (String schem : PLUGIN.getConfig().schematics) {
			String path = String.format("%s\\%s", PLUGIN.getConfigDir(), schem);
			if (Files.exists(Paths.get(path)))
				schematics.put(schem, new File(path));
		}
	}

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().info("Registered command: CommandCreate");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandCreate");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Text.of("You must be a player to run this command!"));

		Player player = (Player) src;
		File schematic = new File(String.format("%s\\Island.schem", PLUGIN.getConfigDir()));

		if (IslandUtil.hasIsland(player.getUniqueId()))
			throw new CommandException(Text.of("You already have an island!"));

		if (args.getOne(Text.of("schematic")).isPresent())
			schematic = (File) args.getOne(Text.of("schematic")).get();

		player.sendMessage(Text.of("Your Island is being created. You will be teleported shortly."));
		Island island = IslandUtil.createIsland(player.getUniqueId(), schematic);

		return CommandResult.success();
	}
}