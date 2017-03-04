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
import net.mohron.skyclaims.world.Island;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortArgument extends CommandElement {
	public static final Map<String, Comparator<Island>> SORT_TYPES = Maps.newHashMap();

	static {
		SORT_TYPES.put("ascending", Comparator.comparing(Island::getSortableName));
		SORT_TYPES.put("descending", Comparator.comparing(Island::getSortableName).reversed());
		SORT_TYPES.put("oldest", Comparator.comparing(Island::getDateCreated));
		SORT_TYPES.put("newest", Comparator.comparing(Island::getDateCreated).reversed());
		SORT_TYPES.put("inactive", Comparator.comparing(Island::getDateLastActive));
		SORT_TYPES.put("active", Comparator.comparing(Island::getDateLastActive).reversed());
		SORT_TYPES.put("team-", Comparator.comparing(Island::getTotalMembers));
		SORT_TYPES.put("team+", Comparator.comparing(Island::getTotalMembers).reversed());
		SORT_TYPES.put("smallest", Comparator.comparing(Island::getWidth));
		SORT_TYPES.put("largest", Comparator.comparing(Island::getWidth).reversed());
	}

	public SortArgument(@Nullable Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		String arg = args.next().toLowerCase();
		if (SORT_TYPES.containsKey(arg)){
			return SORT_TYPES.get(arg);
		}
		throw new ArgumentParseException(Text.of(TextColors.RED, "Invalid sort type."), arg, 0);
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		try {
			String name = args.peek().toLowerCase();
			return SORT_TYPES.keySet().stream()
				.filter(s -> s.startsWith(name))
				.collect(Collectors.toList());
		} catch (ArgumentParseException e) {
			return Lists.newArrayList();
		}
	}
}
