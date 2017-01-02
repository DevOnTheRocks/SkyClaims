package net.mohron.skyclaims.util;

import me.ryanhamshire.griefprevention.claim.Claim;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.lib.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.function.Consumer;

public class WorldUtil {
	public static World getDefaultWorld() {
		String defaultWorldName = SkyClaims.getInstance().getGame().getServer().getDefaultWorldName();
		return SkyClaims.getInstance().getGame().getServer().getWorld(defaultWorldName).get();
	}

	public static void setBlockBiome(Location<World> location, BiomeType biomeType) {
		location.getExtent().setBiome(
				location.getBlockX(),
				0,
				location.getBlockZ(),
				biomeType);
	}

	public static void setChunkBiome(Location<World> location, BiomeType biomeType) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				location.getExtent().setBiome(
						location.getChunkPosition().getX() * 16 + x,
						0,
						location.getChunkPosition().getZ() * 16 + z,
						biomeType);
			}
		}
	}

	public static void setIslandBiome(Island island, BiomeType biomeType) {
		int x1 = island.getClaim().getLesserBoundaryCorner().getBlockX();
		int x2 = island.getClaim().getGreaterBoundaryCorner().getBlockX();
		int z1 = island.getClaim().getLesserBoundaryCorner().getBlockZ();
		int z2 = island.getClaim().getGreaterBoundaryCorner().getBlockZ();
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				island.getWorld().setBiome(
						x,
						0,
						z,
						biomeType);
			}
		}
	}

	public static void setRegionBiome(int rx, int rz, BiomeType biomeType) {
		World world = ConfigUtil.getWorld();
		for (int x = 0; x < 512; x++) {
			for (int z = 0; z < 512; z++) {
				world.setBiome(
						rx * 512 + x,
						0,
						rz * 512 + z,
						biomeType);
			}
		}
	}
}
