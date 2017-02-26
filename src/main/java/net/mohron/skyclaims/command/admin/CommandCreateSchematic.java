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

package net.mohron.skyclaims.command.admin;

import com.flowpowered.math.vector.Vector3i;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.command.argument.SchematicArgument;
import net.mohron.skyclaims.listener.SchematicHandler;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;
import org.spongepowered.api.world.schematic.Schematic;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

import static org.spongepowered.api.command.args.GenericArguments.string;

public class CommandCreateSchematic implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final String HELP_TEXT = "used to save the selected area as an island schematic";
	private static final Text NAME = Text.of("name");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_CREATE_SCHEMATIC)
		.description(Text.of(HELP_TEXT))
		.arguments(string(NAME))
		.executor(new CommandCreateSchematic())
		.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandCreateSchematic");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandCreateSchematic");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.RED, "You must be a player to use this command."));
			return CommandResult.success();
		}
		Player player = (Player) src;
		SchematicHandler.PlayerData data = SchematicHandler.get(player);
		if (data.getPos1() == null || data.getPos2() == null) {
			player.sendMessage(Text.of(TextColors.RED, "You must set both positions before copying"));
			return CommandResult.success();
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
			.paletteType(BlockPaletteTypes.LOCAL)
			.build();
		DataContainer schematicData = DataTranslators.SCHEMATIC.translate(schematic);

		File outputFile = new File(PLUGIN.getConfigDir().toFile(), String.format("schematics%s%s.schematic", File.separator, name.toLowerCase()));
		try {
			DataFormats.NBT.writeTo(new GZIPOutputStream(new FileOutputStream(outputFile)), schematicData);
			player.sendMessage(Text.of(TextColors.GREEN, "Saved schematic to " + outputFile.getAbsolutePath()));
			SchematicArgument.SCHEMATICS.put(name.toLowerCase(), name);
		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(Text.of(TextColors.DARK_RED, "Error saving schematic: " + e.getMessage()));
			return CommandResult.success();
		}
		return CommandResult.success();
	}
}