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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.mohron.skyclaims.PluginInfo;
import net.mohron.skyclaims.command.team.CommandDemote;
import net.mohron.skyclaims.command.team.CommandInvite;
import net.mohron.skyclaims.command.team.CommandKick;
import net.mohron.skyclaims.command.team.CommandLeave;
import net.mohron.skyclaims.command.team.CommandPromote;
import net.mohron.skyclaims.command.user.CommandCreate;
import net.mohron.skyclaims.command.user.CommandExpand;
import net.mohron.skyclaims.command.user.CommandInfo;
import net.mohron.skyclaims.command.user.CommandList;
import net.mohron.skyclaims.command.user.CommandLock;
import net.mohron.skyclaims.command.user.CommandReset;
import net.mohron.skyclaims.command.user.CommandSetBiome;
import net.mohron.skyclaims.command.user.CommandSetSpawn;
import net.mohron.skyclaims.command.user.CommandSpawn;
import net.mohron.skyclaims.command.user.CommandUnlock;
import net.mohron.skyclaims.integration.nucleus.CommandHome;
import net.mohron.skyclaims.integration.nucleus.CommandSetHome;
import net.mohron.skyclaims.permissions.Permissions;
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
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@NonnullByDefault
public class CommandIsland extends CommandBase {

    private static final String HELP_TEXT = String.format("use to run %s's subcommands or display command help info.", PluginInfo.NAME);

    private static final Text HELP = Text.of("help");
    private static final Map<List<String>, CommandCallable> children = Maps.newHashMap();

    public static void register() {
        registerSubCommands();

        CommandSpec commandSpec = CommandSpec.builder()
            .description(Text.of(HELP_TEXT))
            .children(children)
            .arguments(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.literal(HELP, "help"))))
            .executor(new CommandIsland())
            .build();

        try {
            Sponge.getCommandManager().register(PLUGIN, commandSpec, "island", "is");
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
        CommandExpand.register();
        CommandDemote.register();
        CommandInfo.register();
        CommandInvite.register();
        CommandKick.register();
        CommandLeave.register();
        CommandList.register();
        CommandLock.register();
        CommandPromote.register();
        CommandReset.register();
        CommandSetBiome.register();
        CommandSetSpawn.register();
        CommandSpawn.register();
        CommandUnlock.register();
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> helpText = Lists.newArrayList();

        URL gpHelp = null;
        try {
            gpHelp = new URL("http://bit.ly/mcgpuser");
        } catch (MalformedURLException ignored) {
        }
        Text gpInfo = Text.of(
            TextColors.WHITE, "SkyClaims uses GriefPrevention to provide island protection. Learn more at ",
            Text.builder("http://bit.ly/mcgpuser")
                .color(TextColors.YELLOW)
                .onHover(TextActions.showText(Text.of("Click to open")))
                .onClick(gpHelp != null ? TextActions.openUrl(gpHelp) : null),
            TextColors.WHITE, "."
        );

        if (src.hasPermission(Permissions.COMMAND_CREATE)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is create").onClick(TextActions.runCommand("/is create")),
                TextColors.GRAY, " [schematic]",
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandCreate.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_EXPAND)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is expand").onClick(TextActions.suggestCommand("/is expand ")),
                TextColors.GRAY, " [island]",
                TextColors.GRAY, " <blocks>",
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandExpand.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_DEMOTE)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is demote").onClick(TextActions.suggestCommand("/is demote ")),
                TextColors.GOLD, " <user>",
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandDemote.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_HOME)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is home").onClick(TextActions.runCommand("/is home")),
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandHome.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_INFO)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is info").onClick(TextActions.runCommand("/is info")),
                TextColors.GRAY, " [island]",
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandInfo.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_INVITE)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is invite").onClick(TextActions.runCommand("/is invite")),
                TextColors.GRAY, " [user]",
                TextColors.GRAY, " [privilege]",
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandInvite.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_KICK)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is kick").onClick(TextActions.suggestCommand("/is kick")),
                TextColors.GOLD, " <user>",
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandKick.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_LEAVE)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is leave").onClick(TextActions.runCommand("/is leave")),
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandLeave.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_LIST)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is list").onClick(TextActions.runCommand("/is list")),
                TextColors.GRAY, " [user]",
                TextColors.GRAY, Text.builder(" [sort]").onHover(TextActions.showText(getSortOptions())),
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandList.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_LOCK)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is lock").onClick(TextActions.runCommand("/is lock")),
                TextColors.GRAY, (src.hasPermission(Permissions.COMMAND_LOCK_OTHERS)) ? " [island|all]" : Text.EMPTY,
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandLock.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_PROMOTE)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is promote").onClick(TextActions.suggestCommand("/is promote ")),
                TextColors.GOLD, " <user>",
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandPromote.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_RESET)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is reset").onClick(TextActions.suggestCommand("/is reset")),
                TextColors.GRAY, " [schematic]",
                TextColors.GRAY, src.hasPermission(Permissions.COMMAND_RESET_KEEP_INV) ? " [keepinv]" : Text.EMPTY,
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandReset.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_SET_BIOME)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is setbiome").onClick(TextActions.suggestCommand("/is setbiome ")),
                TextColors.GOLD, " <biome>",
                TextColors.GRAY, " [target]",
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandSetBiome.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_SET_HOME)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is sethome").onClick(TextActions.runCommand("/is sethome")),
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandSetHome.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_SET_SPAWN)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is setspawn").onClick(TextActions.runCommand("/is setspawn")),
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandSetSpawn.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_SPAWN)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is spawn").onClick(TextActions.runCommand("/is spawn")),
                TextColors.GRAY, " [player]",
                TextColors.DARK_GRAY, " - ",
                TextColors.DARK_GREEN, CommandSpawn.HELP_TEXT
            ));
        }

        if (src.hasPermission(Permissions.COMMAND_LOCK)) {
            helpText.add(Text.of(
                TextColors.AQUA, Text.builder("is unlock").onClick(TextActions.runCommand("/is unlock")),
                TextColors.GRAY, (src.hasPermission(Permissions.COMMAND_LOCK_OTHERS)) ? " [island|all]" : Text.EMPTY,
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

    private Text getSortOptions() {
        return Text.of(
            TextColors.GREEN, "ascending ", TextColors.RED, "descending", Text.NEW_LINE,
            TextColors.GREEN, "newest ", TextColors.RED, "oldest", Text.NEW_LINE,
            TextColors.GREEN, "active ", TextColors.RED, "inactive", Text.NEW_LINE,
            TextColors.GREEN, "team+ ", TextColors.RED, "team-", Text.NEW_LINE,
            TextColors.GREEN, "largest ", TextColors.RED, "smallest", Text.NEW_LINE,
            TextColors.GREEN, "entities+ ", TextColors.RED, "entities-", Text.NEW_LINE,
            TextColors.GREEN, "tile+ ", TextColors.RED, "tile-", Text.NEW_LINE
        );
    }
}
