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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public class CommandReset extends CommandBase.PlayerCommand {

    public static final String HELP_TEXT = "reset your island and inventory so you can start over.";
    private static final Text SCHEMATIC = Text.of("schematic");

    public static CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_RESET)
        .description(Text.of(HELP_TEXT))
        .arguments(GenericArguments.optional(Argument.schematic(SCHEMATIC)))
        .executor(new CommandReset())
        .build();

    public static void register() {
        try {
            CommandIsland.addSubCommand(commandSpec, "reset");
            PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
            PLUGIN.getLogger().debug("Registered command: CommandReset");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            PLUGIN.getLogger().error("Failed to register command: CommandReset");
        }
    }

    @Override public CommandResult execute(@Nonnull Player player, @Nonnull CommandContext args) throws CommandException {
        Island island = Island.getByOwner(player.getUniqueId())
            .orElseThrow(() -> new CommandException(Text.of("You must have an island to run this command!")));

        Optional<String> schematic = args.getOne(SCHEMATIC);
        if (schematic.isPresent()) {
            getConfirmation(island, schematic.get()).accept(player);
        } else if (PLUGIN.getConfig().getMiscConfig().isListSchematics()) {
            listSchematics(player, island);
        } else {
            getConfirmation(island, Options.getDefaultSchematic(player.getUniqueId())).accept(player);
        }

        return CommandResult.empty();
    }

    private void listSchematics(Player player, Island island) {
        boolean checkPerms = PLUGIN.getConfig().getPermissionConfig().isSeparateSchematicPerms();
        List<Text> schematics = SchematicArgument.SCHEMATICS.keySet().stream()
            .filter(s -> !checkPerms || player.hasPermission(Permissions.COMMAND_ARGUMENTS_SCHEMATICS + "." + s.toLowerCase()))
            .map(s -> Text.builder(s).onClick(TextActions.executeCallback(getConfirmation(island, s))).build())
            .collect(Collectors.toList());
        PaginationList.builder()
            .title(Text.of(TextColors.AQUA, "Starter Islands"))
            .padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
            .contents(schematics)
            .sendTo(player);
    }

    private Consumer<CommandSource> getConfirmation(Island island, String schematic) {
        return src -> {
            if (src instanceof Player) {
                Player player = (Player) src;
                player.sendMessage(Text.of(
                    "Are you sure you want to reset your island and inventory? This cannot be undone!", Text.NEW_LINE,
                    TextColors.GOLD, "Do you want to continue?", Text.NEW_LINE,
                    TextColors.WHITE, "[",
                    Text.builder("YES")
                        .color(TextColors.GREEN)
                        .onClick(TextActions.executeCallback(resetIsland(player, island, schematic))),
                    TextColors.WHITE, "] [",
                    Text.builder("NO")
                        .color(TextColors.RED)
                        .onClick(TextActions.executeCallback(s -> s.sendMessage(Text.of("Island reset canceled!")))),
                    TextColors.WHITE, "]"
                ));
            }
        };
    }

    private Consumer<CommandSource> resetIsland(Player player, Island island, String schematic) {
        return src -> {
            player.getEnderChestInventory().clear();
            player.getInventory().clear();

            // Teleport any players located in the island's region to spawn
            island.getPlayers().forEach(p -> p.setLocationSafely(PLUGIN.getConfig().getWorldConfig().getSpawn()));

            player.sendMessage(Text.of("Please be patient while your island is reset."));
            island.reset(schematic);
        };
    }
}
