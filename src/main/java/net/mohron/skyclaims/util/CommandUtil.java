package net.mohron.skyclaims.util;

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
			Location<World> safeLocation = Sponge.getGame().getTeleportHelper().getSafeLocation(location).orElse(null);
			if (safeLocation == null) {
				player.sendMessage(
						Text.builder().append(Text.of(TextColors.RED, "Location is not safe. "),
								Text.builder().append(
										Text.of(TextColors.GREEN, "Are you sure you want to teleport here?"))
										.onClick(TextActions.executeCallback(createForceTeleportConsumer(player, location)))
										.style(TextStyles.UNDERLINE).build()
						).build());
			} else {
				player.setLocation(safeLocation);
			}
		};
	}

	public static Consumer<Task> createTeleportConsumer(Player player, Location<World> location) {
		return teleport -> {
			Location<World> safeLocation = Sponge.getGame().getTeleportHelper().getSafeLocation(location).orElse(null);
			if (safeLocation == null) {
				player.sendMessage(
						Text.builder().append(Text.of(TextColors.RED, "Location is not safe. "),
								Text.builder().append(
										Text.of(TextColors.GREEN, "Are you sure you want to teleport here?"))
										.onClick(TextActions.executeCallback(createForceTeleportConsumer(player, location)))
										.style(TextStyles.UNDERLINE).build()
						).build());
			} else {
				player.setLocation(safeLocation);
			}
		};
	}

	public static Consumer<CommandSource> createForceTeleportConsumer(Player player, Location<World> location) {
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

	public static Consumer<CommandSource> createReturnIslandInfoConsumer(CommandSource src, String arguments) {
		return consumer -> {
			Text claimListReturnCommand = Text.builder().append(Text.of(
					TextColors.WHITE, "\n[", TextColors.AQUA, "Return to island info", TextColors.WHITE, "]\n"))
					.onClick(TextActions.executeCallback(createCommandConsumer(src, "is info", arguments, null))).build();
			src.sendMessage(claimListReturnCommand);
		};
	}
}