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

import com.google.common.collect.Lists;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.permissions.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;

public class CommandConfig implements CommandExecutor {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	public static final String HELP_TEXT = "used to view loaded config settings.";

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_CONFIG)
		.description(Text.of(HELP_TEXT))
		.executor(new CommandConfig())
		.build();

	public static void register() {
		try {
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandConfig");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandConfig");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		GlobalConfig config = PLUGIN.getConfig();
		List<Text> configText = Lists.newArrayList();

		configText.add(Text.of(TextColors.BLUE, "Misc", TextColors.WHITE, " | ", TextColors.YELLOW, "Island-on-Join", TextColors.WHITE, " : ", TextColors.GRAY, config.getMiscConfig().createIslandOnJoin()));
		configText.add(Text.of(TextColors.BLUE, "Permission", TextColors.WHITE, " | ", TextColors.YELLOW, "Separate-BiomeType-Permissions", TextColors.WHITE, " : ", TextColors.GRAY, config.getPermissionConfig().isSeparateBiomePerms()));
		configText.add(Text.of(TextColors.BLUE, "Permission", TextColors.WHITE, " | ", TextColors.YELLOW, "Separate-Schematics-Permissions", TextColors.WHITE, " : ", TextColors.GRAY, config.getPermissionConfig().isSeparateSchematicPerms()));
		configText.add(Text.of(TextColors.BLUE, "Permission", TextColors.WHITE, " | ", TextColors.YELLOW, "Separate-Target-Permissions", TextColors.WHITE, " : ", TextColors.GRAY, config.getPermissionConfig().isSeparateTargetPerms()));
		configText.add(Text.of(TextColors.BLUE, "Storage", TextColors.WHITE, " | ", TextColors.YELLOW, "Location", TextColors.WHITE, " : ", TextColors.GRAY, config.getStorageConfig().getLocation()));
		configText.add(Text.of(TextColors.BLUE, "Storage", TextColors.WHITE, " | ", TextColors.YELLOW, "Type", TextColors.WHITE, " : ", TextColors.GRAY, config.getStorageConfig().getType()));
		configText.add(Text.of(TextColors.BLUE, "World", TextColors.WHITE, " | ", TextColors.YELLOW, "Island-Height", TextColors.WHITE, " : ", TextColors.GRAY, config.getWorldConfig().getDefaultHeight()));
		configText.add(Text.of(TextColors.BLUE, "World", TextColors.WHITE, " | ", TextColors.YELLOW, "SkyClaims-World", TextColors.WHITE, " : ", TextColors.GRAY, config.getWorldConfig().getWorld().getName()));
		configText.add(Text.of(TextColors.BLUE, "World", TextColors.WHITE, " | ", TextColors.YELLOW, "Spawn-Regions", TextColors.WHITE, " : ", TextColors.GRAY, config.getWorldConfig().getSpawnRegions()));
		configText.add(Text.of(TextColors.BLUE, "Options", TextColors.WHITE, " | ", TextColors.YELLOW, "default-schematic", TextColors.WHITE, " : ", TextColors.GRAY, config.getOptionsConfig().getSchematic()));
		configText.add(Text.of(TextColors.BLUE, "Options", TextColors.WHITE, " | ", TextColors.YELLOW, "default-biome", TextColors.WHITE, " : ", TextColors.GRAY, config.getOptionsConfig().getBiome()));
		configText.add(Text.of(TextColors.BLUE, "Options", TextColors.WHITE, " | ", TextColors.YELLOW, "min-size", TextColors.WHITE, " : ", TextColors.GRAY, config.getOptionsConfig().getMinSize()));
		configText.add(Text.of(TextColors.BLUE, "Options", TextColors.WHITE, " | ", TextColors.YELLOW, "max-size", TextColors.WHITE, " : ", TextColors.GRAY, config.getOptionsConfig().getMaxSize()));

		PaginationList.builder()
			.title(Text.of(TextColors.AQUA, "SkyClaims Config"))
			.padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
			.contents(configText)
			.sendTo(src);

		return CommandResult.success();
	}
}