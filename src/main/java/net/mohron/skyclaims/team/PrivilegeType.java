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

package net.mohron.skyclaims.team;

import com.google.common.collect.Maps;
import com.griefdefender.api.claim.TrustType;
import com.griefdefender.api.claim.TrustTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Map;

public enum PrivilegeType {
    OWNER(Component.text("Owner", NamedTextColor.BLUE), TrustTypes.NONE),
    MANAGER(Component.text("Manager", NamedTextColor.GOLD), TrustTypes.MANAGER),
    MEMBER(Component.text("Member", NamedTextColor.YELLOW), TrustTypes.BUILDER),
    NONE(Component.text("None", NamedTextColor.GRAY), TrustTypes.NONE);

    private final Component text;
    private final TrustType trustType;

    PrivilegeType(Component text, TrustType trustType) {
        this.text = text;
        this.trustType = trustType;
    }

    public Component format(Component component) {
        return component.color(component.color()).hoverEvent(text.asHoverEvent());
    }

    public Component format(String string) {
        return format(Component.text(string));
    }

    public Component toText() {
        return text;
    }

    public TrustType getTrustType() {
        return trustType;
    }

    public static CommandElement getCommandArgument(Component key) {
        Map<String, PrivilegeType> typeMap = Maps.newHashMap();
        for (PrivilegeType type : values()) {
            if (type != NONE) {
                typeMap.put(type.toString().toLowerCase(), type);
            }
        }
        return GenericArguments.choices(key, typeMap);
    }

    public boolean greaterThanOrEqualTo(PrivilegeType other) {
        return this.ordinal() <= other.ordinal();
    }
}
