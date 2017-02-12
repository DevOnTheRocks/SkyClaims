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
import com.google.common.collect.Sets;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class IslandArgument extends CommandElement {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public IslandArgument(@Nullable Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		String arg = args.next().toLowerCase();
		if (SkyClaims.islands.isEmpty())
			throw new ArgumentParseException(Text.of(TextColors.RED, "There are no valid island!"), arg, 0);
		try {
			UUID uuid = UUID.fromString(arg);
			if (SkyClaims.islands.containsKey(uuid)) {
				Set<UUID> islands = Sets.newHashSet();
				islands.add(uuid);
				return islands;
			}
		} catch (IllegalArgumentException ignored) {
		}
		return SkyClaims.islands.entrySet().stream()
				.filter(i -> i.getValue().getOwnerName().equalsIgnoreCase(arg))
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		try {
			String arg = args.peek().toLowerCase();
			boolean admin = src.hasPermission(Permissions.COMMAND_LIST_ALL);
			return SkyClaims.islands.entrySet().stream()
					.filter(i -> i.getValue().getOwnerName().toLowerCase().startsWith(arg))
					.filter(i -> admin || !(src instanceof Player) || i.getValue().hasPermissions((Player) src))
					.map(m -> m.getValue().getOwnerName())
					.collect(Collectors.toList());
		} catch (ArgumentParseException e) {
			return Lists.newArrayList();
		}
	}
}
