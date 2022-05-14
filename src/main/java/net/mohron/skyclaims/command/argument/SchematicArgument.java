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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.schematic.IslandSchematic;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SchematicArgument extends CommandElement {

  private static final SkyClaims PLUGIN = SkyClaims.getInstance();

  public SchematicArgument(@Nullable Text key) {
    super(key);
  }

  @Nullable
  @Override
  protected IslandSchematic parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    String schem = args.next().toLowerCase();
    if (PLUGIN.getSchematicManager().getSchematics().isEmpty()) {
      throw args.createError(LinearComponents.linear(NamedTextColor.RED, "There are no valid schematics available!"));
    }
    Optional<IslandSchematic> schematic = PLUGIN.getSchematicManager().getSchematics().stream().filter(s -> s.getName().equalsIgnoreCase(schem)).findAny();
    if (schematic.isPresent()) {
      if (PLUGIN.getConfig().getPermissionConfig().isSeparateSchematicPerms() && !hasPermission(source, schem)) {
        throw args.createError(LinearComponents.linear(NamedTextColor.RED, "You do not have permission to use the supplied schematic!"));
      }
      return schematic.get();
    }
    throw args.createError(LinearComponents.linear(NamedTextColor.RED, "Invalid Schematic!"));
  }

  @Override
  public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    try {
      String name = args.peek().toLowerCase();
      boolean checkPerms = PLUGIN.getConfig().getPermissionConfig().isSeparateSchematicPerms();
      return PLUGIN.getSchematicManager().getSchematics().stream()
          .filter(s -> s.getName().startsWith(name))
          .filter(s -> !checkPerms || hasPermission(src, s.getName()))
          .map(IslandSchematic::getName)
          .collect(Collectors.toList());
    } catch (ArgumentParseException e) {
      return Lists.newArrayList();
    }
  }

  private boolean hasPermission(CommandSource src, String name) {
    return src.hasPermission(Permissions.COMMAND_ARGUMENTS_SCHEMATICS + "." + name.toLowerCase());
  }
} 