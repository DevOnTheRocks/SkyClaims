package net.mohron.skyclaims.util;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.claim.IClaim;
import net.mohron.skyclaims.lib.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.Consumer;

public class WorldUtil {
	public static World getDefaultWorld() {
		String defaultWorldName = SkyClaims.getInstance().getGame().getServer().getDefaultWorldName();
		return SkyClaims.getInstance().getGame().getServer().getWorld(defaultWorldName).get();
	}

	public static Consumer<CommandSource> createTeleportConsumer(CommandSource src, Location<World> location, IClaim claim) {
		return teleport -> {
			if (!(src instanceof Player)) {
				// ignore
				return;
			}
			Player player = (Player) src;
			// if not owner of claim, validate perms
			if (!player.getUniqueId().equals(claim.getOwnerUniqueId())) {
				if (!claim.getClaimData().getContainers().contains(player.getUniqueId())
						&& !claim.getClaimData().getBuilders().contains(player.getUniqueId())
						&& !claim.getClaimData().getManagers().contains(player.getUniqueId())
						&& !player.hasPermission(Permissions.COMMAND_SPAWN_OTHERS)) {
					player.sendMessage(Text.of(TextColors.RED, "You do not have permission to use the teleport feature in this claim."));
					return;
				}
			}

			Location<World> safeLocation = Sponge.getGame().getTeleportHelper().getSafeLocation(location).orElse(null);
			if (safeLocation == null) {
				player.sendMessage(
						Text.builder().append(Text.of(TextColors.RED, "Location is not safe. "),
								Text.builder().append(Text.of(TextColors.GREEN, "Are you sure you want to teleport here?")).onClick(TextActions.executeCallback(createForceTeleportConsumer(player, location))).style(TextStyles.UNDERLINE).build()).build());
			} else {
				player.setLocation(safeLocation);
			}
		};
	}

	public static Consumer<CommandSource> createForceTeleportConsumer(Player player, Location<World> location) {
		return teleport -> player.setLocation(location);
	}
}
