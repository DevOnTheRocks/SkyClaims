package net.mohron.skyclaims.command.user;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.util.CommandUtil;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.UUID;
import java.util.function.Consumer;

public class CommandInfo implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static final String HELP_TEXT = "display detailed information on your island";

	private static final Text UUID_ARG = Text.of("uuid");

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_INFO)
			.description(Text.of(HELP_TEXT))
			.arguments(GenericArguments.optional(GenericArguments.string(UUID_ARG)))
			.executor(new CommandInfo())
			.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "islandinfo");
			PLUGIN.getLogger().debug("Registered command: CommandInfo");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandInfo");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Island island;
		if (src instanceof Player && !args.hasAny(UUID_ARG)) {
			island = Island.get(((Player) src).getLocation())
					.orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must be on an island to use this command.")));
		} else {
			UUID uuid;
			try {
				uuid = UUID.fromString((String) args.getOne(UUID_ARG).orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must supply an island uuid to use this command."))));
			} catch (IllegalArgumentException e) {
				throw new CommandException(Text.of(TextColors.RED, "The island id supplied is not a valid UUID_ARG."));
			}

			island = Island.get(uuid)
					.orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "The UUID_ARG supplied does not have a corresponding island.")));
		}

		Text members = Text.of(TextColors.YELLOW, "Members", TextColors.WHITE, " : ");
		if (island.getMembers().isEmpty())
			members = members.concat(Text.of(TextColors.GRAY, "None"));
		else {
			int i = 1;
			for (String member : island.getMembers()) {
				members = Text.join(members, Text.of(TextColors.BLUE, member, TextColors.GRAY, (i == island.getMembers().size()) ? "" : ", "));
				i++;
			}
		}

		Text teleport = (src.hasPermission(Permissions.COMMAND_SPAWN_OTHERS)) ? Text.of(
				TextColors.DARK_GRAY, "[",
				TextColors.RED, Text.builder("Teleport")
						.onClick(TextActions.executeCallback(CommandUtil.createTeleportConsumer(src, island.getSpawn().getLocation())))
						.onHover(TextActions.showText(Text.of("Click to teleport to this island!"))),
				TextColors.DARK_GRAY, "] "
		) : Text.EMPTY;

		Text transfer = (src.hasPermission(Permissions.COMMAND_TRANSFER)) ? Text.of(
				TextColors.DARK_GRAY, "[",
				TextColors.RED, Text.builder("Transfer")
						.onClick(TextActions.suggestCommand("/isa transfer " + island.getOwnerName() + " ?"))
						.onHover(TextActions.showText(Text.of("Click to transfer this island!"))),
				TextColors.DARK_GRAY, "] "
		) : Text.EMPTY;

		Text delete = (src.hasPermission(Permissions.COMMAND_DELETE)) ? Text.of(
				TextColors.DARK_GRAY, "[",
				TextColors.RED, Text.builder("Delete")
						.onClick(TextActions.executeCallback(consumer -> {
							island.regen();
							island.delete();
							src.sendMessage(Text.of(island.getOwnerName(), "'s island has been deleted!"));
						}))
						.onHover(TextActions.showText(Text.of("Click to delete this island!"))),
				TextColors.DARK_GRAY, "] "
		) : Text.EMPTY;

		Text admin = (teleport.isEmpty() && transfer.isEmpty() && delete.isEmpty()) ? Text.EMPTY : Text.of(
				TextColors.RED, "Admin", TextColors.WHITE, " : ",
				(!teleport.isEmpty()) ? teleport : Text.EMPTY,
				(!transfer.isEmpty()) ? transfer : Text.EMPTY,
				(!delete.isEmpty()) ? delete : Text.EMPTY,
				Text.NEW_LINE
		);

		Text infoText = Text.of(
				(src instanceof Player) ? admin : Text.EMPTY,
				TextColors.YELLOW, "Name", TextColors.WHITE, " : ", TextColors.AQUA, island.getName(), Text.NEW_LINE,
				TextColors.YELLOW, "Owner", TextColors.WHITE, " : ", TextColors.GOLD, island.getOwnerName(), Text.NEW_LINE,
				members, Text.NEW_LINE,
				TextColors.YELLOW, "Size", TextColors.WHITE, " : ", TextColors.LIGHT_PURPLE, island.getRadius() * 2, TextColors.GRAY, "x", TextColors.LIGHT_PURPLE, island.getRadius() * 2, Text.NEW_LINE,
				TextColors.YELLOW, "Spawn", TextColors.WHITE, " : ", TextColors.LIGHT_PURPLE, island.getSpawn().getLocation().getBlockX(), TextColors.GRAY, "x ",
				TextColors.LIGHT_PURPLE, island.getSpawn().getLocation().getBlockY(), TextColors.GRAY, "y ", TextColors.LIGHT_PURPLE, island.getSpawn().getLocation().getBlockZ(), TextColors.GRAY, "z", Text.NEW_LINE,
				TextColors.YELLOW, "Created", TextColors.WHITE, " : ", TextColors.GRAY, island.getDateCreated(), Text.NEW_LINE,
				TextColors.YELLOW, "UUID", TextColors.WHITE, " : ", TextColors.GRAY, island.getUniqueId(), Text.NEW_LINE,
				(island.getClaim().isPresent()) ? Text.of(
						TextColors.YELLOW, "Claim", TextColors.WHITE, " : ", TextColors.GRAY, Text.builder(island.getClaimUniqueId().toString())
								.onClick(TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "claiminfo", island.getClaimUniqueId().toString(), createReturnConsumer(src, island.getUniqueId().toString()))))
								.onHover(TextActions.showText(Text.of("Click here to check claim info.")))
				) : Text.EMPTY
		);

		PaginationList.Builder paginationBuilder = PaginationList.builder()
				.title(Text.of(TextColors.AQUA, "Island Info"))
				.padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
				.contents(infoText);
		paginationBuilder.sendTo(src);

		return CommandResult.success();
	}

	private static Consumer<CommandSource> createReturnConsumer(CommandSource src, String arguments) {
		return consumer -> {
			Text returnCommand = Text.builder().append(Text.of(
					TextColors.WHITE, Text.NEW_LINE, "[", TextColors.AQUA, "Return to Island Info", TextColors.WHITE, "]", Text.NEW_LINE))
					.onClick(TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "islandinfo", arguments, null))).build();
			src.sendMessage(returnCommand);
		};
	}
}