package net.mohron.skyclaims.command;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.lib.Arguments;
import net.mohron.skyclaims.lib.Permissions;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.util.IslandUtil;
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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;
import java.util.UUID;

public class CommandInfo implements CommandExecutor {

	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static String helpText = "display detailed information on your island";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_INFO)
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(GenericArguments.user(Arguments.USER)))
			.executor(new CommandInfo())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandInfo");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandInfo");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player) && !args.hasAny(Text.of("player"))) {
			throw new CommandException(Text.of(TextColors.RED, "You must supply a player to use this command."));
		} else {
			if (args.hasAny(Arguments.USER)) {
				if (!src.hasPermission(Permissions.COMMAND_INFO_OTHERS))
					throw new CommandPermissionException(Text.of(TextColors.RED, "You do not have permission to use this command!"));
			}
			User user = (args.getOne(Arguments.USER).isPresent()) ? (User) args.getOne(Arguments.USER).get() : (User) src;
			Optional<Island> islandOptional = IslandUtil.getIslandByOwner(user.getUniqueId());
			String name = (user.getName().equalsIgnoreCase(src.getName())) ? user.getName() : "You";

			if (!islandOptional.isPresent())
				throw new CommandException(Text.of(TextColors.RED, name, " must have an island to use this command!"));

			Island island = islandOptional.get();
			Text members = Text.of(TextColors.YELLOW, "Members", TextColors.WHITE, " : ");
			if (island.getMembers().isEmpty())
				members = members.concat(Text.of(TextColors.GRAY, "none"));
			else {
				int i = 1;
				for (UUID member : island.getMembers()) {
					members = Text.join(members, Text.of(TextColors.AQUA, member.toString(), TextColors.GRAY, (i == island.getMembers().size())? "" :", "));
					i++;
				}
			}

			Text infoText = Text.of(
					TextColors.YELLOW, "UUID", TextColors.WHITE, " : ", TextColors.GRAY, island.getUniqueId(), "\n",
					TextColors.YELLOW, "Name", TextColors.WHITE, " : ", TextColors.AQUA, island.getName(), "\n",
					TextColors.YELLOW, "Owner", TextColors.WHITE, " : ", TextColors.GRAY, island.getOwnerName(), "\n",
					TextColors.YELLOW, "Size", TextColors.WHITE, " : ", TextColors.LIGHT_PURPLE, island.getRadius() * 2, TextColors.GRAY, "x", TextColors.LIGHT_PURPLE, island.getRadius() * 2, "\n",
					TextColors.YELLOW, "Spawn", TextColors.WHITE, " : ", TextColors.LIGHT_PURPLE, island.getSpawn().getBlockX(), TextColors.GRAY, "x, ",
					TextColors.LIGHT_PURPLE, island.getSpawn().getBlockY(), TextColors.GRAY, "y, ", TextColors.LIGHT_PURPLE, island.getSpawn().getBlockZ(), TextColors.GRAY, "z", "\n",
					TextColors.YELLOW, "Claim", TextColors.WHITE, " : ", TextColors.GRAY, Text.builder(island.getClaim().getOwnerUniqueId().toString())
							.onClick(TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "claiminfo", island.getClaim().getUniqueId().toString(), CommandUtil.createReturnIslandInfoConsumer(src, ""))))
							.onHover(TextActions.showText(Text.of("Click here to check claim info."))), "\n",
					members, "\n",
					TextColors.YELLOW, "Created", TextColors.WHITE, " : ", TextColors.GRAY, island.getDateCreated()
			);

			PaginationList.Builder paginationBuilder = PaginationList.builder().title(Text.of(TextColors.AQUA, "Island Info")).padding(Text.of(TextColors.AQUA, "-")).contents(infoText);
			paginationBuilder.sendTo(src);

			return CommandResult.success();
		}
	}
}