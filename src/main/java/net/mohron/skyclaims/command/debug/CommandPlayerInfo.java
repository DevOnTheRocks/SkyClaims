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

package net.mohron.skyclaims.command.debug;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.schematic.IslandSchematic;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class CommandPlayerInfo extends CommandBase {

  public static final String HELP_TEXT = "display info about a player as SkyClaims sees it.";
  private static final Text USER = LinearComponents.linear("user");

  public static void register() {
    CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_PLAYER_INFO)
        .description(LinearComponents.linear(HELP_TEXT))
        .arguments(GenericArguments.optional(GenericArguments.user(USER)))
        .executor(new CommandPlayerInfo())
        .build();

    try {
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec, "scplayerinfo");
      PLUGIN.getLogger().debug("Registered command: CommandPlayerInfo");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandPlayerInfo", e);
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!args.hasAny(USER) && !(src instanceof Player)) {
      throw  new CommandException(LinearComponents.linear(NamedTextColor.RED, "You must provide a player argument to use this command!"));
    }
    User user = args.<User>getOne(USER).orElse((User) src);

    PaginationList.builder()
        .title(LinearComponents.linear(NamedTextColor.AQUA, user.getName(), "'s SkyClaims Info"))
        .padding(LinearComponents.linear(NamedTextColor.AQUA, TextStyles.STRIKETHROUGH, "-"))
        .contents(getUserInfo(user))
        .sendTo(src);

    return CommandResult.success();
  }

  private List<Text> getUserInfo(User user) {
    final UUID uuid = user.getUniqueId();
    final List<Text> options = Lists.newArrayList();
    final Text defaultSchematic = Options.getDefaultSchematic(uuid).map(IslandSchematic::getText).orElse(LinearComponents.linear("none"));
    final String defaultBiome = Options.getDefaultBiome(uuid).map(CatalogType::getName).orElse("none");

    options.add(getOptionText(Options.DEFAULT_SCHEMATIC, "Default Schematic", defaultSchematic));
    options.add(getOptionText(Options.DEFAULT_BIOME, "Default Biome", defaultBiome));
    options.add(getOptionText(Options.MIN_SIZE, "Minimum Size", Options.getMinSize(uuid) * 2));
    options.add(getOptionText(Options.MAX_SIZE, "Maximum Size", Options.getMaxSize(uuid) * 2));
    options.add(getOptionText(Options.MAX_SPAWNS, "Maximum Entity Spawns", Options.getMaxSpawns(uuid)));
    options.add(getOptionText(Options.MAX_HOSTILE, "Maximum Hostile Spawns", Options.getMaxHostileSpawns(uuid)));
    options.add(getOptionText(Options.MAX_PASSIVE, "Maximum Passive Spawns", Options.getMaxPassiveSpawns(uuid)));
    options.add(getOptionText(Options.EXPIRATION, "Island Expiration (days)", Options.getExpiration(uuid)));
    options.add(getOptionText(Options.MAX_ISLANDS, "Maximum Islands", Options.getMaxIslands(uuid)));
    options.add(getOptionText(Options.MAX_TEAMMATES, "Maximum Teammates", Options.getMaxTeammates(uuid)));

    return options;
  }

  private Text getOptionText(String option, String name, Object value) {
    TextColor valueColor = value instanceof Integer ? NamedTextColor.LIGHT_PURPLE : NamedTextColor.GRAY;
    return LinearComponents.linear(
        Text.builder(name).color(NamedTextColor.YELLOW).onHover(TextActions.showText(LinearComponents.linear(option))),
        NamedTextColor.WHITE, " : ",
        valueColor, value
    );
  }
}
