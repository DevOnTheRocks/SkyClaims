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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.Consumer;

public class CommandRegen extends CommandBase {

    public static final String HELP_TEXT = "regenerate your island using a schematic.";
    private static final Text SCHEMATIC = Text.of("schematic");

    public static CommandSpec commandSpec = CommandSpec.builder()
        .permission(Permissions.COMMAND_REGEN)
        .description(Text.of(HELP_TEXT))
        .arguments(GenericArguments.optional(Argument.schematic(SCHEMATIC)))
        .executor(new CommandRegen())
        .build();

    public static void register() {
        try {
            CommandIsland.addSubCommand(commandSpec, "regen");
            PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
            PLUGIN.getLogger().debug("Registered command: CommandRegen");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            PLUGIN.getLogger().error("Failed to register command: CommandRegen");
        }
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("You must be a player to run this command!"));
        }
        Player player = (Player) src;
        Island island = Island.getByOwner(player.getUniqueId())
            .orElseThrow(() -> new CommandException(Text.of("You must have an island to run this command!")));
        String schematic = args.<String>getOne(SCHEMATIC).orElse(Options.getDefaultSchematic(player.getUniqueId()));

        player.sendMessage(Text.of(
            "Are you sure you want to regenerate your island? This cannot be undone!", Text.NEW_LINE,
            TextColors.GOLD, "Do you want to continue?", Text.NEW_LINE,
            TextColors.WHITE, "[",
            Text.builder("YES").color(TextColors.GREEN).onClick(TextActions.executeCallback(regen(island, schematic))),
            TextColors.WHITE, "] [",
            Text.builder("NO").color(TextColors.RED).onClick(TextActions.executeCallback(s -> s.sendMessage(Text.of("Island regen canceled!")))),
            TextColors.WHITE, "]"
        ));

        return CommandResult.success();
    }

    private Consumer<CommandSource> regen(Island island, String schematic) {
        return src -> {
            // Teleport any players located in the island's region to spawn
            Location<World> spawn = PLUGIN.getConfig().getWorldConfig().getWorld().getSpawnLocation();
            island.getPlayers().forEach(p -> p.setLocationSafely(spawn));

            src.sendMessage(Text.of("Please be patient while your island is reset."));
            island.regen(schematic);
        };
    }
}
