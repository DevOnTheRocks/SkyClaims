package net.mohron.skyclaims.command;

import net.mohron.skyclaims.Permissions;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.util.IslandUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandSetBiome implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "set the biome of a block, chunk or island";

	private static Map<String, BiomeType> biomes = new HashMap<>();
	private static Map<String, Target> targets = new HashMap<>();

	static {
		biomes.put("forest", BiomeTypes.FOREST);
		biomes.put("plains", BiomeTypes.PLAINS);

		targets.put("-b", Target.BLOCK);
		targets.put("-c", Target.CHUNK);
		targets.put("-i", Target.ISLAND);
	}

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_SET_BIOME)
			.description(Text.of(helpText))
			.arguments(GenericArguments.choices(Text.of("biome"), biomes), GenericArguments.optional(GenericArguments.choices(Text.of("target"), targets)))
			.executor(new CommandSetBiome())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "setbiome");
			PLUGIN.getLogger().info("Registered command: CommandSetBiome");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandSetBiome");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Text.of("You must be a player to run this command!"));

		Player player = (Player) src;
		Optional<Island> island = IslandUtil.getIslandByLocation(player.getLocation());

		Optional<BiomeType> biomeOptional = args.getOne(Text.of("biome"));
		Target target = Target.CHUNK;

		if (!biomeOptional.isPresent())
			throw new CommandException(Text.of("You must supply a biome to use this command"));
		BiomeType biome = biomeOptional.get();
		if (!player.hasPermission(Permissions.COMMAND_SET_BIOME_BIOMES + "." + biome.getName().toLowerCase()))
			throw new CommandPermissionException(Text.of("You do not have permission to use the designated biome type."));

		if (args.getOne(Text.of("target")).isPresent()) target = (Target) args.getOne(Text.of("target")).get();
		switch (target) {
			case BLOCK:
				if (!player.hasPermission(Permissions.COMMAND_SET_BIOME_BLOCK)) throw new CommandPermissionException();
				PLUGIN.getLogger().info("SETBIOME: BLOCK");
				break;
			case CHUNK:
				if (!player.hasPermission(Permissions.COMMAND_SET_BIOME_CHUNK)) throw new CommandPermissionException();
				PLUGIN.getLogger().info("SETBIOME: CHUNK");
				break;
			case ISLAND:
				if (!player.hasPermission(Permissions.COMMAND_SET_BIOME_ISLAND)) throw new CommandPermissionException();
				PLUGIN.getLogger().info("SETBIOME: ISLAND");
				break;
		}


		return CommandResult.success();
	}

	private enum Target {
		BLOCK, CHUNK, ISLAND;
	}
}