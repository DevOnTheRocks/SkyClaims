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

package net.mohron.skyclaims.util;

import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.Consumer;

public class CommandUtil {
	public static Consumer<CommandSource> createTeleportConsumer(CommandSource src, Location<World> location) {
		return teleport -> {
			if (!(src instanceof Player)) return;
			Player player = (Player) src;
			Location<World> safeLocation = SkyClaims.getInstance().getGame().getTeleportHelper().getSafeLocation(location).orElse(null);
			if (safeLocation == null) {
				player.sendMessage(Text.of(
					TextColors.RED, "Location is not safe. ",
					Text.of(TextColors.GREEN, "Are you sure you want to teleport here?").toBuilder()
						.onClick(TextActions.executeCallback(createForceTeleportConsumer(player, location)))
						.style(TextStyles.UNDERLINE)
				));
			} else {
				player.setLocation(safeLocation);
			}
		};
	}

	public static Consumer<Task> createTeleportConsumer(Player player, Location<World> location) {
		return teleport -> {
			Location<World> safeLocation = SkyClaims.getInstance().getGame().getTeleportHelper().getSafeLocation(location).orElse(null);
			if (safeLocation == null) {
				player.sendMessage(Text.of(
					TextColors.RED, "Location is not safe. ",
					Text.of(TextColors.GREEN, "Are you sure you want to teleport here?").toBuilder()
						.onClick(TextActions.executeCallback(createForceTeleportConsumer(player, location)))
						.style(TextStyles.UNDERLINE)
				));
			} else {
				player.setLocation(safeLocation);
			}
		};
	}

	private static Consumer<CommandSource> createForceTeleportConsumer(Player player, Location<World> location) {
		return teleport -> player.setLocation(location);
	}

	public static Consumer<CommandSource> createCommandConsumer(CommandSource src, String command, String arguments, Consumer<CommandSource> postConsumerTask) {
		return consumer -> {
			try {
				Sponge.getCommandManager().get(command).get().getCallable().process(src, arguments);
			} catch (CommandException e) {
				src.sendMessage(e.getText());
			}
			if (postConsumerTask != null) {
				postConsumerTask.accept(src);
			}
		};
	}
}