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
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CommandInfo {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "display detailed information on your island";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_INFO)
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(GenericArguments.user(Text.of("player"))))
			.executor(new CommandCreate())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().info("Registered command: CommandInfo");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandInfo");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player) && !args.hasAny(Text.of("player"))) {
			throw new CommandException(Text.of(TextColors.RED, "You must supply a player to use this command."));
		} else {
			if (args.hasAny(Text.of("player"))) {
				if (!src.hasPermission(Permissions.COMMAND_INFO_OTHERS))
					throw new CommandPermissionException(Text.of(TextColors.RED, "You do not have permission to use this command!"));
			}
			User user = (args.getOne(Text.of("player")).isPresent()) ? (User) args.getOne(Text.of("player")).get() : (User) src;
			Optional<Island> islandOptional = IslandUtil.getIsland(user.getUniqueId());

			if (!islandOptional.isPresent())
				throw new CommandException(Text.of(TextColors.RED, "You must have an Island to run this command!"));

			Island island = islandOptional.get();
			Text infoText = Text.of(
					TextColors.YELLOW, "Owner", TextColors.WHITE, " : ", TextColors.GRAY, island.getOwnerName(), "\n",
					TextColors.YELLOW, "Size", TextColors.WHITE, " : ", TextColors.GRAY, island.getRadius() * 2, "x", island.getRadius() * 2, "\n",
					TextColors.YELLOW, "Spawn", TextColors.WHITE, " : ", TextColors.LIGHT_PURPLE, island.getSpawn().getBlockX(), TextColors.GRAY, " x, ", TextColors.LIGHT_PURPLE, island.getSpawn().getBlockY(), TextColors.GRAY, " y, ",TextColors.LIGHT_PURPLE, island.getSpawn().getBlockZ(), TextColors.GRAY, " z", "\n",
					TextColors.YELLOW, "Claim", TextColors.WHITE, " : ", TextColors.GRAY, island.getClaim().getID()
			);

			PaginationList.Builder paginationBuilder = PaginationList.builder().title(Text.of(TextColors.AQUA, "Island Info")).padding(Text.of("-")).contents(infoText);
			paginationBuilder.sendTo(src);

			return CommandResult.success();
		}
	}
}
