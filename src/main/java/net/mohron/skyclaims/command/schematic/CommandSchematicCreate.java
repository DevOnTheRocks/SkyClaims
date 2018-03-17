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

package net.mohron.skyclaims.command.schematic;

import static org.spongepowered.api.command.args.GenericArguments.string;

import com.flowpowered.math.vector.Vector3i;
import java.time.Instant;
import net.mohron.skyclaims.command.CommandBase;
import net.mohron.skyclaims.listener.SchematicHandler;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;
import org.spongepowered.api.world.schematic.Schematic;

@NonnullByDefault
public class CommandSchematicCreate extends CommandBase.PlayerCommand {

  public static final String HELP_TEXT = "used to save the selected area as an island schematic";
  private static final Text NAME = Text.of("name");

  public static CommandSpec commandSpec = CommandSpec.builder()
      .permission(Permissions.COMMAND_SCHEMATIC_CREATE)
      .description(Text.of(HELP_TEXT))
      .arguments(string(NAME))
      .executor(new CommandSchematicCreate())
      .build();

  public static void register() {
    try {
      PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
      PLUGIN.getLogger().debug("Registered command: CommandCreateSchematic");
    } catch (UnsupportedOperationException e) {
      PLUGIN.getLogger().error("Failed to register command: CommandCreateSchematic", e);
    }
  }

  @Override
  public CommandResult execute(Player player, CommandContext args) throws CommandException {
    SchematicHandler.PlayerData data = SchematicHandler.get(player);
    if (data.getPos1() == null || data.getPos2() == null) {
      throw new CommandException(Text.of(TextColors.RED, "You must set both positions before copying."));
    }
    Vector3i min = data.getPos1().min(data.getPos2());
    Vector3i max = data.getPos1().max(data.getPos2());
    ArchetypeVolume volume = player.getWorld().createArchetypeVolume(min, max, player.getLocation().getPosition().toInt());

    String name = args.<String>getOne(NAME)
        .orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must supply a name to use this command!")));

    Schematic schematic = Schematic.builder()
        .volume(volume)
        .metaValue(Schematic.METADATA_AUTHOR, player.getName())
        .metaValue(Schematic.METADATA_NAME, name)
        .metaValue(Schematic.METADATA_DATE, Instant.now().toString())
        .paletteType(BlockPaletteTypes.LOCAL)
        .build();

    if (PLUGIN.getSchematicManager().create(schematic)) {
      player.sendMessage(Text.of(TextColors.GREEN, "Successfully created schematic."));
      return CommandResult.success();
    } else {
      throw new CommandException(Text.of(TextColors.RED, "Error saving schematic!"));
    }
  }
}
