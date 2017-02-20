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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class CommandList implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final String HELP_TEXT = "display a list of the current islands";
	private static final Text USER = Text.of("user");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_LIST)
		.description(Text.of(HELP_TEXT))
		.arguments(GenericArguments.optionalWeak(GenericArguments.user(USER)))
		.executor(new CommandList())
		.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "islandlist");
			PLUGIN.getLogger().debug("Registered command: CommandList");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandList");
		}
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (SkyClaims.islands.isEmpty())
			src.sendMessage(Text.of(TextColors.RED, "There are currently no islands!"));
		List<Text> listText = Lists.newArrayList();
		Player player = (src instanceof Player) ? (Player) src : null;
		User user = args.<User>getOne(USER).orElse(null);

		boolean spawnOthers = src.hasPermission(Permissions.COMMAND_SPAWN_OTHERS);

		SkyClaims.islands.values().stream()
			.filter(i -> user == null || i.hasPermissions(user))
			.sorted(Comparator.comparing(Island::getName))
			.forEach(island -> listText.add(Text.of(
				getLocked(island),
				island.getName().toBuilder()
					.onHover(TextActions.showText(Text.of("Click here to view island info")))
					.onClick(TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "islandinfo", island.getUniqueId().toString(), createReturnConsumer(src)))),
				(!island.isLocked() || ((player == null || island.hasPermissions(player)) || spawnOthers)) ? getClickableCoords(src, island) : getCoords(island)
			)));

		if (listText.isEmpty())
			listText.add(Text.of(TextColors.RED, "There are no islands to display!"));

		if (!(src instanceof Player))
			listText.forEach(src::sendMessage);

		PaginationList.builder()
			.title(Text.of(TextColors.AQUA, "Island List"))
			.padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
			.contents(listText)
			.sendTo(src);

		return CommandResult.success();
	}

	private static Consumer<CommandSource> createReturnConsumer(CommandSource src) {
		return consumer -> {
			Text returnCommand = Text.builder().append(Text.of(
					Text.NEW_LINE, TextColors.WHITE, "[", TextColors.AQUA, "Return to Island List", TextColors.WHITE, "]", Text.NEW_LINE))
					.onClick(TextActions.executeCallback(CommandUtil.createCommandConsumer(src, "islandlist", "", null))).build();
			src.sendMessage(returnCommand);
		};
	}

	private Text getLocked(Island island) {
		return Text.of(TextColors.WHITE, " [",
			Text.builder(island.isLocked() ? "L" : "U")
				.color(island.isLocked() ? TextColors.RED : TextColors.GREEN)
				.onHover(TextActions.showText(island.isLocked()
					? Text.of(TextColors.RED, "LOCKED")
					: Text.of(TextColors.GREEN, "UNLOCKED")
				))
				.onClick(TextActions.executeCallback(toggleLock(island))),
			TextColors.WHITE, "] ");
	}

	private Consumer<CommandSource> toggleLock(Island island) {
		return src -> {
			if (src instanceof Player && ((Player) src).getUniqueId().equals(island.getOwnerUniqueId()) && src.hasPermission(Permissions.COMMAND_LOCK) || src.hasPermission(Permissions.COMMAND_LOCK_OTHERS)) {
				island.setLocked(!island.isLocked());
				src.sendMessage(Text.of(island.getName(), TextColors.GREEN, " is now ",
					Text.builder((island.isLocked()) ? "LOCKED" : "UNLOCKED")
						.color((island.isLocked()) ? TextColors.RED : TextColors.GREEN)
						.onClick(TextActions.executeCallback(toggleLock(island))),
					TextColors.GREEN, "!"
				));
			}
		};
	}

	private Text getCoords(Island island) {
		return Text.of(TextColors.GRAY, " (",
			TextColors.LIGHT_PURPLE, island.getRegion().getX(),
			TextColors.GRAY, ", ",
			TextColors.LIGHT_PURPLE, island.getRegion().getZ(),
			TextColors.GRAY, ")");
	}

	private Text getClickableCoords(CommandSource src, Island island) {
		return getCoords(island).toBuilder()
			.onHover(TextActions.showText(Text.of("Click here to teleport to this island.")))
			.onClick(TextActions.executeCallback(CommandUtil.createTeleportConsumer(src, island.getSpawn().getLocation())))
			.build();
	}
}