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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

@NonnullByDefault
public class BiomeArgument extends CommandElement {

  public static final Map<String, BiomeType> BIOMES = Maps.newHashMap();
  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  static {
    BIOMES.putAll(Sponge.getRegistry().getAllOf(BiomeType.class).stream().collect(Collectors.toMap(BiomeArgument::getArgument, b -> b)));
  }

  public BiomeArgument(@Nullable Text key) {
    super(key);
  }

  private static String getArgument(BiomeType biomeType) {
    return biomeType.getName().replaceAll(" ", "_").replaceAll("[+]", "_plus").toLowerCase();
  }

  @Nullable
  @Override
  protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    String arg = args.next().toLowerCase();
    if (BIOMES.containsKey(arg)) {
      BiomeType biomeType = BIOMES.get(arg);
      if (!PLUGIN.getConfig().getPermissionConfig().isSeparateBiomePerms() || !hasPermission(source, getArgument(biomeType))) {
        throw args.createError(Text.of(TextColors.RED, "You do not have permission to use the supplied biome type."));
      }
      return biomeType;
    }
    throw args.createError(Text.of(TextColors.RED, "Invalid biome type."));
  }

  @Override
  public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    try {
      String name = args.peek().toLowerCase();
      boolean checkPerms = PLUGIN.getConfig().getPermissionConfig().isSeparateBiomePerms();
      return BIOMES.keySet().stream()
          .filter(s -> s.startsWith(name))
          .filter(s -> !checkPerms || hasPermission(src, s))
          .collect(Collectors.toList());
    } catch (ArgumentParseException e) {
      return Lists.newArrayList();
    }
  }

  private boolean hasPermission(CommandSource src, String biomeType) {
    return src.hasPermission(Permissions.COMMAND_ARGUMENTS_BIOMES + "." + biomeType);
  }
}