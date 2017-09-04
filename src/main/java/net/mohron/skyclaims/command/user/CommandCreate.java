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

package net.mohron.skyclaims.command.user;

import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.command.CommandIsland;
import net.mohron.skyclaims.command.argument.Argument;
import net.mohron.skyclaims.command.argument.SchematicArgument;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.permissions.Permissions;
import net.mohron.skyclaims.world.Island;
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

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public class CommandCreate extends CommandBase.PlayerCommand {

    public static final String HELP_TEXT = "create an island.";
    private static final Text SCHEMATIC = Text.of("schematic");

    public static CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_CREATE)
        .description(Text.of(HELP_TEXT))
        .arguments(GenericArguments.optional(Argument.schematic(SCHEMATIC)))
        .executor(new CommandCreate())
        .build();

    public static void register() {
        try {
            CommandIsland.addSubCommand(commandSpec, "create");
            PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
            PLUGIN.getLogger().debug("Registered command: CommandCreate");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            PLUGIN.getLogger().error("Failed to register command: CommandCreate");
        }
    }

    @Override public CommandResult execute(@Nonnull Player player, @Nonnull CommandContext args) throws CommandException {
        if (Island.hasIsland(player.getUniqueId())) {
            throw new CommandException(Text.of(TextColors.RED, "You already have an island!"));
        }

        Optional<String> schematic = args.getOne(SCHEMATIC);
        if (schematic.isPresent()) {
            return createIsland(player, schematic.get());
        } else if (PLUGIN.getConfig().getWorldConfig().isListSchematics()) {
            return listSchematics(player);
        } else {
            return createIsland(player, Options.getDefaultSchematic(player.getUniqueId()));
        }
    }

    private CommandResult createIsland(Player player, String schematic) throws CommandException {
        player.sendMessage(Text.of(TextColors.GREEN, "Your island is being created. You will be teleported shortly."));

        try {
            new Island(player, schematic);
            return CommandResult.success();
        } catch (CreateIslandException e) {
            throw new CommandException(Text.of(TextColors.RED, "Unable to create island!", Text.NEW_LINE, TextColors.RESET, e.getMessage()));
        }
    }

    private CommandResult listSchematics(Player player) {
        boolean checkPerms = PLUGIN.getConfig().getPermissionConfig().isSeparateSchematicPerms();
        List<Text> schematics = SchematicArgument.SCHEMATICS.keySet().stream()
            .filter(s -> !checkPerms || player.hasPermission(Permissions.COMMAND_ARGUMENTS_SCHEMATICS + "." + s.toLowerCase()))
            .map(s -> Text.builder(s).onClick(TextActions.executeCallback(createIsland(s))).build())
            .collect(Collectors.toList());
        PaginationList.builder()
            .title(Text.of(TextColors.AQUA, "Starter Islands"))
            .padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
            .contents(schematics)
            .sendTo(player);
        return CommandResult.empty();
    }

    private Consumer<CommandSource> createIsland(String s) {
        return src -> {
            if (src instanceof Player) {
                Player player = (Player) src;
                if (Island.hasIsland(player.getUniqueId())) {
                    player.sendMessage(Text.of(TextColors.RED, "You already have an island!"));
                    return;
                }
                try {
                    player.sendMessage(Text.of(TextColors.GREEN, "Your island is being created. You will be teleported shortly."));
                    new Island(player, s);
                } catch (CreateIslandException e) {
                    player.sendMessage(Text.of(TextColors.RED, "Unable to create island!", Text.NEW_LINE, TextColors.RESET, e.getMessage()));
                }
            }
        };
    }
}
