package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.lib.Arguments;
import net.mohron.skyclaims.lib.Permissions;
import net.mohron.skyclaims.util.IslandUtil;
import net.mohron.skyclaims.util.WorldUtil;
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
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.Optional;

public class CommandSetBiome implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "set the biome of a block, chunk or island";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_SET_BIOME)
			.description(Text.of(helpText))
			.arguments(
					GenericArguments.choices(Arguments.BIOME, Arguments.BIOMES),
					GenericArguments.optional(GenericArguments.enumValue(Arguments.TARGET, Arguments.Target.class)))
			.executor(new CommandSetBiome())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "setbiome");
			PLUGIN.getLogger().debug("Registered command: CommandSetBiome");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandSetBiome");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Text.of("You must be a player to run this command!"));

		Player player = (Player) src;

		Optional<BiomeType> biomeOptional = args.getOne(Arguments.BIOME);
		if (!biomeOptional.isPresent())
			throw new CommandException(Text.of("You must supply a biome to use this command"));
		BiomeType biome = biomeOptional.get();

		Optional<Island> island = IslandUtil.getIslandByLocation(player.getLocation());
		if (!island.isPresent())
			throw new CommandException(Text.of("You must be on an island to use this command"));

		if (!player.getUniqueId().equals(island.get().getOwner()) && !player.hasPermission(Permissions.COMMAND_SET_BIOME_OTHERS))
			throw new CommandException(Text.of("You do not have permission to use setbiome on this island"));

		if (!player.hasPermission(Permissions.COMMAND_SET_BIOME_BIOMES + "." + biome.getName().toLowerCase()))
			throw new CommandPermissionException(Text.of("You do not have permission to use the designated biome type."));

		Arguments.Target target = Arguments.Target.CHUNK;
		if (args.getOne(Arguments.TARGET).isPresent()) target = (Arguments.Target) args.getOne(Arguments.TARGET).get();

		switch (target) {
			case BLOCK:
				if (!player.hasPermission(Permissions.COMMAND_SET_BIOME_BLOCK))
					throw new CommandPermissionException(Text.of("You do not have permissions to use setbiome <biome> block"));
				PLUGIN.getLogger().info("SETBIOME: BLOCK");
				WorldUtil.setBlockBiome(player.getLocation(), biome);
				src.sendMessage(Text.of(TextColors.GREEN, String.format("Successfully changed the biome at %s,%s to %s.", player.getLocation().getBlockX(), player.getLocation().getBlockZ(), biome.getName())));
				break;
			case CHUNK:
				if (!player.hasPermission(Permissions.COMMAND_SET_BIOME_CHUNK))
					throw new CommandPermissionException(Text.of("You do not have permissions to use setbiome <biome> chunk"));
				PLUGIN.getLogger().info("SETBIOME: CHUNK");
				WorldUtil.setChunkBiome(player.getLocation(), biome);
				src.sendMessage(Text.of(TextColors.GREEN, String.format("Successfully changed the biome in this chunk to %s.", biome.getName())));
				break;
			case ISLAND:
				if (!player.hasPermission(Permissions.COMMAND_SET_BIOME_ISLAND))
					throw new CommandPermissionException(Text.of("You do not have permissions to use setbiome <biome> island"));
				PLUGIN.getLogger().info("SETBIOME: ISLAND");
				WorldUtil.setIslandBiome(island.get(), biome);
				src.sendMessage(Text.of(TextColors.GREEN, String.format("Successfully changed the biome on this island to %s.", biome.getName())));
				break;
		}

		return CommandResult.success();
	}
}