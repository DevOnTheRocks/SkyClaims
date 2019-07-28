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

package net.mohron.skyclaims.command;

import static net.mohron.skyclaims.PluginInfo.NAME;
import static net.mohron.skyclaims.PluginInfo.VERSION;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.mohron.skyclaims.command.admin.CommandReload;
import net.mohron.skyclaims.command.admin.CommandTransfer;
import net.mohron.skyclaims.command.argument.IslandSortType;
import net.mohron.skyclaims.command.schematic.CommandSchematic;
import net.mohron.skyclaims.command.team.CommandDemote;
import net.mohron.skyclaims.command.team.CommandInvite;
import net.mohron.skyclaims.command.team.CommandKick;
import net.mohron.skyclaims.command.team.CommandLeave;
import net.mohron.skyclaims.command.team.CommandPromote;
import net.mohron.skyclaims.command.user.CommandCreate;
import net.mohron.skyclaims.command.user.CommandDelete;
import net.mohron.skyclaims.command.user.CommandEntityInfo;
import net.mohron.skyclaims.command.user.CommandExpand;
import net.mohron.skyclaims.command.user.CommandInfo;
import net.mohron.skyclaims.command.user.CommandList;
import net.mohron.skyclaims.command.user.CommandLock;
import net.mohron.skyclaims.command.user.CommandReset;
import net.mohron.skyclaims.command.user.CommandSetBiome;
import net.mohron.skyclaims.command.user.CommandSetName;
import net.mohron.skyclaims.command.user.CommandSetSpawn;
import net.mohron.skyclaims.command.user.CommandSpawn;
import net.mohron.skyclaims.command.user.CommandUnlock;
import net.mohron.skyclaims.integration.nucleus.CommandHome;
import net.mohron.skyclaims.integration.nucleus.CommandSetHome;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.team.PrivilegeType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class CommandIsland extends CommandBase {

  private static final String HELP_TEXT = "use to run SkyClaim's subcommands or display command help info.";

  private static final Text HELP = Text.of("help");
  private static final Map<List<String>, CommandCallable> children = Maps.newHashMap();

  public static void register() {
    registerSubCommands();

    CommandSpec commandSpec = CommandSpec.builder()
        .description(Text.of(HELP_TEXT))
        .children(children)
        .childArgumentParseExceptionFallback(false)
        .arguments(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.literal(HELP, "help"))))
        .executor(new CommandIsland())
        .build();

    try {
      Sponge.getCommandManager().register(PLUGIN, commandSpec, PLUGIN.getConfig().getCommandConfig().getBaseAlias());
      PLUGIN.getLogger().debug("Registered command: CommandIsland");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandIsland", e);
    }
  }

  public static void addSubCommand(CommandCallable child, String... aliases) {
    children.put(ImmutableList.copyOf(aliases), child);
  }

  public static void clearSubCommands() {
    children.clear();
  }

  private static void registerSubCommands() {
    CommandCreate.register();
    CommandDelete.register();
    CommandEntityInfo.register();
    CommandDemote.register();
    CommandExpand.register();
    CommandInfo.register();
    CommandInvite.register();
    CommandKick.register();
    CommandLeave.register();
    CommandList.register();
    CommandLock.register();
    CommandPromote.register();
    CommandReload.register();
    CommandReset.register();
    CommandSchematic.register();
    CommandSetBiome.register();
    CommandSetName.register();
    CommandSetSpawn.register();
    CommandSpawn.register();
    CommandTransfer.register();
    CommandUnlock.register();
  }

  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    List<Text> helpText = Lists.newArrayList();
    String alias = PLUGIN.getConfig().getCommandConfig().getBaseAlias().get(0) + " ";

    URL gpHelp = null;
    try {
      gpHelp = new URL("http://bit.ly/mcgpuser");
    } catch (MalformedURLException ignored) {
    }
    Text gpInfo = Text.of(
        TextColors.WHITE,
        "SkyClaims uses GriefPrevention to provide island protection. Learn more at ",
        Text.builder("http://bit.ly/mcgpuser")
            .color(TextColors.YELLOW)
            .onHover(TextActions.showText(Text.of("Click to open")))
            .onClick(gpHelp != null ? TextActions.openUrl(gpHelp) : null),
        TextColors.WHITE, "."
    );

    if (src.hasPermission(Permissions.COMMAND_CREATE)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "create").onClick(TextActions.runCommand("/" + alias + "create")),
          TextColors.GRAY, " [schematic]",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandCreate.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_ENTITY_INFO)) {
      helpText.add(Text.of(
          TextColors.AQUA, Text.builder(alias + "entity").onClick(TextActions.runCommand("/" + alias + "entity")),
          TextColors.GRAY, " [island]",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandEntityInfo.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_EXPAND)) {
      helpText.add(Text.of(
          TextColors.AQUA, Text.builder(alias + "expand")
              .onClick(TextActions.suggestCommand("/" + alias + "expand ")),
          TextColors.GRAY, " [island]",
          TextColors.GOLD, " <blocks>",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandExpand.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_DELETE)) {
      helpText.add(Text.of(
          TextColors.AQUA, Text.builder(alias + "delete")
              .onClick(TextActions.suggestCommand("/" + alias + "delete")),
          TextColors.GRAY, " [island]",
          TextColors.GRAY,
          src.hasPermission(Permissions.COMMAND_DELETE_OTHERS) ? " [clear]" : Text.EMPTY,
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandDelete.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_DEMOTE)) {
      helpText.add(Text.of(
          TextColors.AQUA, Text.builder(alias + "demote")
              .onClick(TextActions.suggestCommand("/" + alias + "demote ")),
          TextColors.GOLD, " <user>",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandDemote.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_HOME)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "home").onClick(TextActions.runCommand("/" + alias + "home")),
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandHome.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_INFO)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "info").onClick(TextActions.runCommand("/" + alias + "info")),
          TextColors.GRAY, " [island]",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandInfo.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_INVITE)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "invite").onClick(TextActions.runCommand("/" + alias + "invite")),
          TextColors.GRAY, " [user]",
          TextColors.GRAY, Text.builder(" [privilege]")
              .onHover(TextActions.showText(getTextFromEnum(PrivilegeType.class))),
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandInvite.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_KICK)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "kick").onClick(TextActions.suggestCommand("/" + alias + "kick")),
          TextColors.GOLD, " <user>",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandKick.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_LEAVE)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "leave").onClick(TextActions.runCommand("/" + alias + "leave")),
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandLeave.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_LIST)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "list").onClick(TextActions.runCommand("/" + alias + "list")),
          TextColors.GRAY, " [island]",
          TextColors.GRAY, Text.builder(" [sort type]")
              .onHover(TextActions.showText(getTextFromEnum(IslandSortType.class))),
          TextColors.GRAY, Text.builder(" [sort order]")
              .onHover(TextActions.showText(getTextFromEnum(IslandSortType.Order.class))),
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandList.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_LOCK)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "lock").onClick(TextActions.runCommand("/" + alias + "lock")),
          TextColors.GRAY,
          src.hasPermission(Permissions.COMMAND_LOCK_OTHERS) ? " [island|all]" : Text.EMPTY,
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandLock.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_PROMOTE)) {
      helpText.add(Text.of(
          TextColors.AQUA, Text.builder(alias + "promote")
              .onClick(TextActions.suggestCommand("/" + alias + "promote ")),
          TextColors.GOLD, " <user>",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandPromote.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_RELOAD)) {
      helpText.add(Text.of(
          TextColors.AQUA, Text.of(TextActions.runCommand("/" + alias + "reload"), alias, "reload"),
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandReload.HELP_TEXT));
    }

    if (src.hasPermission(Permissions.COMMAND_RESET)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "reset").onClick(TextActions.suggestCommand("/" + alias + "reset")),
          TextColors.GRAY, " [schematic]",
          TextColors.GRAY,
          src.hasPermission(Permissions.COMMAND_RESET_KEEP_INV) ? " [keepinv]" : Text.EMPTY,
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandReset.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_SCHEMATIC)) {
      helpText.add(Text.of(
          TextColors.AQUA, alias, "schematic",
          TextColors.GRAY, Text.builder(" [sub command]")
              .onHover(TextActions.showText(Text.of("list, command, create, delete, info, setbiome, sethieght, seticon, setname, setpreset"))),
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandSchematic.HELP_TEXT));
    }

    if (src.hasPermission(Permissions.COMMAND_SET_BIOME)) {
      helpText.add(Text.of(
          TextColors.AQUA, Text.builder(alias + "setbiome")
              .onClick(TextActions.suggestCommand("/" + alias + "setbiome ")),
          TextColors.GOLD, " <biome>",
          TextColors.GRAY, " [target]",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandSetBiome.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_SET_NAME)) {
      helpText.add(Text.of(
          TextColors.AQUA, Text.builder(alias + "setname")
              .onClick(TextActions.suggestCommand("/" + alias + "setname ")),
          TextColors.GRAY, " [name]",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandSetName.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_SET_HOME)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "sethome").onClick(TextActions.runCommand("/" + alias + "sethome")),
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandSetHome.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_SET_SPAWN)) {
      helpText.add(Text.of(
          TextColors.AQUA, Text.builder(alias + "setspawn")
              .onClick(TextActions.runCommand("/" + alias + "setspawn")),
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandSetSpawn.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_SPAWN)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "spawn").onClick(TextActions.runCommand("/" + alias + "spawn")),
          TextColors.GRAY, " [player]",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandSpawn.HELP_TEXT
      ));
    }

    if (src.hasPermission(Permissions.COMMAND_TRANSFER)) {
      helpText.add(Text.of(
          TextColors.AQUA, alias, "transfer",
          TextColors.GRAY, " [owner]",
          TextColors.GOLD, " <player>",
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandTransfer.HELP_TEXT));
    }

    if (src.hasPermission(Permissions.COMMAND_LOCK)) {
      helpText.add(Text.of(
          TextColors.AQUA,
          Text.builder(alias + "unlock").onClick(TextActions.runCommand("/" + alias + "unlock")),
          TextColors.GRAY,
          src.hasPermission(Permissions.COMMAND_LOCK_OTHERS) ? " [island|all]" : Text.EMPTY,
          TextColors.DARK_GRAY, " - ",
          TextColors.DARK_GREEN, CommandUnlock.HELP_TEXT
      ));
    }

    if (helpText.isEmpty()) {
      src.sendMessage(Text.of(TextColors.AQUA, NAME, " ", VERSION));
    } else {
      if (src instanceof Player) {
        PaginationList.builder()
            .title(Text.of(TextColors.AQUA, NAME, " Help"))
            .padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
            .header(gpInfo)
            .contents(helpText)
            .sendTo(src);
      } else {
        helpText.forEach(src::sendMessage);
      }
    }

    return CommandResult.success();
  }

  private Text getTextFromEnum(Class e) {
    Text.Builder builder = Text.builder();
    Iterator it = Iterators.forArray(e.getEnumConstants());
    while (it.hasNext()) {
      builder.append(Text.of(it.next()));
      if (it.hasNext()) {
        builder.append(Text.of(", "));
      }
    }

    return builder.build();
  }
}
