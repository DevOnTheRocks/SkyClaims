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

package net.mohron.skyclaims.team;

import com.google.common.collect.Maps;
import java.util.Map;
import me.ryanhamshire.griefprevention.api.claim.TrustType;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public enum PrivilegeType {
  OWNER(Text.of(TextColors.BLUE, "Owner"), TrustType.NONE),
  MANAGER(Text.of(TextColors.GOLD, "Manager"), TrustType.MANAGER),
  MEMBER(Text.of(TextColors.YELLOW, "Member"), TrustType.BUILDER),
  NONE(Text.of(TextColors.GRAY, "None"), TrustType.NONE);

  private Text text;
  private TrustType trustType;

  PrivilegeType(Text text, TrustType trustType) {
    this.text = text;
    this.trustType = trustType;
  }

  public Text format(Text text) {
    return format(text.toPlain());
  }

  public Text format(String string) {
    return Text.builder(string)
        .color(text.getColor())
        .onHover(TextActions.showText(text))
        .build();
  }

  public Text toText() {
    return text;
  }

  public TrustType getTrustType() {
    return trustType;
  }

  public static CommandElement getCommandArgument(Text key) {
    Map<String, PrivilegeType> typeMap = Maps.newHashMap();
    for (PrivilegeType type : values()) {
      if (type != NONE) {
        typeMap.put(type.toString().toLowerCase(), type);
      }
    }
    return GenericArguments.choices(key, typeMap);
  }
}
