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
import java.util.List;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PositiveIntegerArgument extends CommandElement {

  private final boolean allowZero;

  public PositiveIntegerArgument(@Nullable Text key) {
    this(key, false);
  }

  public PositiveIntegerArgument(@Nullable Text key, boolean allowZero) {
    super(key);
    this.allowZero = allowZero;
  }

  @Nullable
  @Override
  protected Object parseValue(CommandSource source, CommandArgs args)
      throws ArgumentParseException {
    String i = args.next();
    try {
      int a = Integer.parseUnsignedInt(i);
      if (allowZero || a != 0) {
        return a;
      }

      throw new ArgumentParseException(Text.of(TextColors.RED, "Zero is not a valid input!"), i, 0);
    } catch (NumberFormatException e) {
      throw new ArgumentParseException(Text.of(TextColors.RED, "A positive integer is required!"),
          i, 0);
    }
  }

  @Override
  public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    return Lists.newArrayList();
  }
}
