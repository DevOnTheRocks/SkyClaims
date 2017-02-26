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

package net.mohron.skyclaims.command.argument;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.PermissionConfig;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BiomeArgument extends CommandElement {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final Map<String, BiomeType> BIOMES = Maps.newHashMap();

	static {
		// Standard Biomes
		BIOMES.put(getArgument(BiomeTypes.BEACH), BiomeTypes.BEACH);
		BIOMES.put(getArgument(BiomeTypes.BIRCH_FOREST), BiomeTypes.BIRCH_FOREST);
		BIOMES.put(getArgument(BiomeTypes.BIRCH_FOREST_HILLS), BiomeTypes.BIRCH_FOREST_HILLS);
		BIOMES.put(getArgument(BiomeTypes.COLD_BEACH), BiomeTypes.COLD_BEACH);
		BIOMES.put(getArgument(BiomeTypes.COLD_TAIGA), BiomeTypes.COLD_TAIGA);
		BIOMES.put(getArgument(BiomeTypes.COLD_TAIGA_HILLS), BiomeTypes.COLD_TAIGA_HILLS);
		BIOMES.put(getArgument(BiomeTypes.DEEP_OCEAN), BiomeTypes.DEEP_OCEAN);
		BIOMES.put(getArgument(BiomeTypes.DESERT), BiomeTypes.DESERT);
		BIOMES.put(getArgument(BiomeTypes.DESERT_HILLS), BiomeTypes.DESERT_HILLS);
		BIOMES.put(getArgument(BiomeTypes.EXTREME_HILLS), BiomeTypes.EXTREME_HILLS);
		BIOMES.put(getArgument(BiomeTypes.EXTREME_HILLS_EDGE), BiomeTypes.EXTREME_HILLS_EDGE);
		BIOMES.put(getArgument(BiomeTypes.EXTREME_HILLS_PLUS), BiomeTypes.EXTREME_HILLS_PLUS);
		BIOMES.put(getArgument(BiomeTypes.FOREST), BiomeTypes.FOREST);
		BIOMES.put(getArgument(BiomeTypes.FOREST_HILLS), BiomeTypes.FOREST_HILLS);
		BIOMES.put(getArgument(BiomeTypes.FROZEN_OCEAN), BiomeTypes.FROZEN_OCEAN);
		BIOMES.put(getArgument(BiomeTypes.FROZEN_RIVER), BiomeTypes.FROZEN_RIVER);
		BIOMES.put(getArgument(BiomeTypes.HELL), BiomeTypes.HELL);
		BIOMES.put(getArgument(BiomeTypes.ICE_MOUNTAINS), BiomeTypes.ICE_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.ICE_PLAINS), BiomeTypes.ICE_PLAINS);
		BIOMES.put(getArgument(BiomeTypes.JUNGLE), BiomeTypes.JUNGLE);
		BIOMES.put(getArgument(BiomeTypes.JUNGLE_EDGE), BiomeTypes.JUNGLE_EDGE);
		BIOMES.put(getArgument(BiomeTypes.JUNGLE_HILLS), BiomeTypes.JUNGLE_HILLS);
		BIOMES.put(getArgument(BiomeTypes.MEGA_TAIGA), BiomeTypes.MEGA_TAIGA);
		BIOMES.put(getArgument(BiomeTypes.MEGA_TAIGA_HILLS), BiomeTypes.MEGA_TAIGA_HILLS);
		BIOMES.put(getArgument(BiomeTypes.MESA), BiomeTypes.MESA);
		BIOMES.put(getArgument(BiomeTypes.MESA_PLATEAU), BiomeTypes.MESA_PLATEAU);
		BIOMES.put(getArgument(BiomeTypes.MESA_PLATEAU_FOREST), BiomeTypes.MESA_PLATEAU_FOREST);
		BIOMES.put(getArgument(BiomeTypes.MUSHROOM_ISLAND), BiomeTypes.MUSHROOM_ISLAND);
		BIOMES.put(getArgument(BiomeTypes.MUSHROOM_ISLAND_SHORE), BiomeTypes.MUSHROOM_ISLAND_SHORE);
		BIOMES.put(getArgument(BiomeTypes.OCEAN), BiomeTypes.OCEAN);
		BIOMES.put(getArgument(BiomeTypes.PLAINS), BiomeTypes.PLAINS);
		BIOMES.put(getArgument(BiomeTypes.RIVER), BiomeTypes.RIVER);
		BIOMES.put(getArgument(BiomeTypes.ROOFED_FOREST), BiomeTypes.ROOFED_FOREST);
		BIOMES.put(getArgument(BiomeTypes.SAVANNA), BiomeTypes.SAVANNA);
		BIOMES.put(getArgument(BiomeTypes.SAVANNA_PLATEAU), BiomeTypes.SAVANNA_PLATEAU);
		BIOMES.put(getArgument(BiomeTypes.SKY), BiomeTypes.SKY);
		BIOMES.put(getArgument(BiomeTypes.STONE_BEACH), BiomeTypes.STONE_BEACH);
		BIOMES.put(getArgument(BiomeTypes.SWAMPLAND), BiomeTypes.SWAMPLAND);
		BIOMES.put(getArgument(BiomeTypes.TAIGA), BiomeTypes.TAIGA);
		BIOMES.put(getArgument(BiomeTypes.TAIGA_HILLS), BiomeTypes.TAIGA_HILLS);
		// Mutated Biomes
		BIOMES.put(getArgument(BiomeTypes.BIRCH_FOREST_HILLS_MOUNTAINS), BiomeTypes.BIRCH_FOREST_HILLS_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.BIRCH_FOREST_MOUNTAINS), BiomeTypes.BIRCH_FOREST_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.COLD_TAIGA_MOUNTAINS), BiomeTypes.COLD_TAIGA_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.DESERT_MOUNTAINS), BiomeTypes.DESERT_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.EXTREME_HILLS_MOUNTAINS), BiomeTypes.EXTREME_HILLS_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.EXTREME_HILLS_PLUS_MOUNTAINS), BiomeTypes.EXTREME_HILLS_PLUS_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.FLOWER_FOREST), BiomeTypes.FLOWER_FOREST);
		BIOMES.put(getArgument(BiomeTypes.ICE_PLAINS_SPIKES), BiomeTypes.ICE_PLAINS_SPIKES);
		BIOMES.put(getArgument(BiomeTypes.JUNGLE_EDGE_MOUNTAINS), BiomeTypes.JUNGLE_EDGE_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.JUNGLE_MOUNTAINS), BiomeTypes.JUNGLE_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.MEGA_SPRUCE_TAIGA), BiomeTypes.MEGA_SPRUCE_TAIGA);
		BIOMES.put(getArgument(BiomeTypes.MEGA_SPRUCE_TAIGA_HILLS), BiomeTypes.MEGA_SPRUCE_TAIGA_HILLS);
		BIOMES.put(getArgument(BiomeTypes.MESA_BRYCE), BiomeTypes.MESA_BRYCE);
		BIOMES.put(getArgument(BiomeTypes.MESA_PLATEAU_FOREST_MOUNTAINS), BiomeTypes.MESA_PLATEAU_FOREST_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.MESA_PLATEAU_MOUNTAINS), BiomeTypes.MESA_PLATEAU_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.ROOFED_FOREST_MOUNTAINS), BiomeTypes.ROOFED_FOREST_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.SAVANNA_MOUNTAINS), BiomeTypes.SAVANNA_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.SAVANNA_PLATEAU_MOUNTAINS), BiomeTypes.SAVANNA_PLATEAU_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.SUNFLOWER_PLAINS), BiomeTypes.SUNFLOWER_PLAINS);
		BIOMES.put(getArgument(BiomeTypes.SWAMPLAND_MOUNTAINS), BiomeTypes.SWAMPLAND_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.TAIGA_MOUNTAINS), BiomeTypes.TAIGA_MOUNTAINS);
		BIOMES.put(getArgument(BiomeTypes.VOID), BiomeTypes.VOID);
	}

	public BiomeArgument(@Nullable Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		String arg = args.next().toLowerCase();
		if (BIOMES.containsKey(arg)) {
			BiomeType biomeType = BIOMES.get(arg);
			if (!hasPermission(source, getArgument(biomeType)))
				throw new ArgumentParseException(Text.of(TextColors.RED, "You do not have permission to use the supplied biome type."), arg, 0);
			return biomeType;
		}
		throw new ArgumentParseException(Text.of(TextColors.RED, "Invalid biome type."), arg, 0);
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		try {
			String name = args.peek().toLowerCase();
			return BIOMES.keySet().stream()
					.filter(s -> s.startsWith(name))
					.filter(s -> hasPermission(src, s))
					.collect(Collectors.toList());
		} catch (ArgumentParseException e) {
			return Lists.newArrayList();
		}
	}

	private static String getArgument(BiomeType biomeType) {
		return biomeType.getName().replaceAll(" ", "_").replaceAll("[+]", "_plus").toLowerCase();
	}

	private boolean hasPermission(CommandSource src, String biomeType) {
		boolean checkPerms = PLUGIN.getConfig().getPermissionConfig().isSeparateBiomePerms();
		return !checkPerms || src.hasPermission(Permissions.COMMAND_ARGUMENTS_BIOMES + "." + biomeType);
	}
}