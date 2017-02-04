package net.mohron.skyclaims.command.user;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.argument.BiomeArgument;
import net.mohron.skyclaims.config.type.PermissionConfig;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.util.WorldUtil;
import net.mohron.skyclaims.world.Island;
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

public class CommandSetBiome implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static PermissionConfig config = PLUGIN.getConfig().getPermissionConfig();

	public static final String HELP_TEXT = "set the biome of a block, chunk or island";

	private static final Text BIOME = Text.of("biome");
	private static final Text TARGET = Text.of("target");

	public enum Target {
		BLOCK, CHUNK, ISLAND;
	}

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_SET_BIOME)
			.description(Text.of(HELP_TEXT))
			.arguments(GenericArguments.seq(
					new BiomeArgument(BIOME),
					GenericArguments.optional(GenericArguments.enumValue(TARGET, Target.class))
			))
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
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of("You must be a player to run this command!"));
		}

		Player player = (Player) src;
		BiomeType biome = (BiomeType) args.getOne(BIOME)
				.orElseThrow(() -> new CommandException(Text.of("You must supply a biome to use this command")));
		Island island = Island.get(player.getLocation())
				.orElseThrow(() -> new CommandException(Text.of("You must be on an island to use this command")));

		if (!player.getUniqueId().equals(island.getOwnerUniqueId()) && !player.hasPermission(Permissions.COMMAND_SET_BIOME_OTHERS))
			throw new CommandException(Text.of("You do not have permission to use setbiome on this island"));

		if (config.isSeparateTargetPerms() && !player.hasPermission(Permissions.COMMAND_ARGUMENTS_BIOMES + "." + biome.getName().toLowerCase()))
			throw new CommandPermissionException(Text.of("You do not have permission to use the designated biome type."));

		Target target = (Target) args.getOne(TARGET).orElse(Target.ISLAND);

		switch (target) {
			case BLOCK:
				if (!player.hasPermission(Permissions.COMMAND_ARGUMENTS_BLOCK))
					throw new CommandPermissionException(Text.of("You do not have permissions to use setbiome <biome> block"));
				WorldUtil.setBlockBiome(player.getLocation(), biome);
				src.sendMessage(Text.of(TextColors.GREEN, String.format("Successfully changed the biome at %s,%s to %s.", player.getLocation().getBlockX(), player.getLocation().getBlockZ(), biome.getName())));
				break;
			case CHUNK:
				if (!player.hasPermission(Permissions.COMMAND_ARGUMENTS_CHUNK))
					throw new CommandPermissionException(Text.of("You do not have permissions to use setbiome <biome> chunk"));
				WorldUtil.setChunkBiome(player.getLocation(), biome);
				src.sendMessage(Text.of(TextColors.GREEN, String.format("Successfully changed the biome in this chunk to %s.", biome.getName())));
				break;
			case ISLAND:
				if (!player.hasPermission(Permissions.COMMAND_ARGUMENTS_ISLAND))
					throw new CommandPermissionException(Text.of("You do not have permissions to use setbiome <biome> island"));
				WorldUtil.setIslandBiome(island, biome);
				src.sendMessage(Text.of(TextColors.GREEN, String.format("Successfully changed the biome on this island to %s.", biome.getName())));
				break;
		}

		return CommandResult.success();
	}
}