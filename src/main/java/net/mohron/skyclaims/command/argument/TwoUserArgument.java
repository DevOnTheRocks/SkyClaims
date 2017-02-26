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
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.GuavaCollectors;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TwoUserArgument extends CommandElement {
	private final Text key;
	private final Text key2;

	public TwoUserArgument(@Nullable Text key, Text key2) {
		super(key);
		this.key = key;
		this.key2 = key2;
	}

	@Nullable
	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		return null;
	}

	@Override
	public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
		String user1 = args.next();
		String user2 = args.nextIfPresent().orElse(null);
		if (user2 != null) {
			context.putArg(key, getUserFromName(user1));
			context.putArg(key2, getUserFromName(user2));
		} else {
			context.putArg(key2, getUserFromName(user1));
		}
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		try {
			String name = args.peek().toLowerCase();
			return SkyClaims.getInstance().getGame().getServiceManager().provideUnchecked(UserStorageService.class)
				.getAll().stream()
				.map(GameProfile::getName)
				.filter(Optional::isPresent)
				.filter(s -> s.get().startsWith(name))
				.map(Optional::get)
				.collect(GuavaCollectors.toImmutableList());
		} catch (ArgumentParseException e) {
			return Lists.newArrayList();
		}
	}

	private User getUserFromName(String name) throws ArgumentParseException {
		return SkyClaims.getInstance().getGame().getServiceManager().provideUnchecked(UserStorageService.class).get(name)
			.orElseThrow(() -> new ArgumentParseException(Text.of("Invalid User Supplied"), name, 0));
	}
}
