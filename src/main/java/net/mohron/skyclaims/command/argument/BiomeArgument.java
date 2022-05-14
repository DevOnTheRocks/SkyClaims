/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2022 Mohron
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import org.apache.commons.lang3.text.StrBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.biome.BiomeType;

public class BiomeArgument extends CommandElement {

  public static final List<BiomeType> BIOMES;
  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  static {
    BIOMES = Lists.newArrayList(Sponge.getRegistry().getAllOf(BiomeType.class));
    BIOMES.sort(Comparator.comparing(BiomeType::getId));
    // Output to log
    if (PLUGIN.getConfig().getMiscConfig().isLogBiomes()) {
      StrBuilder biomeDebug = new StrBuilder("SkyClaims Biome Permissions:").appendNewLine().append("Biome Name | Biome ID | Biome Permission");
      BIOMES.forEach(b -> biomeDebug.appendNewLine().append(b.getName()).append(" | ").append(b.getId()).append(" | ").append(getPermission(b)));
      PLUGIN.getLogger().info(biomeDebug.build());
    }
  }

  public BiomeArgument(@Nullable Text key) {
    super(key);
  }

  @Nullable
  @Override
  protected Object parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException {
    String arg = args.next().toLowerCase();
    boolean checkPerms = PLUGIN.getConfig().getPermissionConfig().isSeparateBiomePerms();
    List<BiomeType> biomeList = BIOMES.stream()
        .filter(b -> b.getId().startsWith(arg) || b.getId().startsWith("minecraft:" + arg))
        .collect(Collectors.toList());

    if (!biomeList.isEmpty()) {
      if (biomeList.size() == 1) {
        if (checkPerms && !src.hasPermission(getPermission(biomeList.get(0)))) {
          throw args.createError(LinearComponents.linear(NamedTextColor.RED, "You do not have permission to use the supplied biome type."));
        }
        return biomeList.get(0);
      } else {
        return biomeList.stream()
            .filter(b -> (b.getId().equalsIgnoreCase(arg) || b.getId().equalsIgnoreCase("minecraft:" + arg))
                && (!checkPerms || src.hasPermission(getPermission(b))))
            .findAny()
            .orElseThrow(() -> args.createError(LinearComponents.linear(NamedTextColor.RED, "More that one biome found for ", arg, ".")));
      }
    }
    throw args.createError(LinearComponents.linear(NamedTextColor.RED, "Invalid biome type."));
  }

  @Override
  public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    try {
      String name = args.peek().toLowerCase();
      boolean checkPerms = PLUGIN.getConfig().getPermissionConfig().isSeparateBiomePerms();
      return BIOMES.stream()
          .filter(b -> b.getId().startsWith(name) || b.getName().startsWith(name))
          .filter(b -> !checkPerms || src.hasPermission(getPermission(b)))
          .map(b -> b.getId().replace("minecraft:", ""))
          .collect(Collectors.toList());
    } catch (ArgumentParseException e) {
      return Lists.newArrayList();
    }
  }

  private static String getPermission(BiomeType biome) {
    return String.format("%s.%s", Permissions.COMMAND_ARGUMENTS_BIOMES, biome.getId().replace(':', '.'));
  }
}