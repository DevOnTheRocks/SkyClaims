/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims.command.user;

import com.google.common.collect.Lists;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.argument.Argument;
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

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class CommandInfo implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static final String HELP_TEXT = "display detailed information on your island";

	private static final Text ISLAND = Text.of("island");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_INFO)
		.description(Text.of(HELP_TEXT))
		.arguments(GenericArguments.optional(Argument.island(ISLAND)))
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
		List<Text> infoText = Lists.newArrayList();
		List<Island> islands = Lists.newArrayList();

		if (src instanceof Player && !args.hasAny(ISLAND)) {
			islands.add(Island.get(((Player) src).getLocation())
				.orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must be on an island to use this command.")))
			);
		} else {
			Collection<UUID> islandIds = args.getAll(ISLAND);
			islandIds.forEach(i -> Island.get(i).ifPresent(islands::add));
			if (islands.size() > 1) return listIslands();
		}

		islands.forEach(island -> {
			infoText.add(Text.of(
				(src instanceof Player) ? getAdminShortcuts(src, island) : Text.EMPTY,
				TextColors.YELLOW, "Name", TextColors.WHITE, " : ", island.getName(),
				getLocked(island, (src instanceof Player) && island.hasPermissions((Player) src)), Text.NEW_LINE,
				TextColors.YELLOW, "Owner", TextColors.WHITE, " : ", TextColors.GOLD, island.getOwnerName(), Text.NEW_LINE,
				TextColors.YELLOW, "Members", TextColors.WHITE, " : ", getMembers(island), Text.NEW_LINE,
				TextColors.YELLOW, "Size", TextColors.WHITE, " : ", TextColors.LIGHT_PURPLE, island.getWidth(),
				TextColors.GRAY, "x", TextColors.LIGHT_PURPLE, island.getWidth(), Text.NEW_LINE,
				TextColors.YELLOW, "Spawn", TextColors.WHITE, " : ", getSpawn(island), Text.NEW_LINE,
				TextColors.YELLOW, "Created", TextColors.WHITE, " : ", TextColors.GRAY, island.getDateCreated(), Text.NEW_LINE,
				TextColors.YELLOW, "UUID", TextColors.WHITE, " : ", TextColors.GRAY, island.getUniqueId(), Text.NEW_LINE,
				(island.getClaim().isPresent()) ? Text.of(
					TextColors.YELLOW, "Claim", TextColors.WHITE, " : ", TextColors.GRAY, Text.builder(
						island.getClaimUniqueId().toString())
						.onClick(TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "claiminfo",
							island.getClaimUniqueId().toString(),
							createReturnConsumer(src, island.getUniqueId().toString())
						)))
						.onHover(TextActions.showText(Text.of("Click here to check claim info.")))
				) : Text.EMPTY
			));
		});

		PaginationList.builder()
			.title(Text.of(TextColors.AQUA, "Island Info"))
			.padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
			.contents(infoText)
			.sendTo(src);

		return CommandResult.success();
	}

	private static CommandResult listIslands() throws CommandException {
		throw new CommandException(
			Text.of(TextColors.RED, "Command does not support players with multiple islands yet!"));
	}

	private static Text getAdminShortcuts(CommandSource src, Island island) {
		Text teleport = src.hasPermission(Permissions.COMMAND_SPAWN_OTHERS) ? Text.of(
			TextColors.DARK_GRAY, "[",
			TextColors.RED, Text.builder("Teleport")
				.onClick(TextActions.executeCallback(CommandUtil.createTeleportConsumer(src, island.getSpawn()
					.getLocation())))
				.onHover(TextActions.showText(Text.of("Click to teleport to this island!"))),
			TextColors.DARK_GRAY, "] "
		) : Text.EMPTY;

		Text transfer = src.hasPermission(Permissions.COMMAND_TRANSFER) ? Text.of(
			TextColors.DARK_GRAY, "[",
			TextColors.RED, Text.builder("Transfer")
				.onClick(TextActions.suggestCommand("/isa transfer " + island.getOwnerName() + " ?"))
				.onHover(TextActions.showText(Text.of("Click to transfer this island!"))),
			TextColors.DARK_GRAY, "] "
		) : Text.EMPTY;

		Text delete = src.hasPermission(Permissions.COMMAND_DELETE) ? Text.of(
			TextColors.DARK_GRAY, "[",
			TextColors.RED, Text.builder("Delete")
				.onClick(TextActions.executeCallback(consumer -> {
					island.clear();
					island.delete();
					src.sendMessage(Text.of(island.getOwnerName(), "'s island has been deleted!"));
				}))
				.onHover(TextActions.showText(Text.of("Click to delete this island!"))),
			TextColors.DARK_GRAY, "] "
		) : Text.EMPTY;

		Text expand = src.hasPermission(Permissions.COMMAND_EXPAND_OTHERS) ? Text.of(
			TextColors.DARK_GRAY, "[",
			TextColors.RED, Text.builder("Expand")
				.onClick(TextActions.executeCallback(consumer -> {
					island.expand(1);
					src.sendMessage(Text.of(
						island.getOwnerName(), "'s island has been expanded to ",
						TextColors.LIGHT_PURPLE, island.getWidth(), TextColors.RESET, "x", TextColors.LIGHT_PURPLE, island.getWidth(),
						TextColors.RESET,"!"
					));
				}))
				.onHover(TextActions.showText(Text.of("Click to expand this island's width by ", TextColors.LIGHT_PURPLE, 2, TextColors.RESET, "!"))),
			TextColors.DARK_GRAY, "] "
		) : Text.EMPTY;

		return  (teleport.isEmpty() && transfer.isEmpty() && delete.isEmpty() && expand.isEmpty()) ? Text.EMPTY : Text.of(
			TextColors.RED, "Admin", TextColors.WHITE, " : ",
			teleport, transfer, delete, expand, Text.NEW_LINE
		);
	}

	private Text getLocked(Island island, boolean perm) {
		if (perm) return Text.of(TextColors.WHITE, " [",(island.isLocked()) ?
				Text.builder("L")
					.color(TextColors.RED)
					.onHover(TextActions.showText(Text.of(TextColors.RED, "Locked", Text.NEW_LINE, TextColors.GRAY, "Click to toggle.")))
					.onClick(TextActions.executeCallback(src -> island.setLocked(!island.isLocked())))
				: Text.builder("U")
				.color(TextColors.GREEN)
				.onHover(TextActions.showText(Text.of(TextColors.GREEN, "Unlocked", Text.NEW_LINE, TextColors.GRAY, "Click to toggle.")))
				.onClick(TextActions.executeCallback(src -> island.setLocked(!island.isLocked()))),
			TextColors.WHITE, "] ");
		else return Text.of(TextColors.WHITE, " [",(island.isLocked()) ?
				Text.builder("L")
					.color(TextColors.RED)
					.onHover(TextActions.showText(Text.of(TextColors.RED, "Locked")))
				: Text.builder("U")
				.color(TextColors.GREEN)
				.onHover(TextActions.showText(Text.of(TextColors.GREEN, "Unlocked"))),
			TextColors.WHITE, "] ");
	}

	private static Text getMembers(Island island) {
		if (island.getMembers().isEmpty())
			return Text.of(TextColors.GRAY, "None");
		else {
			int i = 1;
			Text members = Text.EMPTY;
			for (String member : island.getMembers()) {
				members = Text.join(members, Text.of(
					TextColors.BLUE, member, TextColors.GRAY,
					(i == island.getMembers().size()) ? "" : ", ")
				);
				i++;
			}
			return members;
		}
	}

	private Text getSpawn(Island island) {
		return Text.of(
			TextColors.LIGHT_PURPLE, island.getSpawn().getLocation().getBlockX(), TextColors.GRAY, "x ",
			TextColors.LIGHT_PURPLE, island.getSpawn().getLocation().getBlockY(), TextColors.GRAY, "y ",
			TextColors.LIGHT_PURPLE, island.getSpawn().getLocation().getBlockZ(), TextColors.GRAY, "z"
		);
	}

	private static Consumer<CommandSource> createReturnConsumer(CommandSource src, String arguments) {
		return consumer -> {
			Text returnCommand = Text.builder()
				.append(Text.of(
					TextColors.WHITE, Text.NEW_LINE, "[", TextColors.AQUA, "Return to Island Info", TextColors.WHITE,
					"]", Text.NEW_LINE
				))
				.onClick(
					TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "islandinfo", arguments, null)))
				.build();
			src.sendMessage(returnCommand);
		};
	}
}