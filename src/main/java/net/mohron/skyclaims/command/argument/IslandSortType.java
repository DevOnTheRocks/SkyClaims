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

import java.util.Comparator;
import java.util.function.Function;
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.data.key.Keys;

public enum IslandSortType {
  NONE(Order.ASC),
  NAME(Order.ASC),
  CREATED(Order.ASC),
  ONLINE(Order.DESC),
  ACTIVE(Order.DESC),
  MEMBERS(Order.DESC),
  SIZE(Order.DESC),
  ENTITIES(Order.DESC),
  TILES(Order.DESC);

  private final Order order;

  IslandSortType(Order order) {
    this.order = order;
  }

  public enum Order {
    ASC, DESC;

    public Comparator getComparator() {
      switch (this) {
        case DESC:
          return Comparator.reverseOrder();
        case ASC:
        default:
          return Comparator.naturalOrder();
      }
    }
  }

  public Order getOrder() {
    return order;
  }

  public Function<Island, ? extends Comparable> getSortFunction() {
    switch (this) {
      case CREATED:
        return Island::getDateCreated;
      case ONLINE:
        return i -> i.getMembers().stream()
            .anyMatch(user -> user.isOnline() && !user.get(Keys.VANISH).orElse(false));
      case ACTIVE:
        return Island::getDateLastActive;
      case MEMBERS:
        return Island::getTotalMembers;
      case SIZE:
        return Island::getWidth;
      case ENTITIES:
        return Island::getTotalEntities;
      case TILES:
        return Island::getTotalTileEntities;
      case NAME:
      case NONE:
      default:
        return Island::getSortableName;
    }
  }
}
